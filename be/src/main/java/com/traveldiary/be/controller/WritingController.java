package com.traveldiary.be.controller;

import com.traveldiary.be.dto.WritingDto;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.WritingPhoto;
import com.traveldiary.be.service.WritingPhotoService;
import com.traveldiary.be.service.WritingService;
import com.traveldiary.be.service.FileUploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/diaries")
public class WritingController {

    private final WritingService diaryService;
    private final WritingPhotoService writingPhotoService;

    @Autowired
    public WritingController(WritingService diaryService, WritingPhotoService writingPhotoService) {
        this.diaryService = diaryService;
        this.writingPhotoService = writingPhotoService;
    }

    /**
     * 새로운 일기 생성 메서드
     *
     * @param providerId 사용자 소셜 아이디
     * @param diaryDto 일기 데이터 전송 객체
     * @param photo 첨부된 사진 파일 리스트
     * @return 생성된 일기 엔터티와 함께 응답
     */
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<WritingDiary> createWriting(@RequestParam String providerId, @RequestPart("diaryDto") WritingDto diaryDto, @RequestPart(required = false) List<MultipartFile> photo) {
        try {
            WritingDiary diary = diaryService.createOrUpdateDiary(diaryDto, providerId);

            if (photo != null) {
                List<FileUploadResponse> photoInfos = photo.stream().map(file -> writingPhotoService.storeFile(file, diary)).collect(Collectors.toList());
                diary.setWritingPhotos(
                        photoInfos.stream()
                                .map(info -> {
                                    WritingPhoto writingPhoto = new WritingPhoto();
                                    writingPhoto.setPhoto(info.getUniqueFileName());
                                    writingPhoto.setDiary(diary);
                                    writingPhoto.setPhotoId(info.getPhotoId()); // photo_id 설정
                                    return writingPhoto;
                                })
                                .collect(Collectors.toList())
                );
            }

            return ResponseEntity.ok(diary);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * 기존 일기 업데이트 메서드
     *
     * @param diaryId 업데이트할 일기의 ID
     * @param providerId 사용자 소셜 아이디
     * @param diaryDto 업데이트할 일기 데이터 전송 객체
     * @param photo 첨부된 사진 파일 리스트
     * @return 업데이트된 일기 엔터티와 함께 응답
     */
    @PutMapping(value = "/{diaryId}", consumes = {"multipart/form-data"})
    public ResponseEntity<WritingDiary> updateWriting(@PathVariable int diaryId, @RequestParam String providerId, @RequestPart("diaryDto") WritingDto diaryDto, @RequestPart(required = false) List<MultipartFile> photo) {
        try {
            final WritingDiary diary = diaryService.findById(diaryId).orElseThrow(() -> new RuntimeException("Diary not found"));

            diaryService.updateDiary(diaryId, diaryDto, providerId);

            // 기존 사진 삭제
            if (diary.getWritingPhotos() != null) {
                for (WritingPhoto existingPhoto : diary.getWritingPhotos()) {
                    writingPhotoService.deleteFile(existingPhoto.getPhoto());
                    writingPhotoService.deletePhotoById(existingPhoto.getPhotoId());
                }
                diary.getWritingPhotos().clear();
            }

            // 새로운 사진 저장
            if (photo != null) {
                List<FileUploadResponse> photoInfos = photo.stream().map(file -> writingPhotoService.storeFile(file, diary)).toList();
                diary.setWritingPhotos(
                        photoInfos.stream()
                                .map(info -> {
                                    WritingPhoto writingPhoto = new WritingPhoto();
                                    writingPhoto.setPhoto(info.getUniqueFileName());
                                    writingPhoto.setDiary(diary);
                                    writingPhoto.setPhotoId(info.getPhotoId()); // photo_id 설정
                                    return writingPhoto;
                                })
                                .collect(Collectors.toList())
                );
            }

            WritingDiary updatedDiary = diaryService.findById(diaryId).orElseThrow(() -> new RuntimeException("Diary not found"));
            return ResponseEntity.ok(updatedDiary);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    /**
     * 일기 삭제 메서드
     *
     * @param diaryId 삭제할 일기의 ID
     * @param providerId 사용자 소셜 아이디
     * @return 삭제 요청에 대한 응답
     */
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteWriting(@PathVariable int diaryId, @RequestParam String providerId) {
        try {
            diaryService.deleteDiary(diaryId, providerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
