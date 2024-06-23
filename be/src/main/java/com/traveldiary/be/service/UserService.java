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

    private final UserRepository userRepository;

    // 생성자를 통해 UserRepository를 주입받음
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ID로 사용자 조회
    public Optional<Users> findById(int id) {
        return userRepository.findById(id);
    }

    // providerId로 사용자 ID를 조회
    public int getUserIdByProviderId(String providerId) {
        Optional<Users> optionalUser = userRepository.findByProviderId(providerId);
        Users user = optionalUser.orElseThrow(() -> new IllegalArgumentException("User not found")); // 사용자를 찾지 못할 경우 예외를 발생
        return user.getId();
    }

    public Users updateNickname(Integer userId, UpdateNicknameRequestDto updateNicknameRequestDto) {
        Optional<Users> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            user.setNickname(updateNicknameRequestDto.getNickname());

            userRepository.save(user);

            return user;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public Users updateProfileImage(Integer userId, MultipartFile profileImageFile) throws IOException {

        Optional<Users> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            String profileImagePath = saveProfileImage(profileImageFile); // 프로필 이미지를 저장하는 메서드
            user.setProfileImage(profileImagePath);

            userRepository.save(user);

            return user;
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public Users updateBio(Integer userId, UpdateBioRequestDto updateBioRequestDto) {

        Optional<Users> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            user.setBio(updateBioRequestDto.getBio());

            userRepository.save(user);

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

