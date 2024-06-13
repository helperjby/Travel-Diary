package com.traveldiary.be.entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Setter
@Getter
@Entity
@Table(name = "writephoto")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Hibernate의 Lazy Loading 문제 해결
public class WritingPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("photo_id") // JSON 응답에서 photo_id로 사용
    private Long photoId; // 기본 키

    @Column(name = "photo", nullable = false)
    private String photo;  // 사진 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "write_id")
    @JsonBackReference
    private WritingDiary diary;  // 연관된 일기
}
