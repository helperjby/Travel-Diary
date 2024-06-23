package com.traveldiary.be.repository;

import com.traveldiary.be.entity.CommentRe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentReRepository extends JpaRepository<CommentRe, Integer> {
    // 특정 댓글 ID에 해당하는 모든 대댓글 조회
    List<CommentRe> findByCommentId(int commentId);

    // 특정 댓글 ID에 해당하며 삭제되지 않은 모든 대댓글 조회
    List<CommentRe> findByCommentIdAndIsDeletedFalse(int commentId);

    // 특정 댓글 ID에 해당하며 삭제되지 않은 대댓글의 수 조회
    // long countByCommentIdAndIsDeletedFalse(int commentId);
}
