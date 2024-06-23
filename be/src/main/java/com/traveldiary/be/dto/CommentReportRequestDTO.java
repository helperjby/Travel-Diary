package com.traveldiary.be.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReportRequestDTO {
    private int targetId;  // 신고 대상 ID (댓글 또는 대댓글 ID)
    private boolean isRecomment;  // 대댓글인지 여부
}
