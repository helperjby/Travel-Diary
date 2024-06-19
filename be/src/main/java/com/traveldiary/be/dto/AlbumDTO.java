package com.traveldiary.be.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AlbumDTO {
    private int id; // 앨범 ID
    private String name; // 앨범 이름
    private LocalDate startDate; // 앨범의 시작 날짜
    private LocalDate finalDate; // 앨범의 종료 날짜
    private LocalDateTime createdAt; // 앨범 생성 일시
    private LocalDateTime updatedAt; // 앨범 수정 일시
    private String representativeImage; // 대표 이미지 URL 추가

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getFinalDate() {
        return finalDate;
    }

    public void setFinalDate(LocalDate finalDate) {
        this.finalDate = finalDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRepresentativeImage() {
        return representativeImage;
    }

    public void setRepresentativeImage(String representativeImage) {
        this.representativeImage = representativeImage;
    }
}
