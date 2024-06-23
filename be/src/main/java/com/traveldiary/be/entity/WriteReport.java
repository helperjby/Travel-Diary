package com.traveldiary.be.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "writereports", uniqueConstraints = {@UniqueConstraint(columnNames = {"write_id", "user_id"})})
public class WriteReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // 기본 키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "write_id", nullable = false)
    private WritingDiary writingDiary;  // 일기

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;  // 사용자

    @Column(name = "diary_report_count", nullable = false)
    private int diaryReportCount;  // 일기 신고 횟수

    @Column(name = "is_reported", nullable = false)
    private boolean isReported; // 신고 처리 여부

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성 시간

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
