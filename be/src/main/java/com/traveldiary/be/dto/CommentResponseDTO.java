package com.traveldiary.be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CommentResponseDTO {
    private int id;  // 댓글 ID
    private String content;  // 댓글 내용
    private String nickname;  // 댓글 작성자 닉네임
    private LocalDateTime createdAt;  // 댓글 생성 시간
    private LocalDateTime updatedAt;  // 댓글 수정 시간
    private List<CommentReResponseDTO> replies;  // 대댓글 목록
    private int reportCount;  // 댓글 신고 횟수
    private boolean deleted;  // 댓글 삭제 여부
}
