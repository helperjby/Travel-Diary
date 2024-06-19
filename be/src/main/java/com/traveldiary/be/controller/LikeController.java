package com.traveldiary.be.controller;

import com.traveldiary.be.dto.LikeDTO;
import com.traveldiary.be.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    /**
     * 사용자가 작성물에 좋아요 / 취소하는 요청 처리
     *
     * @param writeId 일기 ID
     * @param userId 사용자 ID
     * @return 업데이트된 좋아요 상태를 포함한 LikeDTO 객체
     */
    @PostMapping
    public ResponseEntity<LikeDTO> likeStatus(@RequestParam int writeId, @RequestParam int userId) {
        try {
            LikeDTO likeDTO = likeService.likeStatus(writeId, userId);
            return ResponseEntity.ok(likeDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     사용자가 특정 일기에 대해 좋아요 눌렀는지 여부 조회
     *
     * @param writeId 일기 ID
     * @param userId 사용자 ID
     * @return 현재 좋아요 상태를 포함한 LikeDTO 객체
     */
    @GetMapping
    public ResponseEntity<LikeDTO> getLikeStatus(@RequestParam int writeId, @RequestParam int userId) {
        try {
            LikeDTO likeDTO = likeService.getLikeStatus(writeId, userId);
            return ResponseEntity.ok(likeDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
