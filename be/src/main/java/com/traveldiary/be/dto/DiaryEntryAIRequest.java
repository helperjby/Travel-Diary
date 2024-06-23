package com.traveldiary.be.dto;

import java.util.List;

public class DiaryEntryAIRequest {
    private int userId; // 사용자 ID
    private List<String> keywords; // 키워드 리스트
    private List<String> questions; // 질문 리스트
    private List<String> responses; // 사용자 답변 리스트

    // Getter 및 Setter
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public List<String> getResponses() {
        return responses;
    }

    public void setResponses(List<String> responses) {
        this.responses = responses;
    }
}
