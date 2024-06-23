package com.traveldiary.be.repository;

import com.traveldiary.be.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Integer> {
    // 특정 사용자가 특정 댓글을 신고했는지 확인
    Optional<CommentReport> findByUserIdAndCommentId(int userId, int commentId);

    // 특정 사용자가 특정 대댓글을 신고했는지 확인
    Optional<CommentReport> findByUserIdAndRecommentId(int userId, int recommentId);
}
