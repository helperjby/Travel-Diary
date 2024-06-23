package com.traveldiary.be.repository;

import com.traveldiary.be.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByProviderId(String providerId); // 소셜아이디로 사용자 조회
    Optional<Users> findByEmail(String providerId);
}