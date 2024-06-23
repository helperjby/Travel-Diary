package com.traveldiary.be.dto;

public class DiaryEntryAIResponseRequest {
    private int userId; // 사용자 ID
    private String keyword; // 키워드
    private String question; // 질문
    private String response; // 사용자 답변

    // Getter 및 Setter
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
