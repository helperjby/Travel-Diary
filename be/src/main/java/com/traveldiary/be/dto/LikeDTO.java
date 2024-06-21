package com.traveldiary.be.dto;

import java.time.LocalDateTime;

public class LikeDTO {

    private int id;
    private int userId;
    private int writingDiaryId;
    private LocalDateTime createdAt;
    private int likeCount;
    private boolean isLiked;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getWritingDiaryId() {
        return writingDiaryId;
    }

    public void setWritingDiaryId(int writingDiaryId) {
        this.writingDiaryId = writingDiaryId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }
}
