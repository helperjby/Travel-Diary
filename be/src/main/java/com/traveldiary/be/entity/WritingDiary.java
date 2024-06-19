package com.traveldiary.be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "diaryentry")
public class WritingDiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // 기본 키

    @Column(name = "title", nullable = false)
    private String title;  // 제목

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;  // 내용

    @Column(name = "travel_date", nullable = false)
    private LocalDate travelDate;  // 여행 날짜

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;  // 여행 시작 날짜

    @Column(name = "final_date", nullable = false)
    private LocalDate finalDate;  // 여행 종료 날짜

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;  // 공개 여부

    @Column(name = "url")
    private String url;  // URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private Users user;  // 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    @JsonBackReference
    private Album album;  // 앨범

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<WritingPhoto> writingPhoto;  // 사진들

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;  // 생성 시간

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 수정 시간

    @Column(name = "representative_image")
    private String representativeImage;  // 대표 이미지

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
