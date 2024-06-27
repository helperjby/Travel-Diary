package com.traveldiary.be.dto;

import java.time.LocalDateTime;

public class FeedDTO {
    private int id; //일기 id
    private String representativeImage;
    private String title;
    private String profileImage;  // 작성자의 프로필 이미지
    private String nickname;  // 작성자의 닉네임
    private int likeCount;  // 좋아요 수
    private int commentCount;  // 댓글 수
    private LocalDateTime createdAt;  // 일기 작성일

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRepresentativeImage() {
        return representativeImage;
    }

    public void setRepresentativeImage(String representativeImage) {
        this.representativeImage = representativeImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
