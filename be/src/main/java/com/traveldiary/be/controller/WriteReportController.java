package com.traveldiary.be.controller;

import com.traveldiary.be.service.WriteReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class WriteReportController {

    private final WriteReportService writeReportService;

    @Autowired
    public WriteReportController(WriteReportService writeReportService) {
        this.writeReportService = writeReportService;
    }

    // 일기 신고 처리
    @PostMapping("/diary")
    public ResponseEntity<String> reportDiary(@RequestBody ReportRequest request) {
        boolean isReported = writeReportService.reportDiary(request.getDiaryId(), request.getUserId());

        if (isReported) {
            return ResponseEntity.ok("신고가 처리되었습니다.");
        } else {
            return ResponseEntity.status(409).body("이미 신고했습니다.");
        }
    }

    // 특정 일기의 신고 횟수를 조회
    @GetMapping("/diary/{diaryId}")
    public ResponseEntity<Map<String, Object>> getReportCount(@PathVariable Integer diaryId) {
        long count = writeReportService.getReportCount(diaryId);
        Map<String, Object> response = new HashMap<>();
        response.put("write_id", diaryId);
        response.put("diary_report_count", count);
        return ResponseEntity.ok(response);
    }

    // 신고 요청 처리하기 위한 DTO
    public static class ReportRequest {
        private Integer diaryId;
        private Integer userId;

        // getters and setters
        public Integer getDiaryId() {
            return diaryId;
        }

        public void setDiaryId(Integer diaryId) {
            this.diaryId = diaryId;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }
    }
}
