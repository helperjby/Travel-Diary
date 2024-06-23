package com.traveldiary.be.service;

import com.traveldiary.be.dto.CommentReRequestDTO;
import com.traveldiary.be.dto.CommentReResponseDTO;
import com.traveldiary.be.entity.Comment;
import com.traveldiary.be.entity.CommentRe;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.repository.CommentReRepository;
import com.traveldiary.be.repository.CommentRepository;
import com.traveldiary.be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentReService {

    @Autowired
    private CommentReRepository commentReRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    // 대댓글 작성
    public CommentReResponseDTO writeCommentRe(int userId, CommentReRequestDTO dto) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        Comment comment = commentRepository.findById(dto.getCommentId()).orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        CommentRe commentRe = CommentRe.builder()
                .comment(comment)
                .user(user)
                .content(dto.getContent())
                .build();
        CommentRe savedCommentRe = commentReRepository.save(commentRe);
        return convertToDto(savedCommentRe);
    }

    // 대댓글 수정
    public CommentReResponseDTO updateCommentRe(int commentReId, String content) {
        CommentRe commentRe = commentReRepository.findById(commentReId).orElseThrow(() -> new IllegalArgumentException("대댓글 없음"));
        if (commentRe.isDeleted()) {
            throw new IllegalStateException("삭제된 대댓글은 수정할 수 없습니다.");
        }
        commentRe.setContent(content);
        CommentRe updatedCommentRe = commentReRepository.save(commentRe);
        return convertToDto(updatedCommentRe);
    }

    // 대댓글 삭제
    public void deleteCommentRe(int commentReId) {
        CommentRe commentRe = commentReRepository.findById(commentReId).orElseThrow(() -> new IllegalArgumentException("대댓글 없음"));
        if (commentRe.isDeleted()) {
            throw new IllegalStateException("이미 삭제된 대댓글입니다.");
        }
        commentRe.setDeleted(true);
        commentReRepository.save(commentRe);
    }

    // 대댓글 조회
    public List<CommentReResponseDTO> getReCommentsByCommentId(int commentId) {
        List<CommentRe> reComments = commentReRepository.findByCommentIdAndIsDeletedFalse(commentId);
        return reComments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // CommentRe 엔티티를 CommentReResponseDTO로 변환
    private CommentReResponseDTO convertToDto(CommentRe commentRe) {
        return CommentReResponseDTO.builder()
                .id(commentRe.getId())
                .content(commentRe.getContent())
                .nickname(commentRe.getUser().getNickname())
                .createdAt(commentRe.getCreatedAt())
                .updatedAt(commentRe.getUpdatedAt())
                .reportCount(commentRe.getReportCount())
                .deleted(commentRe.isDeleted())
                .build();
    }

    // 대댓글 수 조회
//    public long countReCommentsByCommentId(int commentId) {
//        return commentReRepository.countByCommentIdAndIsDeletedFalse(commentId);
//    }
}
