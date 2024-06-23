package com.traveldiary.be.dto;

public class WriteReportDTO {
    private Integer diaryId;  // 일기 ID
    private Integer userId;  // 사용자 ID

    // 일기 ID를 반환하는 getter 메서드
    public Integer getDiaryId() {
        return diaryId;
    }

    // 일기 ID를 설정하는 setter 메서드
    public void setDiaryId(Integer diaryId) {
        this.diaryId = diaryId;
    }

    // 사용자 ID를 반환하는 getter 메서드
    public Integer getUserId() {
        return userId;
    }

    // 사용자 ID를 설정하는 setter 메서드
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
