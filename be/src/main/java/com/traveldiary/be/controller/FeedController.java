package com.traveldiary.be.controller;

import com.traveldiary.be.dto.FeedDTO;
import com.traveldiary.be.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    @Autowired
    private FeedService feedService;

    /**
     * 공개된 일기 피드를 가져오는 엔드포인트
     * @param sortBy 정렬 기준 (기본값: "date")
     * @return 공개된 일기의 피드 목록
     * /api/feed 는 최신 작성일 순 정렬
     * /api/feed?sortBy=likes 는 좋아요 순으로 정렬
     */
    @GetMapping
    public ResponseEntity<List<FeedDTO>> getPublicFeed(@RequestParam(required = false, defaultValue = "date") String sortBy) {
        List<FeedDTO> feed = feedService.getPublicFeeds(sortBy);
        return ResponseEntity.ok(feed);
    }
}
