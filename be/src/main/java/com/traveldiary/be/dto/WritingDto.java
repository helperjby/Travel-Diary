package com.traveldiary.be.dto;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public class WritingDto {

    private String title;  // 제목
    private String content;  // 내용
    private LocalDate travel_date;  // 여행 날짜
    private LocalDate start_date;  // 여행 시작 날짜
    private LocalDate final_date;  // 여행 종료 날짜
    private Boolean is_public;  // 공개/비공개 설정
    private List<String> photo;  // 첨부된 사진 파일 리스트
    private String url; // 지도 URL

    // 기본 생성자
    public WritingDto() {}

    // 모든 필드를 초기화하는 생성자
    public WritingDto(String title, String content, LocalDate travel_date, LocalDate start_date, LocalDate final_date, Boolean is_public, List<String> photo, String url) {
        this.title = title;
        this.content = content;
        this.travel_date = travel_date;
        this.start_date = start_date;
        this.final_date = final_date;
        this.is_public = is_public;
        this.photo = photo;
        this.url = url;
    }

    // Getters and Setters

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

    public LocalDate getTravel_date() {
        return travel_date;
    }

    public void setTravel_date(LocalDate travel_date) {
        this.travel_date = travel_date;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getFinal_date() {
        return final_date;
    }

    public void setFinal_date(LocalDate final_date) {
        this.final_date = final_date;
    }

    public Boolean getIs_public() {
        return is_public;
    }

    public void setIs_public(Boolean is_public) {
        this.is_public = is_public;
    }

    public List<String> getPhoto() {
        return photo;
    }

    public void setPhoto(List<String> photo) {
        this.photo = photo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
