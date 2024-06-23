package com.traveldiary.be.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaqDTO {
    private int id;  // FAQ ID
    private String question;  // 질문
    private String answer;  // 답변
}
