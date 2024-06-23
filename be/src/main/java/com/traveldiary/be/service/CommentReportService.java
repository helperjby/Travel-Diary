package com.traveldiary.be.service;

import com.traveldiary.be.dto.CommentReportRequestDTO;
import com.traveldiary.be.entity.Comment;
import com.traveldiary.be.entity.CommentRe;
import com.traveldiary.be.entity.CommentReport;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.repository.CommentReRepository;
import com.traveldiary.be.repository.CommentRepository;
import com.traveldiary.be.repository.CommentReportRepository;
import com.traveldiary.be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentReportService {

    @Autowired
    private CommentReportRepository commentReportRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentReRepository commentReRepository;

    // 댓글 신고 작성
    public CommentReport reportComment(int userId, CommentReportRequestDTO dto) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Optional<CommentReport> existingReport = commentReportRepository.findByUserIdAndCommentId(userId, dto.getTargetId());
        if (existingReport.isPresent()) {
            throw new IllegalArgumentException("이미 신고한 댓글입니다.");
        }

        Comment comment = commentRepository.findById(dto.getTargetId()).orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        CommentReport report = CommentReport.builder()
                .user(user)
                .comment(comment)
                .reportBy(comment.getUser().getId())
                .createdAt(LocalDateTime.now())
                .reportCount(1)  // 최초 신고 횟수는 1
                .build();

        comment.setReportCount(comment.getReportCount() + 1);
        if (comment.getReportCount() >= 15) {
            comment.setPrivate(true);  // 신고 횟수가 15번 이상이면 비공개 처리
        }
        commentRepository.save(comment);

        return commentReportRepository.save(report);
    }

    // 신고 횟수 조회
    public int getReportCount(int targetId, boolean isRecomment) {
        if (isRecomment) {
            CommentRe commentRe = commentReRepository.findById(targetId).orElseThrow(() -> new IllegalArgumentException("대댓글 없음"));
            return commentRe.getReportCount();
        } else {
            Comment comment = commentRepository.findById(targetId).orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
            return comment.getReportCount();
        }
    }
}
