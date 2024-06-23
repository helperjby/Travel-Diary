package com.traveldiary.be.dto;

public class WritingPhotoDTO {
    private String photo;  // 사진 URL 또는 경로
    private int photoId;  // 사진 ID
    private Boolean representativeImage; // 대표 이미지 여부

    // 기본 생성자
    public WritingPhotoDTO() {}

    // 모든 필드를 초기화하는 생성자
    public WritingPhotoDTO(String photo, int photoId, Boolean representativeImage) {
        this.photo = photo;
        this.photoId = photoId;
        this.representativeImage = representativeImage;
    }

    // Getters and Setters
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public Boolean getRepresentativeImage() {
        return representativeImage;
    }

    public void setRepresentativeImage(Boolean representativeImage) {
        this.representativeImage = representativeImage;
    }
}
