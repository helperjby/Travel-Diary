package com.traveldiary.be.controller;

import com.traveldiary.be.dto.WritingDTO;
import com.traveldiary.be.dto.WritingPhotoDTO;
import com.traveldiary.be.service.WritingService;
import com.traveldiary.be.service.WritingPhotoService;
import com.traveldiary.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/diaries")
public class WritingController {

    private final WritingService diaryService;
    private final WritingPhotoService writingPhotoService;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(WritingController.class);

    @Autowired
    public WritingController(WritingService diaryService, UserService userService, WritingPhotoService writingPhotoService) {
        this.diaryService = diaryService;
        this.userService = userService;
        this.writingPhotoService = writingPhotoService;
    }

    /**
     * 일기 작성 메서드
     *
     * @param userId 사용자 ID
     * @param diaryDto 일기 DTO
     * @return 작성된 일기 DTO
     */
    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<WritingDTO> createWriting(@RequestParam Integer userId, @RequestBody WritingDTO diaryDto) {
        try {
            logger.info("POST 요청 수신됨 - userId: {} 및 diaryDto: {}", userId, diaryDto);
            WritingDTO diary = diaryService.createDiary(diaryDto, userId);
            logger.info("일기 작성 성공 - ID: {}", diary.getId());
            return ResponseEntity.ok(diary);
        } catch (IllegalArgumentException e) {
            logger.error("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("예외 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 일기 수정 메서드
     *
     * @param diaryId 일기 ID
     * @param userId 사용자 ID
     * @param diaryDto 일기 DTO
     * @return 수정된 일기 DTO
     */
    @PutMapping(value = "/{diaryId}", consumes = {"application/json"})
    public ResponseEntity<WritingDTO> updateWriting(@PathVariable int diaryId, @RequestParam Integer userId, @RequestBody WritingDTO diaryDto) {
        try {
            logger.info("PUT 요청 수신됨 - diaryId: {}, userId: {} 및 diaryDto: {}", diaryId, userId, diaryDto);
            WritingDTO diary = diaryService.updateDiary(diaryId, diaryDto, userId);
            logger.info("일기 수정 성공 - ID: {}", diary.getId());
            return ResponseEntity.ok(diary);
        } catch (IllegalArgumentException e) {
            logger.error("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            logger.error("예외 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 일기 삭제 메서드
     *
     * @param diaryId 일기 ID
     * @param userId 사용자 ID
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<String> deleteWriting(@PathVariable int diaryId, @RequestParam Integer userId) {
        try {
            diaryService.deleteDiary(diaryId, userId);
            return ResponseEntity.ok("삭제가 완료됐습니다.");
        } catch (Exception e) {
            logger.error("예외 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("삭제 중 에러 발생");
        }
    }

    /**
     * 일기를 다른 앨범으로 이동하는 메서드
     *
     * @param diaryId 일기 ID
     * @param albumId 앨범 ID
     * @param userId 사용자 ID
     * @return 이동된 일기 DTO
     */
    @PutMapping("/{diaryId}/move-to-album/{albumId}")
    public ResponseEntity<WritingDTO> moveDiaryToAlbum(@PathVariable int diaryId, @PathVariable int albumId, @RequestParam Integer userId) {
        WritingDTO updatedDiary = diaryService.moveDiaryToAlbum(diaryId, albumId, userId);
        return ResponseEntity.ok(updatedDiary);
    }

    /**
     * 일기에 사진을 추가하는 메서드
     *
     * @param diaryId 일기 ID
     * @param userId 사용자 ID
     * @param photos 사진 파일 리스트
     * @return 추가된 사진 DTO 리스트
     */
    @PostMapping(value = "/{diaryId}/photos", consumes = "multipart/form-data")
    public ResponseEntity<List<WritingPhotoDTO>> addPhotosToDiary(@PathVariable int diaryId, @RequestParam("userId") Integer userId, @RequestPart("photos") List<MultipartFile> photos) {
        try {
            List<WritingPhotoDTO> photoDTOs = writingPhotoService.addPhotosToDiary(diaryId, userId, photos);
            return ResponseEntity.ok(photoDTOs);
        } catch (IOException e) {
            logger.error("사진 업로드 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * 일기에 있는 사진을 수정하는 메서드
     *
     * @param diaryId 일기 ID
     * @param userId 사용자 ID
     * @param photos 사진 파일 리스트
     * @return 수정된 사진 DTO 리스트
     */
    @PutMapping(value = "/{diaryId}/photos", consumes = "multipart/form-data")
    public ResponseEntity<List<WritingPhotoDTO>> updatePhotosInDiary(@PathVariable int diaryId, @RequestParam("userId") Integer userId, @RequestPart("photos") List<MultipartFile> photos) {
        try {
            List<WritingPhotoDTO> photoDTOs = writingPhotoService.updatePhotosInDiary(diaryId, userId, photos);
            return ResponseEntity.ok(photoDTOs);
        } catch (IOException e) {
            logger.error("사진 업로드 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
