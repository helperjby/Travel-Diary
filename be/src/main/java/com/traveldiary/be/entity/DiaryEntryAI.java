package com.traveldiary.be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "diaryentry_ai")
public class DiaryEntryAI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // 기본 키

    @Column(name = "keyword", nullable = false)
    private String keyword;  // 키워드

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    private String question;  // 질문

    @Column(name = "response", nullable = false, columnDefinition = "TEXT")
    private String response;  // 응답

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private Users user;  // 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "write_id", nullable = false)
    @JsonBackReference
    private WritingDiary writingDiary;  // 일기

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성 시간

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 수정 시간

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
