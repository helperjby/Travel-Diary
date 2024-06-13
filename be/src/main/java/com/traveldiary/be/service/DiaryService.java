package com.traveldiary.be.service;

import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.repository.WritingRepository;
import com.traveldiary.be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiaryService {

    private static final Logger logger = LoggerFactory.getLogger(DiaryService.class);

    @Autowired
    private WritingRepository writingRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 사용자의 모든 일기 조회 메서드
     *
     * @param providerId 사용자 소셜 아이디
     * @return 사용자의 모든 일기 리스트
     */
    public List<WritingDiary> getUserDiaries(String providerId) {
        // 사용자 소셜 아이디로 사용자 조회
        Users user = userRepository.findByProviderId(providerId).orElseThrow(() -> new RuntimeException("User not found"));
        List<WritingDiary> diaries = writingRepository.findByUser(user);
        logger.info("Fetched diaries for user: {}", providerId);
        return diaries;
    }

    /**
     * 특정 일기 조회 메서드
     *
     * @param diaryId 일기 ID
     * @param providerId 사용자 소셜 아이디
     * @return 조회된 일기 엔터티
     */
    public WritingDiary getDiaryById(Integer diaryId, String providerId) {
        logger.info("Fetching user with providerId: {}", providerId);
        // 소셜 아이디로 사용자 조회
        Users user = userRepository.findByProviderId(providerId).orElseThrow(() -> new RuntimeException("User not found"));
        logger.info("User found: {}", user);

        logger.info("Fetching diary with ID: {}", diaryId);
        // 일기 ID로 일기 조회
        WritingDiary diary = writingRepository.findById(diaryId).orElseThrow(() -> new RuntimeException("Diary not found"));
        logger.info("Diary found: {}", diary);

        // 사용자가 일기의 소유자인지 확인
        if (!diary.getUser().equals(user)) {
            logger.warn("Unauthorized access attempt by user: {}", user);
            throw new RuntimeException("Unauthorized");
        }

        logger.info("Fetched diary by ID: {} for user: {}", diaryId, providerId);
        return diary;
    }

    /**
     * 특정 날짜의 일기 조회 메서드
     *
     * @param date 특정 날짜
     * @param providerId 사용자 소셜 아이디
     * @return 특정 날짜의 일기 리스트
     */
    public List<WritingDiary> getDiariesByDate(LocalDate date, String providerId) {
        // 사용자 소셜 아이디로 사용자 조회
        Users user = userRepository.findByProviderId(providerId).orElseThrow(() -> new RuntimeException("User not found"));

        // 특정 날짜와 사용자로 일기 조회
        List<WritingDiary> diaries = writingRepository.findByTravelDateAndUser(date, user);
        logger.info("Fetched diaries for date: {} for user: {}", date, providerId);
        return diaries;
    }

    /**
     * 특정 여행 기간의 일기 조회 메서드
     *
     * @param startDate 여행 시작 날짜
     * @param endDate 여행 종료 날짜
     * @param providerId 사용자 소셜 아이디
     * @return 특정 여행 기간의 일기 리스트
     */
    public List<WritingDiary> getDiariesByPeriod(LocalDate startDate, LocalDate endDate, String providerId) {
        // 사용자 소셜 아이디로 사용자 조회
        Users user = userRepository.findByProviderId(providerId).orElseThrow(() -> new RuntimeException("User not found"));

        // 여행 기간과 사용자로 일기 조회
        List<WritingDiary> diaries = writingRepository.findByTravelDateBetweenAndUser(startDate, endDate, user);

        logger.info("Fetched diaries for period: {} - {} for user: {}", startDate, endDate, providerId);
        return diaries;
    }

    /**
     * 공개된 모든 일기 조회 메서드
     *
     * @return 공개된 일기 리스트
     */
    public List<WritingDiary> getPublicDiaries() {
        // 공개된 일기 조회
        List<WritingDiary> diaries = writingRepository.findByIsPublicTrue();
        logger.info("Fetched all public diaries");
        return diaries;
    }
}
