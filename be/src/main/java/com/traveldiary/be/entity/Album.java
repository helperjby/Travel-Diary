package com.traveldiary.be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "diaries")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // 기본 키

    @Column(name = "name", nullable = false)
    private String name;  // 앨범 이름

    @Column(name = "start_date")
    private LocalDate startDate;  // 시작 날짜

    @Column(name = "final_date")
    private LocalDate finalDate;  // 종료 날짜

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 생성 시간

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  // 수정 시간

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")  // 데이터베이스 필드명은 user_id
    @JsonBackReference
    private Users user;  // 사용자 (객체 관계를 유지)

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<WritingDiary> writingDiaries = new ArrayList<>();  // 일기들 초기화

    // 기본 생성자
    public Album() {}

    // 새로운 생성자 추가
    public Album(int id) {
        this.id = id;
    }
}
