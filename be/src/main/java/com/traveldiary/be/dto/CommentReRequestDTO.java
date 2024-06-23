package com.traveldiary.be.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReRequestDTO {
    private int commentId;  // 댓글 ID
    private String content;  // 대댓글 내용
}
