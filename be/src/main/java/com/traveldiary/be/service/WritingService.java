package com.traveldiary.be.service;

import com.traveldiary.be.dto.WritingDto;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.WritingPhoto;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.Album;
import com.traveldiary.be.repository.WritingRepository;
import com.traveldiary.be.repository.WritingPhotoRepository;
import com.traveldiary.be.repository.UserRepository;
import com.traveldiary.be.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WritingService {

    @Autowired
    private WritingRepository writingRepository;

    @Autowired
    private WritingPhotoRepository writingPhotoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private WritingPhotoService writingPhotoService;

    private static final Logger logger = LoggerFactory.getLogger(WritingService.class);

    // 일기 ID로 일기 조회 메서드
    public Optional<WritingDiary> findById(int diaryId) {
        return writingRepository.findById(diaryId);
    }

    // 일기 생성 또는 업데이트 메서드
    @Transactional
    public WritingDiary createOrUpdateDiary(WritingDto diaryDto, String providerId) {
        Users user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Album album = albumRepository.findByUserAndStartDateAndFinalDate(user, diaryDto.getStart_date(), diaryDto.getFinal_date())
                .orElseGet(() -> createNewAlbum(user, diaryDto.getStart_date(), diaryDto.getFinal_date()));

        WritingDiary diary = new WritingDiary();
        updateDiaryFields(diary, diaryDto);
        diary.setUser(user);
        diary.setAlbum(album);

        diary = writingRepository.save(diary);

        savePhotos(diary, diaryDto);

        return diary;
    }

    // 일기 업데이트 메서드
    @Transactional
    public WritingDiary updateDiary(int diaryId, WritingDto diaryDto, String providerId) {
        Users user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        WritingDiary diary = writingRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary not found"));

        if (!diary.getUser().equals(user)) {
            throw new RuntimeException("Unauthorized");
        }

        Album newAlbum = albumRepository.findByUserAndStartDateAndFinalDate(user, diaryDto.getStart_date(), diaryDto.getFinal_date())
                .orElseGet(() -> createNewAlbum(user, diaryDto.getStart_date(), diaryDto.getFinal_date()));

        if (!diary.getAlbum().equals(newAlbum)) {
            diary.setAlbum(newAlbum);
        }

        updateDiaryFields(diary, diaryDto);
        diary.setUpdatedAt(LocalDateTime.now());
        diary = writingRepository.save(diary);

        savePhotos(diary, diaryDto);

        return diary;
    }

    // 일기 삭제 메서드
    @Transactional
    public void deleteDiary(int diaryId, String providerId) {
        logger.info("Attempting to delete diary with ID: {}", diaryId);

        Users user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        logger.info("User found: {}", user);

        WritingDiary diary = writingRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary not found"));
        logger.info("Diary found: {}", diary);

        if (!diary.getUser().equals(user)) {
            logger.warn("Unauthorized attempt to delete diary by user: {}", user);
            throw new RuntimeException("Unauthorized");
        }

        // 사진 삭제
        for (WritingPhoto photo : diary.getWritingPhotos()) {
            writingPhotoService.deleteFile(photo.getPhoto());
            writingPhotoRepository.delete(photo);
        }

        Album album = diary.getAlbum();
        logger.info("Album found: {}", album);

        writingRepository.delete(diary);
        logger.info("Diary deleted: {}", diaryId);

        // 앨범에 일기가 더 이상 없다면 앨범도 삭제
        if (writingRepository.findByAlbum(album).isEmpty()) {
            albumRepository.delete(album);
            logger.info("Album deleted: {}", album.getId());
        }
    }

    private Album createNewAlbum(Users user, LocalDate startDate, LocalDate finalDate) {
        Album album = new Album();
        album.setUser(user);
        album.setStartDate(startDate);
        album.setFinalDate(finalDate);
        album.setName(generateAlbumName(user));
        return albumRepository.save(album);
    }

    private String generateAlbumName(Users user) {
        long albumCount = albumRepository.countByUser(user);
        return "여행일기 " + (albumCount + 1);
    }

    private void savePhotos(WritingDiary diary, WritingDto diaryDto) {
        if (diaryDto.getPhoto() != null) {
            for (String photoUrl : diaryDto.getPhoto()) {
                WritingPhoto writingPhoto = new WritingPhoto();
                writingPhoto.setPhoto(photoUrl);
                writingPhoto.setDiary(diary);
                writingPhotoRepository.save(writingPhoto);
            }
        }
    }

    private void updateDiaryFields(WritingDiary diary, WritingDto diaryDto) {
        diary.setTitle(diaryDto.getTitle());
        diary.setContent(diaryDto.getContent());
        diary.setTravelDate(diaryDto.getTravel_date());
        diary.setStartDate(diaryDto.getStart_date());
        diary.setFinalDate(diaryDto.getFinal_date());
        diary.setIsPublic(diaryDto.getIs_public());
        diary.setUrl(diaryDto.getUrl());
    }
}
