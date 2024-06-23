package com.traveldiary.be.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO {
    private int diaryId;  // 일기 ID
    private String content;  // 댓글 내용
}
