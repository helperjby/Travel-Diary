//package com.traveldiary.be.controller;
//
//import com.traveldiary.be.dto.CommentRequestDTO;
//import com.traveldiary.be.entity.Comment;
//import com.traveldiary.be.service.CommentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/comments")
//public class CommentController {
//
//    @Autowired
//    private CommentService commentService;
//
//    // 댓글 작성 API
//    @PostMapping
//    public ResponseEntity<Comment> writeComment(@RequestParam int userId, @RequestBody CommentRequestDTO dto) {
//        Comment createdComment = commentService.writeComment(userId, dto);
//        return ResponseEntity.ok(createdComment);
//    }
//
//    // 댓글 수정 API
//    @PutMapping("/{commentId}")
//    public ResponseEntity<Comment> updateComment(@PathVariable int commentId, @RequestParam String content) {
//        Comment updatedComment = commentService.updateComment(commentId, content);
//        return ResponseEntity.ok(updatedComment);
//    }
//
//    // 댓글 삭제 API
//    @DeleteMapping("/{commentId}")
//    public ResponseEntity<Void> deleteComment(@PathVariable int commentId) {
//        commentService.deleteComment(commentId);
//        return ResponseEntity.ok().build();
//    }
//
//    // 댓글 조회 API
//    @GetMapping("/diary/{diaryId}")
//    public ResponseEntity<List<Comment>> getCommentsByDiaryId(@PathVariable int diaryId) {
//        List<Comment> comments = commentService.getCommentsByDiaryId(diaryId);
//        return ResponseEntity.ok(comments);
//    }
//
//    // 특정 diary_id에 연결된 모든 댓글 및 대댓글 조회 API
//    @GetMapping("/all/diary/{diaryId}")
//    public ResponseEntity<List<Comment>> getAllCommentsByDiaryId(@PathVariable int diaryId) {
//        List<Comment> comments = commentService.getAllCommentsByDiaryId(diaryId);
//        return ResponseEntity.ok(comments);
//    }
//
//    // 특정 일기의 댓글 수 조회 API
//    @GetMapping("/count/diary/{diaryId}")
//    public ResponseEntity<Long> getCommentCountByDiaryId(@PathVariable int diaryId) {
//        long count = commentService.countCommentsByDiaryId(diaryId);
//        return ResponseEntity.ok(count);
//    }
//
//
//}







package com.traveldiary.be.controller;

import com.traveldiary.be.dto.CommentRequestDTO;
import com.traveldiary.be.dto.CommentResponseDTO;
import com.traveldiary.be.entity.Comment;
import com.traveldiary.be.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 댓글 작성 API
    @PostMapping
    public ResponseEntity<CommentResponseDTO> writeComment(@RequestParam int userId, @RequestBody CommentRequestDTO dto) {
        Comment createdComment = commentService.writeComment(userId, dto);
        return ResponseEntity.ok(commentService.convertToDto(createdComment));
    }


    // 댓글 수정 API
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(@PathVariable int commentId, @RequestParam String content) {
        Comment updatedComment = commentService.updateComment(commentId, content);
        return ResponseEntity.ok(commentService.convertToDto(updatedComment));
    }

    // 댓글 삭제 API
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable int commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    // 댓글 조회 API
    @GetMapping("/diary/{diaryId}")
    public ResponseEntity<List<CommentResponseDTO>> getCommentsByDiaryId(@PathVariable int diaryId) {
        List<CommentResponseDTO> comments = commentService.getCommentsByDiaryId(diaryId);
        return ResponseEntity.ok(comments);
    }

    // 특정 diary_id에 연결된 모든 댓글 및 대댓글 조회 API
    @GetMapping("/all/diary/{diaryId}")
    public ResponseEntity<List<CommentResponseDTO>> getAllCommentsByDiaryId(@PathVariable int diaryId) {
        List<CommentResponseDTO> comments = commentService.getAllCommentsByDiaryId(diaryId);
        return ResponseEntity.ok(comments);
    }

    // 특정 일기의 댓글 수 조회 API
    @GetMapping("/count/diary/{diaryId}")
    public ResponseEntity<Long> getCommentCountByDiaryId(@PathVariable int diaryId) {
        long count = commentService.countCommentsByDiaryId(diaryId);
        return ResponseEntity.ok(count);
    }
}
