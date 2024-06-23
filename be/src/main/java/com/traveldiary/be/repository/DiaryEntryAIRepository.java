package com.traveldiary.be.repository;

import com.traveldiary.be.entity.DiaryEntryAI;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.WritingDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryEntryAIRepository extends JpaRepository<DiaryEntryAI, Integer> {
    // 특정 사용자 ID로 AI 일기 항목 조회
    List<DiaryEntryAI> findByUserId(int userId);

    // 특정 WritingDiary 엔티티로 AI 일기 항목 조회
    List<DiaryEntryAI> findByWritingDiary(WritingDiary writingDiary);

    // 특정 WritingDiary ID로 AI 일기 항목 조회
    List<DiaryEntryAI> findByWritingDiaryId(int writingDiaryId);

    // 특정 사용자와 키워드에 대한 가장 최신의 AI 일기 항목 조회
    Optional<DiaryEntryAI> findTopByUserAndKeywordOrderByCreatedAtDesc(Users user, String keyword);

    // 특정 사용자와 키워드에 대한 질문과 답변을 생성일 기준으로 내림차순 정렬하여 조회
    @Query("SELECT d FROM DiaryEntryAI d WHERE d.user.id = :userId AND d.keyword = :keyword ORDER BY d.createdAt DESC")
    List<DiaryEntryAI> findUserResponsesByKeyword(@Param("userId") int userId, @Param("keyword") String keyword);

    // 특정 WritingDiary와 사용자로 AI 일기 항목 조회
    List<DiaryEntryAI> findByWritingDiaryAndUser(WritingDiary writingDiary, Users user);
}
