package com.traveldiary.be.repository;

import com.traveldiary.be.entity.Like;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.WritingDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    Optional<Like> findByUserAndWritingDiary(Users user, WritingDiary writingDiary); // 사용자와 일기 기준으로 좋아요 찾기
    long countByWritingDiary(WritingDiary writingDiary); // 특정 일기의 좋아요 수를 계산
}
