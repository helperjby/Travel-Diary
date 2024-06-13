package com.traveldiary.be.service;

//파일 업로드 시 반환되는 응답 객체로, 파일의 ID와 고유 파일 이름을 포함

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FileUploadResponse {
    //업로드된 사진의 ID를 설정
    //업로드된 사진의 ID를 반환
    private Long photoId; //업로드된 사진의 ID
    //업로드된 파일의 고유한 이름을 설정
    //업로드된 파일의 고유한 이름을 반환
    private String uniqueFileName; // 업로드된 파일의 고유한 이름

    //기본 생성자
    //객체 생성 시 초기화할 값을 지정하지 않음
    public FileUploadResponse() {}

    //모든 필드를 초기화하는 생성자
    public FileUploadResponse(Long photoId, String uniqueFileName) {
        this.photoId = photoId;
        this.uniqueFileName = uniqueFileName;
    }

}