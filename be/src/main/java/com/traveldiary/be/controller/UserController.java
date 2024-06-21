package com.traveldiary.be.controller;


import com.traveldiary.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.traveldiary.be.dto.UpdateNicknameRequestDto;
import com.traveldiary.be.dto.UpdateBioRequestDto;
import com.traveldiary.be.entity.Users;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}/nickname")
    public ResponseEntity<Users> updateNickname(@PathVariable Integer id, @RequestBody UpdateNicknameRequestDto updateNicknameRequestDto) {
        Users updatedUser = userService.updateNickname(id, updateNicknameRequestDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/profileImage")
    public ResponseEntity<Users> updateProfileImage(@PathVariable Integer id, @RequestParam("profileImage") MultipartFile profileImageFile) throws IOException {
        Users updatedUser = userService.updateProfileImage(id, profileImageFile);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/bio")
    public ResponseEntity<Users> updateBio(@PathVariable Integer id, @RequestBody UpdateBioRequestDto updateBioRequestDto) {
        Users updatedUser = userService.updateBio(id, updateBioRequestDto);
        return ResponseEntity.ok(updatedUser);
    }
}
