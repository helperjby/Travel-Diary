package com.traveldiary.be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.traveldiary.be.entity.Like;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.WritingDiary;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    Optional<Like> findByUserAndWrite(Users user, WritingDiary write);//사용자와 일기 기준으로 좋아요 찾기
}
