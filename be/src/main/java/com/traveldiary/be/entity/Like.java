package com.traveldiary.be.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "likes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // 기본키

    @ManyToOne
    @JoinColumn(name = "write_id", nullable = false)
    private WritingDiary writingDiary; // 다이어리 엔트리와의 연관 관계

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user; // 사용자와의 연관 관계

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "like_count", nullable = false, columnDefinition = "int default 0")
    private int likeCount = 0; // 좋아요 횟수

    @Column(name = "is_liked", nullable = false, columnDefinition = "boolean default false")
    private boolean isLiked = false; // 좋아요 여부

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
