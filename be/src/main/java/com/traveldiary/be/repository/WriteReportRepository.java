package com.traveldiary.be.repository;

import com.traveldiary.be.entity.WriteReport;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WriteReportRepository extends JpaRepository<WriteReport, Integer> {

    // 특정 일기와 사용자로 신고가 존재하는지 확인
    boolean existsByWritingDiaryAndUser(WritingDiary diary, Users user);

    // 특정 일기에 대한 신고 수를 계산
    long countByWritingDiary(WritingDiary diary);

    // 특정 일기에 대한 isReported 상태를 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE WriteReport wr SET wr.isReported = :isReported WHERE wr.writingDiary = :diary")
    void updateIsReportedByWritingDiary(WritingDiary diary, boolean isReported);
}
