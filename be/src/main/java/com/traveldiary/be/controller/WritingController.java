package com.traveldiary.be.controller;

import com.traveldiary.be.dto.WritingDTO;
import com.traveldiary.be.service.WritingService;
import com.traveldiary.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/diaries")
public class WritingController {

    private final WritingService diaryService;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(WritingController.class);

    @Autowired
    public WritingController(WritingService diaryService, UserService userService) {
        this.diaryService = diaryService;
        this.userService = userService;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<WritingDTO> createWriting(
            @RequestParam Integer userId,
            @RequestPart("diaryDto") WritingDTO diaryDto,
            @RequestPart(required = false) List<MultipartFile> photo) {
        try {
            logger.info("POST 요청 수신됨 - userId: {} 및 diaryDto: {}", userId, diaryDto);

            if (diaryDto.getFinal_date() == null) {
                throw new IllegalArgumentException("최종 날짜는 null일 수 없습니다.");
            }

            WritingDTO diary = diaryService.createDiary(diaryDto, userId, photo);

            logger.info("일기 작성 성공 - ID: {}", diary.getId());
            return ResponseEntity.ok(diary);
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping(value = "/{diaryId}", consumes = {"multipart/form-data"})
    public ResponseEntity<WritingDTO> updateWriting(
            @PathVariable int diaryId,
            @RequestParam Integer userId,
            @RequestPart("diaryDto") WritingDTO diaryDto,
            @RequestPart(required = false) List<MultipartFile> photo) {
        try {
            logger.info("PUT 요청 수신됨 - diaryId: {}, userId: {} 및 diaryDto: {}", diaryId, userId, diaryDto);

            if (diaryDto.getFinal_date() == null) {
                throw new IllegalArgumentException("최종 날짜는 null일 수 없습니다.");
            }

            WritingDTO diary = diaryService.updateDiary(diaryId, diaryDto, userId, photo);

            logger.info("일기 수정 성공 - ID: {}", diary.getId());
            return ResponseEntity.ok(diary);
        } catch (IllegalArgumentException e) {
            logger.error("IllegalArgumentException: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteWriting(@PathVariable int diaryId, @RequestParam Integer userId) {
        try {
            diaryService.deleteDiary(diaryId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
}
