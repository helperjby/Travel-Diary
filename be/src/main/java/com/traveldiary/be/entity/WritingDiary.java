package com.traveldiary.be.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private int write_id; // 기본 키

    @Column(name = "title", nullable = false)
    private String title;  // 제목

    @Column(name = "travel_date", nullable = false)
    private LocalDate travelDate = LocalDate.now();  // 여행 날짜, 기본값은 작성 날짜

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;  // 여행 시작 날짜

    @Column(name = "final_date", nullable = false)
    private LocalDate finalDate;  // 여행 종료 날짜

    @Column(name = "content", nullable = false)
    private String content;  // 내용

    @Column(name = "is_public", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isPublic = false;  // 공개 여부, 기본값은 비공개(false:0), 공개는 (true:1)

    @Column(name = "url", length = 225)
    private String url; // 지도 URL

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 생성 일시

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 수정 일시

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;  // 삭제 일시

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private Users user;  // 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id")
    @JsonBackReference
    private Album album;  // 앨범

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<WritingPhoto> writingPhotos;  // 여행 일기에 첨부된 사진들
}
