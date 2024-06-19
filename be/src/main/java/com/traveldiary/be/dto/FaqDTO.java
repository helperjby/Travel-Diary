package com.traveldiary.be.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaqDTO {
    private int id;
    private String question;
    private String answer;
}
