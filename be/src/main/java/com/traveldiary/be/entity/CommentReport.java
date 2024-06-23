package com.traveldiary.be.entity;

import lombok.Builder;  // 추가
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "comment_report")
@NoArgsConstructor
@AllArgsConstructor
@Builder  // 추가
public class CommentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // 신고 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = true)
    private Comment comment;  // 댓글 ID (nullable)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recomment_id", nullable = true)
    private CommentRe recomment;  // 대댓글 ID (nullable)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;  // 신고한 사용자 ID

    @Column(name = "report_by", nullable = false)
    private int reportBy;  // 신고 당한 사용자 ID

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 신고 시간

    @Column(name = "report_count", nullable = false)
    private int reportCount;  // 신고 횟수

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // 신고가 댓글을 대상으로 하는지 대댓글을 대상으로 하는지 확인하는 메소드
    public boolean isCommentReport() {
        return comment != null;
    }

    public boolean isRecommentReport() {
        return recomment != null;
    }
}
