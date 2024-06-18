package com.traveldiary.be.service;

public class FileUploadResponse {
    private int photoId;  // 사진 ID
    private String uniqueFileName;  // 고유 파일 이름

    public FileUploadResponse(int photoId, String uniqueFileName) {
        this.photoId = photoId;
        this.uniqueFileName = uniqueFileName;
    }

    // Getters and Setters

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }

    public String getUniqueFileName() {
        return uniqueFileName;
    }

    public void setUniqueFileName(String uniqueFileName) {
        this.uniqueFileName = uniqueFileName;
    }
}
