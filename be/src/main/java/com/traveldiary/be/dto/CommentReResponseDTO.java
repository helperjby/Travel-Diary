package com.traveldiary.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentReResponseDTO {
    private int id;  // 대댓글 ID
    private String content;  // 대댓글 내용
    private String nickname;  // 대댓글 작성자 닉네임
    private LocalDateTime createdAt;  // 대댓글 생성 시간
    private LocalDateTime updatedAt;  // 대댓글 수정 시간
    private int reportCount;  // 대댓글 신고 횟수
    private boolean deleted;  // 대댓글 삭제 여부
}
