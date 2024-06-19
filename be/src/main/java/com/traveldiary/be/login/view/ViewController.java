package com.traveldiary.be.login.view;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Controller
public class ViewController {

    private final RestTemplate restTemplate;

    @Value("${NAVER_CLIENT_ID}")
    private String naverClientId;

    @Value("${NAVER_CLIENT_SECRET}")
    private String naverClientSecret;

    public ViewController(@Qualifier("appRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/oauth-login")
    public String socialSuccess(Model model,
                                @RequestParam(value = "provider", required = false) String provider,
                                @RequestParam(value = "providerId", required = false) String providerId
    ) {
        model.addAttribute("provider", provider);
        model.addAttribute("providerId", providerId);
        return "social-success";
    }

    @GetMapping("/logout-success")
    public String logoutSuccess() {
        return "logout-success";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
                         @RequestParam("provider") String provider, @RequestParam("token") String token) {
        if (authentication != null) {
            switch (provider.toLowerCase()) {
                case "kakao":
                    logoutFromKakao(token);
                    break;
                case "google":
                    logoutFromGoogle(token);
                    break;
                case "naver":
                    logoutFromNaver(token);
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 provider입니다: " + provider);
            }
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/logout-success";
    }

    private void logoutFromKakao(String token) {
        String requestUri = "https://kapi.kakao.com/v1/user/logout";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestUri, HttpMethod.POST, requestEntity, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println("카카오 로그아웃 성공");
        } else {
            System.err.println("카카오 로그아웃 실패: " + responseEntity.getStatusCode());
        }
    }

    private void logoutFromGoogle(String token) {
        String requestUri = "https://accounts.google.com/o/oauth2/revoke?token=" + token;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(requestUri, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println("구글 로그아웃 성공");
        } else {
            System.err.println("구글 로그아웃 실패: " + responseEntity.getStatusCode());
        }
    }

    private void logoutFromNaver(String token) {
        String requestUri = "https://nid.naver.com/oauth2.0/token?grant_type=delete";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", naverClientId);
        body.add("client_secret", naverClientSecret);
        body.add("access_token", token);
        body.add("service_provider", "NAVER");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestUri, HttpMethod.POST, requestEntity, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            System.out.println("네이버 로그아웃 성공");
        } else {
            System.err.println("네이버 로그아웃 실패: " + responseEntity.getStatusCode());
        }
    }
}
