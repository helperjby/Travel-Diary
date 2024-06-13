package com.traveldiary.be.controller;

import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    /**
     * 사용자의 모든 일기 조회
     *
     * @param providerId 사용자 소셜 아이디
     * @return 사용자의 모든 일기 리스트
     */
    @GetMapping
    public ResponseEntity<List<WritingDiary>> getUserDiaries(@RequestParam String providerId) {
        try {
            List<WritingDiary> diaries = diaryService.getUserDiaries(providerId);
            return ResponseEntity.ok(diaries);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);  // 예외 발생 시 에러 처리
        }
    }

    /**
     * 특정 일기 조회
     *
     * @param diaryId 일기 ID
     * @param providerId 사용자 소셜 아이디
     * @return 조회된 일기 엔터티
     */
    @GetMapping("/{diaryId}")
    public ResponseEntity<?> getDiaryById(@PathVariable Integer diaryId, @RequestParam String providerId) {
        try {
            WritingDiary diary = diaryService.getDiaryById(diaryId, providerId);
            return ResponseEntity.ok(diary);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());  // 예외 발생 시 에러 메시지 반환
        }
    }

    /**
     * 특정 날짜의 일기 조회
     *
     * @param date 조회할 날짜
     * @param providerId 사용자 소셜 아이디
     * @return 특정 날짜의 일기 리스트
     */
    @GetMapping("/date")
    public ResponseEntity<List<WritingDiary>> getDiariesByDate(@RequestParam LocalDate date, @RequestParam String providerId) {
        try {
            List<WritingDiary> diaries = diaryService.getDiariesByDate(date, providerId);
            return ResponseEntity.ok(diaries);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);  // 예외 발생 시 에러 처리
        }
    }

    /**
     * 특정 여행 기간의 일기 조회
     *
     * @param startDate 여행 시작 날짜
     * @param endDate 여행 종료 날짜
     * @param providerId 사용자 소셜 아이디
     * @return 특정 여행 기간의 일기 리스트
     */
    @GetMapping("/period")
    public ResponseEntity<List<WritingDiary>> getDiariesByPeriod(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate, @RequestParam String providerId) {
        try {
            List<WritingDiary> diaries = diaryService.getDiariesByPeriod(startDate, endDate, providerId);
            return ResponseEntity.ok(diaries);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);  // 예외 발생 시 에러 처리
        }
    }

    /**
     * 공개된 모든 일기 조회
     *
     * @return 공개된 일기 리스트
     */
    @GetMapping("/public")
    public ResponseEntity<List<WritingDiary>> getPublicDiaries() {
        try {
            List<WritingDiary> diaries = diaryService.getPublicDiaries();
            return ResponseEntity.ok(diaries);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);  // 예외 발생 시 에러 처리
        }
    }
}
