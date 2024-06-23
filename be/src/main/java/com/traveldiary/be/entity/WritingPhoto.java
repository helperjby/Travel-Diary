package com.traveldiary.be.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "writephoto")
public class WritingPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // 기본 키

    @Column(name = "photo", nullable = false)
    private String photo;  // 사진 파일 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "write_id")
    @JsonBackReference("diary-photos")
    private WritingDiary writingDiary;  // 사진이 속한 일기 엔티티

    @Column(name = "representative_image")
    private Boolean representativeImage;  //  이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = true)
    private Album album;  // 앨범

    public WritingPhoto() {
        this.representativeImage = false;
    }

    public WritingPhoto(String photo, WritingDiary writingDiary, Boolean representativeImage) {
        this.photo = photo;
        this.writingDiary = writingDiary;
        this.representativeImage = representativeImage;
    }
}
