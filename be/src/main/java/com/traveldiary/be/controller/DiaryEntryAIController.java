package com.traveldiary.be.controller;

import com.traveldiary.be.dto.DiaryEntryAIRequest;
import com.traveldiary.be.entity.DiaryEntryAI;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.service.DiaryEntryAIService;
import com.traveldiary.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai-diaries")
public class DiaryEntryAIController {

    @Autowired
    private DiaryEntryAIService diaryEntryAIService;

    @Autowired
    private UserService userService;

    // 키워드에 대한 랜덤 질문 반환
    @GetMapping("/random-questions")
    public String getRandomQuestion(@RequestParam String keyword) {
        return diaryEntryAIService.getRandomQuestion(keyword);
    }

    // 사용자가 답변한 내용을 저장하고 writeId를 반환
    @PostMapping("/responses")
    public Map<String, Object> saveUserResponses(@RequestParam int userId, @RequestBody DiaryEntryAIRequest request) {
        System.out.println("사용자 답변 저장 - 사용자 ID: " + userId);
        return diaryEntryAIService.saveUserResponses(request, userId);
    }

    // 특정 사용자의 모든 질문과 답변 조회
    @GetMapping("/responses")
    public List<DiaryEntryAI> getUserResponses(@RequestParam int userId) {
        return diaryEntryAIService.getUserResponses(userId);
    }

    // 특정 사용자의 특정 write_id에 해당하는 질문과 답변 조회
    @GetMapping("/responses/by-write-id")
    public List<DiaryEntryAI> getUserResponsesByWriteId(@RequestParam int writeId, @RequestParam int userId) {
        return diaryEntryAIService.getUserResponsesByWriteIdAndUserId(writeId, userId);
    }

    // 특정 write_id의 질문과 응답을 이용하여 여행일기 생성
    @PutMapping("/generate-diary")
    public WritingDiary generateDiary(@RequestParam int writeId, @RequestParam int userId) {
        Users user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return diaryEntryAIService.generateDiaryFromResponses(writeId, user);
    }

}