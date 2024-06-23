package com.traveldiary.be.dto;

import com.traveldiary.be.entity.CommentReport;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentReportResponseDTO {
    private int id;
    private int userId;
    private int reportBy;
    private LocalDateTime createdAt;
    private int reportCount;

    public CommentReportResponseDTO(CommentReport report) {
        this.id = report.getId();
        this.userId = report.getUser().getId();
        this.reportBy = report.getReportBy();
        this.createdAt = report.getCreatedAt();
        this.reportCount = report.getReportCount();
    }
}
