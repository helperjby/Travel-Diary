package com.traveldiary.be.login.oauth2.handler;

import com.traveldiary.be.login.oauth2.Provider;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.repository.UserRepository;
import com.traveldiary.be.login.randomsrc.RandomUserInfoGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final RestTemplate restTemplate;

    private static final int TOKEN_EXPIRY_BUFFER_SECONDS = 300;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("로그인 성공!");

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();

        String clientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        var client = authorizedClientRepository.loadAuthorizedClient(clientRegistrationId, authentication, request);
        OAuth2AccessToken accessToken = client.getAccessToken();
        OAuth2RefreshToken refreshToken = client.getRefreshToken();

        // 토큰 갱신 로직 호출
        accessToken = refreshAccessTokenIfNecessary(client, accessToken, refreshToken, clientRegistrationId, authentication, request, response);

        logTokenInformation(accessToken, refreshToken);

        Provider provider = extractProviderFromRequest(request);
        String providerId = extractProviderIdFromOAuth2User(oAuth2User, provider);

        updateUserInformation(oAuth2User, provider, providerId, accessToken, refreshToken);

        String redirectUri = UriComponentsBuilder.fromUriString("http://localhost:8080/social")
                .queryParam("provider", provider)
                .queryParam("providerId", providerId)
                .build().toUriString();
        response.sendRedirect(redirectUri);
    }

    private Provider extractProviderFromRequest(HttpServletRequest request) {
        String[] path = request.getRequestURI().split("/");
        return Provider.valueOf(path[path.length - 1].toUpperCase());
    }

    private String extractProviderIdFromOAuth2User(OAuth2User oAuth2User, Provider provider) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerId = null;

        switch (provider) {
            case NAVER:
                providerId = (String) attributes.get("id");
                break;
            case GOOGLE:
                providerId = (String) attributes.get("sub");
                break;
            case KAKAO:
                providerId = String.valueOf(attributes.get("id"));
                break;
            default:
                throw new IllegalArgumentException("Unknown provider: " + provider);
        }

        return providerId;
    }



    private OAuth2AccessToken refreshAccessTokenIfNecessary(OAuth2AuthorizedClient client, OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken, String clientRegistrationId, Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        if (refreshToken != null && accessToken.getExpiresAt().isBefore(Instant.now().plusSeconds(TOKEN_EXPIRY_BUFFER_SECONDS))) {
            String tokenUri = client.getClientRegistration().getProviderDetails().getTokenUri();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(client.getClientRegistration().getClientId(), client.getClientRegistration().getClientSecret());

            Map<String, String> body = new HashMap<>();
            body.put("grant_type", "refresh_token");
            body.put("refresh_token", refreshToken.getTokenValue());

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> responseEntity = restTemplate.exchange(tokenUri, HttpMethod.POST, requestEntity, Map.class);

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = responseEntity.getBody();
                String newAccessToken = (String) responseBody.get("access_token");
                String newRefreshToken = (String) responseBody.get("refresh_token");

                accessToken = new OAuth2AccessToken(
                        OAuth2AccessToken.TokenType.BEARER,
                        newAccessToken,
                        Instant.now(),
                        Instant.now().plusSeconds(7200) // 2 hours
                );

                if (newRefreshToken != null) {
                    refreshToken = new OAuth2RefreshToken(
                            newRefreshToken,
                            Instant.now(),
                            Instant.now().plusSeconds(2592000) // 30 days
                    );
                }

                // 갱신된 토큰으로 인증 정보를 업데이트
                OAuth2AuthenticationToken newAuthentication = new OAuth2AuthenticationToken(
                        (OAuth2User) authentication.getPrincipal(),
                        authentication.getAuthorities(),
                        clientRegistrationId
                );

                SecurityContextHolder.getContext().setAuthentication(newAuthentication);

                // 새로운 토큰으로 Authorized Client 업데이트
                OAuth2AuthorizedClient updatedClient = new OAuth2AuthorizedClient(
                        client.getClientRegistration(),
                        authentication.getName(),
                        accessToken,
                        refreshToken
                );

                authorizedClientRepository.saveAuthorizedClient(updatedClient, authentication, request, response);

                log.info("새로운 Access Token: " + newAccessToken);
            } else {
                log.error("Failed to refresh access token, response code: {}", responseEntity.getStatusCode());
            }
        }
        return accessToken;
    }

    private void logTokenInformation(OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
        log.info("Access Token: " + accessToken.getTokenValue());
        if (refreshToken != null) {
            log.info("Refresh Token: " + refreshToken.getTokenValue());
        }
    }

    private void updateUserInformation(OAuth2User oAuth2User, Provider provider, String providerId, OAuth2AccessToken accessToken, OAuth2RefreshToken refreshToken) {
        String email = (String) oAuth2User.getAttributes().get("email");
        Optional<Users> userOptional = userRepository.findByEmail(email);

        Users user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            log.info("기존 사용자 업데이트: " + email);

            // 닉네임과 프로필 이미지를 업데이트하지 않음
            user.setName((String) oAuth2User.getAttributes().get("name"));
            user.setProvider(provider.name());
            user.setProviderId(providerId);
            user.setAccessToken(accessToken.getTokenValue());
            user.setRefreshToken(refreshToken != null ? refreshToken.getTokenValue() : null);
            user.setUpdatedAt(LocalDateTime.now());
        } else {
            log.info("새 사용자 생성: " + email);

            user = Users.builder()
                    .name((String) oAuth2User.getAttributes().get("name"))
                    .email(email)
                    .provider(provider.name())
                    .providerId(providerId)
                    .nickname(RandomUserInfoGenerator.getRandomNickname())
                    .profileImage(RandomUserInfoGenerator.getRandomProfileImage())
                    .accessToken(accessToken.getTokenValue())
                    .refreshToken(refreshToken != null ? refreshToken.getTokenValue() : null)
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        userRepository.save(user);
    }
}
