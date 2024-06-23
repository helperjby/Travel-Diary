package com.traveldiary.be.controller;

import com.traveldiary.be.dto.CommentReportRequestDTO;
import com.traveldiary.be.dto.CommentReportResponseDTO;
import com.traveldiary.be.entity.CommentReport;
import com.traveldiary.be.service.CommentReportService;
import com.traveldiary.be.service.RecommentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class CommentReportController {

    @Autowired
    private CommentReportService commentReportService;

    @Autowired
    private RecommentReportService recommentReportService;

    // 댓글 신고 작성 API
    @PostMapping("/comments")
    public ResponseEntity<CommentReportResponseDTO> reportComment(@RequestParam int userId, @RequestBody CommentReportRequestDTO dto) {
        CommentReport report = commentReportService.reportComment(userId, dto);
        return ResponseEntity.ok(new CommentReportResponseDTO(report));
    }

    // 대댓글 신고 작성 API
    @PostMapping("/recomments")
    public ResponseEntity<CommentReportResponseDTO> reportRecomment(@RequestParam int userId, @RequestBody CommentReportRequestDTO dto) {
        CommentReport report = recommentReportService.reportRecomment(userId, dto);
        return ResponseEntity.ok(new CommentReportResponseDTO(report));
    }

    // 신고 횟수 조회 API
    @GetMapping("/report-count")
    public ResponseEntity<Integer> getReportCount(@RequestParam int targetId, @RequestParam boolean isRecomment) {
        int reportCount = commentReportService.getReportCount(targetId, isRecomment);
        return ResponseEntity.ok(reportCount);
    }
}
