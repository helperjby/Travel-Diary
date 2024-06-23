package com.traveldiary.be.service;

import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.WriteReport;
import com.traveldiary.be.repository.UserRepository;
import com.traveldiary.be.repository.WriteReportRepository;
import com.traveldiary.be.repository.WritingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class WriteReportService {

    private final WriteReportRepository writeReportRepository;
    private final UserRepository userRepository;
    private final WritingRepository writingRepository;

    @Autowired
    public WriteReportService(WriteReportRepository writeReportRepository, UserRepository userRepository, WritingRepository writingRepository) {
        this.writeReportRepository = writeReportRepository;
        this.userRepository = userRepository;
        this.writingRepository = writingRepository;
    }

    // 일기 신고
    @Transactional
    public boolean reportDiary(Integer diaryId, Integer userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));// 사용자 조회
        WritingDiary diary = writingRepository.findById(diaryId).orElseThrow(() -> new IllegalArgumentException("Invalid diary ID"));// 일기 조회

        // 중복 신고 확인
        if (writeReportRepository.existsByWritingDiaryAndUser(diary, user)) {
            return false; // 이미 신고한 경우
        }

        // 신고 저장
        WriteReport report = new WriteReport();
        report.setWritingDiary(diary);
        report.setUser(user);
        report.setDiaryReportCount(1);
        report.setReported(false); // 초기 상태는 false
        writeReportRepository.save(report);

        // 신고 횟수 확인 및 처리
        long reportCount = writeReportRepository.countByWritingDiary(diary);
        if (reportCount >= 15) {
            handleExcessiveReports(diary);
        }
        return true; // 신고가 성공적으로 이루어진 경우
    }

    public long getReportCount(Integer diaryId) {
        WritingDiary diary = writingRepository.findById(diaryId).orElseThrow(() -> new IllegalArgumentException("Invalid diary ID"));//일기조회
        return writeReportRepository.countByWritingDiary(diary);// 신고 횟수 반환
    }

    //신고 15회 누적 처리
    private void handleExcessiveReports(WritingDiary diary) {
        // 일기를 비공개로 전환
        diary.setIsPublic(false);
        writingRepository.save(diary);

        // 모든 신고를 isReported로 업데이트
        writeReportRepository.updateIsReportedByWritingDiary(diary, true);
    }
}
