package com.traveldiary.be.service;

import com.traveldiary.be.dto.CommentReportRequestDTO;
import com.traveldiary.be.entity.CommentRe;
import com.traveldiary.be.entity.CommentReport;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.repository.CommentReRepository;
import com.traveldiary.be.repository.CommentReportRepository;
import com.traveldiary.be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class RecommentReportService {

    @Autowired
    private CommentReportRepository commentReportRepository;

    @Autowired
    private CommentReRepository commentReRepository;

    @Autowired
    private UserRepository userRepository;

    // 대댓글 신고 작성
    public CommentReport reportRecomment(int userId, CommentReportRequestDTO dto) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Optional<CommentReport> existingReport = commentReportRepository.findByUserIdAndRecommentId(userId, dto.getTargetId());
        if (existingReport.isPresent()) {
            throw new IllegalArgumentException("이미 신고한 대댓글입니다.");
        }

        CommentRe commentRe = commentReRepository.findById(dto.getTargetId()).orElseThrow(() -> new IllegalArgumentException("대댓글 없음"));

        CommentReport report = CommentReport.builder()
                .user(user)
                .recomment(commentRe)
                .reportBy(commentRe.getUser().getId())
                .createdAt(LocalDateTime.now())
                .reportCount(1)  // 최초 신고 횟수는 1
                .build();

        commentRe.setReportCount(commentRe.getReportCount() + 1);
        if (commentRe.getReportCount() >= 15) {
            commentRe.setPrivate(true);  // 신고 횟수가 15번 이상이면 비공개 처리
        }
        commentReRepository.save(commentRe);

        return commentReportRepository.save(report);
    }
}
