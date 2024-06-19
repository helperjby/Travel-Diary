package com.traveldiary.be.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LikeDTO {
    private int id; // 기본키
    private int writeId; // 일기 ID
    private int userId; // 사용자 ID
    private LocalDateTime createdAt; // 생성 시간
    private int likeCount; // 좋아요 횟수
    private boolean isLiked; // 좋아요 여부
}
