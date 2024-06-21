package com.traveldiary.be.service;

import com.traveldiary.be.dto.UpdateNicknameRequestDto;
import com.traveldiary.be.dto.UpdateBioRequestDto;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository UserRepository;

    @Autowired
    public UserService(UserRepository UserRepository) {
        this.UserRepository = UserRepository;
    }

    public int getUserIdByProviderId(String providerId) {
        Optional<Users> optionalUser = UserRepository.findByProviderId(providerId);
        Users user = optionalUser.orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getId();
    }

    public Users updateNickname(Integer userId, UpdateNicknameRequestDto updateNicknameRequestDto) {
        Optional<Users> optionalUser = UserRepository.findById(userId);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            user.setNickname(updateNicknameRequestDto.getNickname());
            UserRepository.save(user);
            return user;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public Users updateProfileImage(Integer userId, MultipartFile profileImageFile) throws IOException {
        Optional<Users> optionalUser = UserRepository.findById(userId);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            String profileImagePath = saveProfileImage(profileImageFile); // 프로필 이미지를 저장하는 메서드
            user.setProfileImage(profileImagePath);
            UserRepository.save(user);
            return user;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public Users updateBio(Integer userId, UpdateBioRequestDto updateBioRequestDto) {
        Optional<Users> optionalUser = UserRepository.findById(userId);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            user.setBio(updateBioRequestDto.getBio());
            UserRepository.save(user);
            return user;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private String saveProfileImage(MultipartFile profileImageFile) throws IOException {
        // 저장 경로 설정
        String folderPath = "src/main/resources/images";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 파일 저장
        String originalFileName = profileImageFile.getOriginalFilename();
        String filePath = folderPath + File.separator + originalFileName;
        Path path = Paths.get(filePath);
        Files.write(path, profileImageFile.getBytes());

        return filePath;
    }
}
