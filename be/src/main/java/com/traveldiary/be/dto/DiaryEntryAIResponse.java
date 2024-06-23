package com.traveldiary.be.dto;

import java.time.LocalDateTime;

public class DiaryEntryAIResponse {
    private int id; // 기본 키
    private int userId; // 사용자 ID
    private String title; // GPT가 생성한 제목
    private String content; // GPT가 생성한 일기 내용
    private LocalDateTime createdAt; // 생성 시간

    // Getter 및 Setter
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
