package com.traveldiary.be.service;

import com.traveldiary.be.dto.WritingDTO;
import com.traveldiary.be.dto.WritingPhotoDTO;
import com.traveldiary.be.entity.Album;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.WritingPhoto;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.repository.WritingRepository;
import com.traveldiary.be.repository.WritingPhotoRepository;
import com.traveldiary.be.repository.UserRepository;
import com.traveldiary.be.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WritingService {

    private static final Logger logger = LoggerFactory.getLogger(WritingService.class);

    private final WritingRepository writingRepository;
    private final WritingPhotoRepository writingPhotoRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final WritingPhotoService writingPhotoService;

    @Value("${representative.image-url}")
    private String representativeImageUrl;

    @Autowired
    public WritingService(WritingRepository writingRepository, WritingPhotoRepository writingPhotoRepository, UserRepository userRepository, AlbumRepository albumRepository, WritingPhotoService writingPhotoService) {
        this.writingRepository = writingRepository;
        this.writingPhotoRepository = writingPhotoRepository;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
        this.writingPhotoService = writingPhotoService;
    }

    /**
     * 새로운 일기를 생성하는 메서드
     *
     * @param diaryDto 일기 데이터 전송 객체
     * @param userId 사용자 ID
     * @return 생성된 일기 정보를 반환
     */
    public WritingDTO createDiary(WritingDTO diaryDto, Integer userId) {
        Optional<Users> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        Users user = optionalUser.get();

        WritingDiary diary = new WritingDiary();
        diary.setTitle(diaryDto.getTitle());
        diary.setContent(diaryDto.getContent());
        diary.setTravelDate(diaryDto.getTravel_date() != null ? diaryDto.getTravel_date() : LocalDate.now());
        diary.setStartDate(diaryDto.getStart_date() != null ? diaryDto.getStart_date() : LocalDate.now());
        diary.setFinalDate(diaryDto.getFinal_date() != null ? diaryDto.getFinal_date() : LocalDate.now());
        diary.setIsPublic(diaryDto.getIs_public());
        diary.setUrl(diaryDto.getUrl());
        diary.setUser(user);

        Album album;
        if (diaryDto.getAlbumId() != null) {
            album = albumRepository.findById(diaryDto.getAlbumId())
                    .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));
        } else {
            album = albumRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("기본 앨범을 찾을 수 없습니다."));
        }
        diary.setAlbum(album);

        WritingDiary savedDiary = writingRepository.save(diary);

        WritingDTO responseDto = new WritingDTO();
        responseDto.setId(savedDiary.getId());
        responseDto.setTitle(savedDiary.getTitle());
        responseDto.setContent(savedDiary.getContent());
        responseDto.setTravel_date(savedDiary.getTravelDate());
        responseDto.setStart_date(savedDiary.getStartDate());
        responseDto.setFinal_date(savedDiary.getFinalDate());
        responseDto.setIs_public(savedDiary.getIsPublic());
        responseDto.setUrl(savedDiary.getUrl());
        responseDto.setAlbumId(savedDiary.getAlbum().getId());

        return responseDto;
    }

    /**
     * 일기를 업데이트하는 메서드
     *
     * @param diaryId 수정할 일기 ID
     * @param diaryDto 일기 데이터 전송 객체
     * @param userId 사용자 ID
     * @return 업데이트된 일기 정보를 반환
     */
    public WritingDTO updateDiary(int diaryId, WritingDTO diaryDto, Integer userId) {
        Optional<WritingDiary> optionalDiary = writingRepository.findById(diaryId);
        if (optionalDiary.isEmpty()) {
            throw new RuntimeException("일기를 찾을 수 없습니다.");
        }

        WritingDiary diary = optionalDiary.get();

        Album album;
        if (diaryDto.getAlbumId() != null) {
            album = albumRepository.findById(diaryDto.getAlbumId())
                    .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));
        } else {
            album = albumRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("기본 앨범을 찾을 수 없습니다."));
        }
        diary.setAlbum(album);

        diary.setTitle(diaryDto.getTitle());
        diary.setContent(diaryDto.getContent());
        diary.setTravelDate(diaryDto.getTravel_date() != null ? diaryDto.getTravel_date() : LocalDate.now());
        diary.setStartDate(diaryDto.getStart_date() != null ? diaryDto.getStart_date() : LocalDate.now());
        diary.setFinalDate(diaryDto.getFinal_date() != null ? diaryDto.getFinal_date() : LocalDate.now());
        diary.setIsPublic(diaryDto.getIs_public());
        diary.setUrl(diaryDto.getUrl());

        WritingDiary updatedDiary = writingRepository.save(diary);

        WritingDTO responseDto = new WritingDTO();
        responseDto.setId(updatedDiary.getId());
        responseDto.setTitle(updatedDiary.getTitle());
        responseDto.setContent(updatedDiary.getContent());
        responseDto.setTravel_date(updatedDiary.getTravelDate());
        responseDto.setStart_date(updatedDiary.getStartDate());
        responseDto.setFinal_date(updatedDiary.getFinalDate());
        responseDto.setIs_public(updatedDiary.getIsPublic());
        responseDto.setUrl(updatedDiary.getUrl());
        responseDto.setAlbumId(updatedDiary.getAlbum().getId());

        return responseDto;
    }

    /**
     * 일기를 삭제하는 메서드
     *
     * @param diaryId 삭제할 일기 ID
     * @param userId 사용자 ID
     */
    public void deleteDiary(int diaryId, Integer userId) {
        Optional<WritingDiary> optionalDiary = writingRepository.findById(diaryId);
        if (optionalDiary.isEmpty()) {
            throw new RuntimeException("일기를 찾을 수 없습니다.");
        }

        WritingDiary diary = optionalDiary.get();
        if (!diary.getUser().getId().equals(userId)) {
            throw new RuntimeException("사용자가 일기의 소유자가 아닙니다.");
        }

        // 일기에 첨부된 모든 사진 삭제
        if (diary.getWritingPhoto() != null && !diary.getWritingPhoto().isEmpty()) {
            List<WritingPhoto> existingPhotos = new ArrayList<>(diary.getWritingPhoto());
            for (WritingPhoto existingPhoto : existingPhotos) {
                writingPhotoService.deleteFile(existingPhoto.getPhoto());
                writingPhotoRepository.delete(existingPhoto);
            }
        }

        writingRepository.deleteById(diaryId);
    }

    /**
     * 일기를 다른 앨범으로 이동하는 메서드
     *
     * @param diaryId 이동할 일기 ID
     * @param albumId 이동할 앨범 ID
     * @param userId 사용자 ID
     * @return 이동된 일기 정보를 반환
     */
    @Transactional
    public WritingDTO moveDiaryToAlbum(int diaryId, int albumId, int userId) {
        WritingDiary diary = writingRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다."));

        if (!diary.getUser().getId().equals(userId)) {
            throw new RuntimeException("사용자가 일기의 소유자가 아닙니다.");
        }

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));

        diary.setAlbum(album);
        WritingDiary updatedDiary = writingRepository.save(diary);

        // 사진들도 앨범으로 이동
        List<WritingPhoto> photos = diary.getWritingPhoto();
        for (WritingPhoto photo : photos) {
            photo.setAlbum(album);
            writingPhotoRepository.save(photo);
        }

        updateAlbumDates(album);

        WritingDTO responseDto = new WritingDTO();
        responseDto.setId(updatedDiary.getId());
        responseDto.setTitle(updatedDiary.getTitle());
        responseDto.setContent(updatedDiary.getContent());
        responseDto.setTravel_date(updatedDiary.getTravelDate());
        responseDto.setStart_date(updatedDiary.getStartDate());
        responseDto.setFinal_date(updatedDiary.getFinalDate());
        responseDto.setIs_public(updatedDiary.getIsPublic());
        responseDto.setUrl(updatedDiary.getUrl());
        responseDto.setAlbumId(updatedDiary.getAlbum().getId());

        List<WritingPhotoDTO> writingPhotoDTOs = updatedDiary.getWritingPhoto().stream()
                .map(photo -> new WritingPhotoDTO(photo.getPhoto(), photo.getId(), photo.getRepresentativeImage()))
                .collect(Collectors.toList());
        responseDto.setWritingPhotos(writingPhotoDTOs);

        return responseDto;
    }

    /**
     * 앨범의 날짜를 업데이트하는 메서드
     *
     * @param album 앨범 엔티티
     */
    private void updateAlbumDates(Album album) {
        List<WritingDiary> diaries = writingRepository.findByAlbum(album);

        if (diaries.isEmpty()) {
            album.setStartDate(null);
            album.setFinalDate(null);
            albumRepository.save(album);
            return;
        }

        LocalDate earliestDate = null;
        LocalDate latestDate = null;

        for (WritingDiary diary : diaries) {
            LocalDate diaryStartDate = diary.getStartDate() != null ? diary.getStartDate() : diary.getTravelDate();
            LocalDate diaryFinalDate = diary.getFinalDate() != null ? diary.getFinalDate() : diary.getTravelDate();

            if (diaryStartDate != null) {
                if (earliestDate == null || diaryStartDate.isBefore(earliestDate)) {
                    earliestDate = diaryStartDate;
                }
            }

            if (diaryFinalDate != null) {
                if (latestDate == null || diaryFinalDate.isAfter(latestDate)) {
                    latestDate = diaryFinalDate;
                }
            }
        }

        album.setStartDate(earliestDate);
        album.setFinalDate(latestDate);
        albumRepository.save(album);
    }
}
