package com.traveldiary.be.repository;

import com.traveldiary.be.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // 특정 일기 ID에 해당하는 모든 댓글 조회
    List<Comment> findByDiaryId(int diaryId);

    // 특정 일기 ID에 해당하며 삭제되지 않은 모든 댓글 조회
    List<Comment> findByDiaryIdAndIsDeletedFalse(int diaryId);

    // 특정 일기 ID에 해당하며 삭제되지 않은 댓글의 수 조회
    long countByDiaryIdAndIsDeletedFalse(int diaryId);
}
