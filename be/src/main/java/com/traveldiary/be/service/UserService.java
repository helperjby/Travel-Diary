package com.traveldiary.be.service;

import com.traveldiary.be.entity.Users;
import com.traveldiary.be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int getUserIdByProviderId(String providerId) {
        Optional<Users> optionalUser = userRepository.findByProviderId(providerId);
        Users user = optionalUser.orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getId();
    }
}





