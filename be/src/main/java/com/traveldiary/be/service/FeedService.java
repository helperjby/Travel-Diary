package com.traveldiary.be.service;

import com.traveldiary.be.dto.FeedDTO;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.WritingPhoto;
import com.traveldiary.be.repository.WritingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedService {

    @Autowired
    private WritingRepository writingRepository;

    /**
     * 공개된 일기를 피드에 가져오는 메서드
     * @param sortBy 정렬 방식 (date: 작성일 순, likes: 좋아요 순)
     * @return 공개된 일기의 피드 목록
     */
    public List<FeedDTO> getPublicFeeds(String sortBy) {
        List<WritingDiary> publicDiaries;

        // 정렬 방식에 따라 공개된 일기 가져오기
        if ("likes".equalsIgnoreCase(sortBy)) {
            publicDiaries = writingRepository.findPublicDiariesOrderByLikesDesc(); // 좋아요 순으로 정렬
        } else {
            publicDiaries = writingRepository.findPublicDiariesOrderByCreatedAtDesc(); // 작성일 순으로 정렬
        }

        // 일기 목록을 FeedDTO로 변환
        return publicDiaries.stream().map(diary -> {
            FeedDTO feedDTO = new FeedDTO();
            feedDTO.setId(diary.getId());
            feedDTO.setRepresentativeImage(diary.getWritingPhoto().stream()
                    .filter(photo -> photo.getRepresentativeImage() != null && photo.getRepresentativeImage())
                    .findFirst()
                    .map(WritingPhoto::getPhoto)
                    .orElse(null));
            feedDTO.setTitle(diary.getTitle());
            feedDTO.setProfileImage(diary.getUser().getProfileImage());
            feedDTO.setNickname(diary.getUser().getNickname());
            feedDTO.setLikeCount(diary.getLikes().size());
            feedDTO.setCommentCount(diary.getComments().size());
            feedDTO.setCreatedAt(diary.getCreatedAt());

            return feedDTO;
        }).collect(Collectors.toList());
    }
}
