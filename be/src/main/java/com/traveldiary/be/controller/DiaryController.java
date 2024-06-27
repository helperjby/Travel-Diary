package com.traveldiary.be.controller;

import com.traveldiary.be.dto.WritingDTO;
import com.traveldiary.be.dto.AlbumDTO;
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
     * @param userId 사용자 ID
     * @return 사용자의 모든 일기 리스트
     */
    @GetMapping
    public ResponseEntity<List<WritingDTO>> getUserDiaries(@RequestParam Integer userId) {
        try {
            List<WritingDTO> diaries = diaryService.getUserDiaries(userId);
            return ResponseEntity.ok(diaries);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    /**
     * 특정 사용자의 특정 앨범의 모든 일기 조회
     *
     * @param albumId 앨범 ID
     * @param userId 사용자 ID
     * @return 사용자의 모든 일기 리스트
     */
    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<WritingDTO>> getUserDiariesByAlbumId(@PathVariable Integer albumId, @RequestParam Integer userId) {
        try {
            List<WritingDTO> diaries = diaryService.getUserDiariesByAlbumId(albumId, userId);
            return ResponseEntity.ok(diaries);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }



    /**
     * 특정 일기 조회
     *
     * @param diaryId 일기 ID
     * @param userId 사용자 ID
     * @return 조회된 일기 엔터티
     */
    @GetMapping("/{diaryId}")
    public ResponseEntity<?> getDiaryById(@PathVariable Integer diaryId, @RequestParam Integer userId) {
        try {
            WritingDTO diary = diaryService.getDiaryById(diaryId, userId);
            return ResponseEntity.ok(diary);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 특정 날짜의 일기 조회
     *
     * @param date 특정 날짜
     * @param userId 사용자 ID
     * @return 특정 날짜의 일기 리스트
     */
    @GetMapping("/date")
    public ResponseEntity<List<WritingDTO>> getDiariesByDate(@RequestParam LocalDate date, @RequestParam Integer userId) {
        try {
            List<WritingDTO> diaries = diaryService.getDiariesByDate(date, userId);
            return ResponseEntity.ok(diaries);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * 특정 여행 기간의 일기 조회
     *
     * @param startDate 여행 시작 날짜
     * @param endDate 여행 종료 날짜
     * @param userId 사용자 ID
     * @return 특정 여행 기간의 일기 리스트
     */
    @GetMapping("/period")
    public ResponseEntity<List<WritingDTO>> getDiariesByPeriod(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate, @RequestParam Integer userId) {
        try {
            List<WritingDTO> diaries = diaryService.getDiariesByPeriod(startDate, endDate, userId);
            return ResponseEntity.ok(diaries);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * 공개된 모든 일기 조회
     *
     * @return 공개된 일기 리스트
     */
    @GetMapping("/public")
    public ResponseEntity<List<WritingDTO>> getPublicDiaries() {
        try {
            List<WritingDTO> diaries = diaryService.getPublicDiaries();
            return ResponseEntity.ok(diaries);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
