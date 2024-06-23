package com.traveldiary.be.controller;

import com.traveldiary.be.dto.CommentReRequestDTO;
import com.traveldiary.be.dto.CommentReResponseDTO;
import com.traveldiary.be.service.CommentReService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recomments")
public class CommentReController {

    @Autowired
    private CommentReService commentReService;

    // 대댓글 작성 API
    @PostMapping
    public ResponseEntity<CommentReResponseDTO> writeCommentRe(@RequestParam int userId, @RequestBody CommentReRequestDTO dto) {
        return ResponseEntity.ok(commentReService.writeCommentRe(userId, dto));
    }

    // 대댓글 수정 API
    @PutMapping("/{commentReId}")
    public ResponseEntity<CommentReResponseDTO> updateCommentRe(@PathVariable int commentReId, @RequestBody CommentReRequestDTO dto) {
        return ResponseEntity.ok(commentReService.updateCommentRe(commentReId, dto.getContent()));
    }

    // 대댓글 삭제 API
    @DeleteMapping("/{commentReId}")
    public ResponseEntity<Void> deleteCommentRe(@PathVariable int commentReId) {
        commentReService.deleteCommentRe(commentReId);
        return ResponseEntity.ok().build();
    }

    // 대댓글 조회 API
    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<CommentReResponseDTO>> getReCommentsByCommentId(@PathVariable int commentId) {
        return ResponseEntity.ok(commentReService.getReCommentsByCommentId(commentId));
    }

    // 특정 댓글의 대댓글 수 조회 API
//    @GetMapping("/count/comment/{commentId}")
//    public ResponseEntity<Long> getReCommentCountByCommentId(@PathVariable int commentId) {
//        long count = commentReService.countReCommentsByCommentId(commentId);
//        return ResponseEntity.ok(count);
//    }
}
