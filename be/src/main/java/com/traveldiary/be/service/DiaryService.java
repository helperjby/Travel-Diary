package com.traveldiary.be.service;

import com.traveldiary.be.dto.WritingDTO;
import com.traveldiary.be.dto.WritingPhotoDTO;
import com.traveldiary.be.dto.AlbumDTO;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.repository.WritingRepository;
import com.traveldiary.be.repository.UserRepository;
import com.traveldiary.be.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiaryService {

    private static final Logger logger = LoggerFactory.getLogger(DiaryService.class);

    @Autowired
    private WritingRepository writingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;  // LikeRepository 추가

    private WritingDTO convertToWritingDTO(WritingDiary diary) {
        List<WritingPhotoDTO> writingPhotoDTOs = diary.getWritingPhoto().stream()
                .map(wp -> new WritingPhotoDTO(wp.getPhoto(), wp.getId(), wp.getRepresentativeImage()))
                .collect(Collectors.toList());

        WritingDTO responseDto = new WritingDTO();
        responseDto.setId(diary.getId());
        responseDto.setTitle(diary.getTitle());
        responseDto.setContent(diary.getContent());
        responseDto.setTravel_date(diary.getTravelDate());
        responseDto.setStart_date(diary.getStartDate());
        responseDto.setFinal_date(diary.getFinalDate());
        responseDto.setIs_public(diary.getIsPublic());
        responseDto.setUrl(diary.getUrl());
        responseDto.setWritingPhotos(writingPhotoDTOs);
        responseDto.setLikeCount((int) likeRepository.countByWritingDiary(diary)); // long 값을 int로 변환하여 likeCount 설정

        return responseDto;
    }

    /**
     * 사용자의 모든 일기 조회 메서드
     *
     * @param userId 사용자 ID
     * @return 사용자의 모든 일기 리스트
     */
    public List<WritingDTO> getUserDiaries(Integer userId) {
        // 사용자 ID로 사용자 조회
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<WritingDiary> diaries = writingRepository.findByUser(user);
        logger.info("Fetched diaries for user: {}", userId);
        return diaries.stream().map(this::convertToWritingDTO).collect(Collectors.toList());
    }

    /**
     * 특정 일기 조회 메서드
     *
     * @param diaryId 일기 ID
     * @param userId 사용자 ID
     * @return 조회된 일기 엔터티
     */
    public WritingDTO getDiaryById(Integer diaryId, Integer userId) {
        // 사용자 ID로 사용자 조회
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        logger.info("User found: {}", user);

        // 일기 ID로 일기 조회
        WritingDiary diary = writingRepository.findById(diaryId).orElseThrow(() -> new RuntimeException("Diary not found"));
        logger.info("Diary found: {}", diary);

        // 사용자가 일기의 소유자인지 확인
        if (!diary.getUser().equals(user)) {
            logger.warn("Unauthorized access attempt by user: {}", user);
            throw new RuntimeException("Unauthorized");
        }

        logger.info("Fetched diary by ID: {} for user: {}", diaryId, userId);
        return convertToWritingDTO(diary);
    }



    /**
     * 특정 사용자의 특정 앨범의 모든 일기 조회
     * @param albumId 앨범 ID
     * @param userId 사용자 ID
     * @return 사용자의 모든 일기 리스트
     */
    public List<WritingDTO> getUserDiariesByAlbumId(Integer albumId, Integer userId) {
        // 특정 앨범과 사용자로 일기 조회
        List<WritingDiary> diaries = writingRepository.findByAlbumIdAndUserId(albumId, userId);
        logger.info("Fetched diaries for album: {} and user: {}", albumId, userId);
        return diaries.stream().map(this::convertToWritingDTO).collect(Collectors.toList());
    }




    /**
     * 특정 날짜의 일기 조회 메서드
     *
     * @param date 특정 날짜
     * @param userId 사용자 ID
     * @return 특정 날짜의 일기 리스트
     */
    public List<WritingDTO> getDiariesByDate(LocalDate date, Integer userId) {
        // 사용자 ID로 사용자 조회
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // 특정 날짜와 사용자로 일기 조회
        List<WritingDiary> diaries = writingRepository.findByTravelDateAndUser(date, user);
        logger.info("Fetched diaries for date: {} for user: {}", date, userId);
        return diaries.stream().map(this::convertToWritingDTO).collect(Collectors.toList());
    }

    /**
     * 특정 여행 기간의 일기 조회 메서드
     *
     * @param startDate 여행 시작 날짜
     * @param endDate 여행 종료 날짜
     * @param userId 사용자 ID
     * @return 특정 여행 기간의 일기 리스트
     */
    public List<WritingDTO> getDiariesByPeriod(LocalDate startDate, LocalDate endDate, Integer userId) {
        // 사용자 ID로 사용자 조회
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // 여행 기간과 사용자로 일기 조회
        List<WritingDiary> diaries = writingRepository.findByTravelDateBetweenAndUser(startDate, endDate, user);
        logger.info("Fetched diaries for period: {} - {} for user: {}", startDate, endDate, userId);
        return diaries.stream().map(this::convertToWritingDTO).collect(Collectors.toList());
    }

    /**
     * 공개된 모든 일기 조회 메서드
     *
     * @return 공개된 일기 리스트
     */
    public List<WritingDTO> getPublicDiaries() {
        // 공개된 일기 조회
        List<WritingDiary> diaries = writingRepository.findByIsPublicTrue();
        logger.info("Fetched all public diaries");
        return diaries.stream().map(this::convertToWritingDTO).collect(Collectors.toList());
    }
}
