//package com.traveldiary.be.service;
//
//import com.traveldiary.be.dto.CommentRequestDTO;
//import com.traveldiary.be.entity.Comment;
//import com.traveldiary.be.entity.CommentRe;
//import com.traveldiary.be.entity.Users;
//import com.traveldiary.be.entity.WritingDiary;
//import com.traveldiary.be.repository.CommentRepository;
//import com.traveldiary.be.repository.CommentReRepository;
//import com.traveldiary.be.repository.UserRepository;
//import com.traveldiary.be.repository.WritingRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class CommentService {
//
//    @Autowired
//    private CommentRepository commentRepository;
//
//    @Autowired
//    private WritingRepository writingRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private CommentReRepository commentReRepository;
//
//    // 댓글 작성
//    public Comment writeComment(int userId, CommentRequestDTO dto) {
//        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
//        WritingDiary diary = writingRepository.findById(dto.getDiaryId()).orElseThrow(() -> new IllegalArgumentException("일기 없음"));
//
//        Comment comment = Comment.builder()
//                .diary(diary)  // 일기 설정
//                .user(user)
//                .content(dto.getContent())
//                .build();
//        return commentRepository.save(comment);
//    }
//
//    // 댓글 수정
//    public Comment updateComment(int commentId, String content) {
//        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
//        if (comment.isDeleted()) {
//            throw new IllegalStateException("삭제된 댓글은 수정할 수 없습니다.");
//        }
//        comment.setContent(content);
//        return commentRepository.save(comment);
//    }
//
//    // 댓글 삭제
//    public void deleteComment(int commentId) {
//        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
//        if (comment.isDeleted()) {
//            throw new IllegalStateException("이미 삭제된 댓글입니다.");
//        }
//        comment.setDeleted(true);
//        commentRepository.save(comment);
//    }
//
//    // 댓글 조회
//    public List<Comment> getCommentsByDiaryId(int diaryId) {
//        return commentRepository.findByDiaryIdAndIsDeletedFalse(diaryId);
//    }
//
//    // 전체 댓글 및 대댓글 조회 로직
//    public List<Comment> getAllCommentsByDiaryId(int diaryId) {
//        List<Comment> comments = commentRepository.findByDiaryIdAndIsDeletedFalse(diaryId);
//        for (Comment comment : comments) {
//            List<CommentRe> replies = commentReRepository.findByCommentIdAndIsDeletedFalse(comment.getId());
//            comment.setReplies(replies);
//        }
//        return comments;
//    }
//
//    //댓글 수 조회
//    public long countCommentsByDiaryId(int diaryId) {
//        return commentRepository.countByDiaryIdAndIsDeletedFalse(diaryId);
//    }
//
//}












package com.traveldiary.be.service;

import com.traveldiary.be.dto.CommentRequestDTO;
import com.traveldiary.be.dto.CommentResponseDTO;
import com.traveldiary.be.dto.CommentReResponseDTO;
import com.traveldiary.be.entity.Comment;
import com.traveldiary.be.entity.CommentRe;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.repository.CommentReRepository;
import com.traveldiary.be.repository.CommentRepository;
import com.traveldiary.be.repository.UserRepository;
import com.traveldiary.be.repository.WritingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentReRepository commentReRepository;

    @Autowired
    private WritingRepository writingRepository;

    @Autowired
    private UserRepository userRepository;

    // 댓글 작성
    public Comment writeComment(int userId, CommentRequestDTO dto) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        WritingDiary diary = writingRepository.findById(dto.getDiaryId()).orElseThrow(() -> new IllegalArgumentException("일기 없음"));

        Comment comment = Comment.builder()
                .diary(diary)
                .user(user)
                .content(dto.getContent())
                .build();
        return commentRepository.save(comment);
    }

    // 댓글 수정
    public Comment updateComment(int commentId, String content) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(int commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
        comment.setDeleted(true);
        commentRepository.save(comment);
    }

    // 댓글 조회
    public List<CommentResponseDTO> getCommentsByDiaryId(int diaryId) {
        List<Comment> comments = commentRepository.findByDiaryIdAndIsDeletedFalse(diaryId);
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 전체 댓글 및 대댓글 조회 로직
    public List<CommentResponseDTO> getAllCommentsByDiaryId(int diaryId) {
        List<Comment> comments = commentRepository.findByDiaryIdAndIsDeletedFalse(diaryId);
        for (Comment comment : comments) {
            List<CommentRe> replies = commentReRepository.findByCommentIdAndIsDeletedFalse(comment.getId());
            comment.setReplies(replies);
        }
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public long countCommentsByDiaryId(int diaryId) {
        return commentRepository.countByDiaryIdAndIsDeletedFalse(diaryId);
    }

    public CommentResponseDTO convertToDto(Comment comment) {
        List<CommentReResponseDTO> replies = (comment.getReplies() != null ? comment.getReplies() : new ArrayList<>()).stream()
                .map(reply -> {
                    CommentRe commentRe = (CommentRe) reply; // 타입 캐스팅
                    return CommentReResponseDTO.builder()
                            .id(commentRe.getId())
                            .content(commentRe.getContent())
                            .nickname(commentRe.getUser().getNickname()) // 대댓글 작성자 닉네임 추가
                            .createdAt(commentRe.getCreatedAt())
                            .updatedAt(commentRe.getUpdatedAt())
                            .reportCount(commentRe.getReportCount())
                            .deleted(commentRe.isDeleted())
                            .build();
                })
                .collect(Collectors.toList());

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .nickname(comment.getUser().getNickname()) // 댓글 작성자 닉네임 추가
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replies(replies)
                .reportCount(comment.getReportCount())
                .deleted(comment.isDeleted())
                .build();
    }
}
