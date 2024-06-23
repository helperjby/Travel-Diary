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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WritingService {

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
     * @param photos 첨부된 사진 목록 (선택사항)
     * @return 작성된 일기 정보를 반환
     */
    public WritingDTO createDiary(WritingDTO diaryDto, Integer userId, List<MultipartFile> photos) {

        Optional<Users> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        Users user = optionalUser.get();

        // 새로운 일기 엔티티 생성
        WritingDiary diary = new WritingDiary();
        diary.setTitle(diaryDto.getTitle());
        diary.setContent(diaryDto.getContent());
//        diary.setTravelDate(diaryDto.getTravel_date());
//        diary.setStartDate(diaryDto.getStart_date() != null ? diaryDto.getStart_date() : LocalDate.of(9999, 12, 31));
//        diary.setFinalDate(diaryDto.getFinal_date() != null ? diaryDto.getFinal_date() : LocalDate.of(9999, 12, 31));

        diary.setTravelDate(diaryDto.getTravel_date() != null ? diaryDto.getTravel_date() : LocalDate.now());
        diary.setStartDate(diaryDto.getStart_date() != null ? diaryDto.getStart_date() : LocalDate.now());
        diary.setFinalDate(diaryDto.getFinal_date() != null ? diaryDto.getFinal_date() : LocalDate.now());

        diary.setIsPublic(diaryDto.getIs_public());
        diary.setUrl(diaryDto.getUrl());
        diary.setUser(user);

        // 앨범 설정 로직
        if (diaryDto.getAlbumId() != null) {
            Album album = albumRepository.findById(diaryDto.getAlbumId())
                    .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));
            diary.setAlbum(album);
        } else {
            Album defaultAlbum = albumRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("기본 앨범을 찾을 수 없습니다."));
            diary.setAlbum(defaultAlbum);
        }

        WritingDiary savedDiary = writingRepository.save(diary);

        // 사진 저장 로직
        final WritingDiary finalSavedDiary = savedDiary; // effectively final로 변환
        if (photos != null && !photos.isEmpty()) {
            try {
                List<String> photoNames = writingPhotoService.storeFiles(photos, finalSavedDiary);
                List<WritingPhoto> writingPhotos = photoNames.stream()
                        .map(photoName -> {
                            WritingPhoto writingPhoto = new WritingPhoto();
                            writingPhoto.setPhoto(photoName);
                            writingPhoto.setWritingDiary(finalSavedDiary);
                            writingPhoto.setRepresentativeImage(false);
                            return writingPhoto;
                        })
                        .collect(Collectors.toList());

                if (!writingPhotos.isEmpty()) {
                    writingPhotos.get(0).setRepresentativeImage(true); // 첫 번째 사진을 대표 이미지로 설정
                }

                savedDiary.setWritingPhoto(new ArrayList<>(writingPhotos));
            } catch (IOException e) {
                throw new RuntimeException("사진 업로드 중 오류 발생", e);
            }
        } else {
            WritingPhoto defaultPhoto = new WritingPhoto();
            defaultPhoto.setPhoto(representativeImageUrl.substring(representativeImageUrl.lastIndexOf('/') + 1));
            defaultPhoto.setWritingDiary(finalSavedDiary);
            defaultPhoto.setRepresentativeImage(true); // 대표 이미지 설정
            savedDiary.setWritingPhoto(new ArrayList<>(List.of(defaultPhoto)));
        }

        savedDiary = writingRepository.save(savedDiary);

        // WritingPhotoDTO 리스트 설정
        List<WritingPhotoDTO> writingPhotoDTOs = savedDiary.getWritingPhoto().stream()
                .map(wp -> new WritingPhotoDTO(wp.getPhoto(), wp.getId(), wp.getRepresentativeImage()))
                .collect(Collectors.toList());

        WritingDTO responseDto = new WritingDTO();
        responseDto.setId(savedDiary.getId());
        responseDto.setTitle(savedDiary.getTitle());
        responseDto.setContent(savedDiary.getContent());
        responseDto.setTravel_date(savedDiary.getTravelDate());
        responseDto.setStart_date(savedDiary.getStartDate());
        responseDto.setFinal_date(savedDiary.getFinalDate());
        responseDto.setIs_public(savedDiary.getIsPublic());
        responseDto.setUrl(savedDiary.getUrl());
        responseDto.setWritingPhotos(writingPhotoDTOs);
        responseDto.setAlbumId(savedDiary.getAlbum().getId()); // 앨범 ID 설정

        return responseDto;
    }


    /**
     * 기존 일기를 업데이트하는 메서드
     *
     * @param diaryId  수정할 일기 ID
     * @param diaryDto 일기 데이터 전송 객체
     * @param userId   사용자 ID
     * @param photos   첨부된 사진 목록 (선택사항)
     * @return 수정된 일기 정보를 반환
     */
    public WritingDTO updateDiary(int diaryId, WritingDTO diaryDto, Integer userId, List<MultipartFile> photos) {
        Optional<WritingDiary> optionalDiary = writingRepository.findById(diaryId);
        if (optionalDiary.isEmpty()) {
            throw new RuntimeException("일기를 찾을 수 없습니다.");
        }

        WritingDiary diary = optionalDiary.get();

        // 일기 업데이트
        diary.setTitle(diaryDto.getTitle());
        diary.setContent(diaryDto.getContent());
//        diary.setTravelDate(diaryDto.getTravel_date());
//        diary.setStartDate(diaryDto.getStart_date() != null ? diaryDto.getStart_date() : LocalDate.of(9999, 12, 31));
//        diary.setFinalDate(diaryDto.getFinal_date() != null ? diaryDto.getFinal_date() : LocalDate.of(9999, 12, 31));

        diary.setTravelDate(diaryDto.getTravel_date() != null ? diaryDto.getTravel_date() : LocalDate.now());
        diary.setStartDate(diaryDto.getStart_date() != null ? diaryDto.getStart_date() : LocalDate.now());
        diary.setFinalDate(diaryDto.getFinal_date() != null ? diaryDto.getFinal_date() : LocalDate.now());

        diary.setIsPublic(diaryDto.getIs_public());
        diary.setUrl(diaryDto.getUrl());
        diary.setAlbum(new Album(1));

        // 기존 사진 삭제 및 파일 삭제
        if (diary.getWritingPhoto() != null && !diary.getWritingPhoto().isEmpty()) {
            List<WritingPhoto> existingPhotos = new ArrayList<>(diary.getWritingPhoto());
            for (WritingPhoto existingPhoto : existingPhotos) {
                writingPhotoService.deleteFile(existingPhoto.getPhoto());
                writingPhotoRepository.delete(existingPhoto);
            }
            diary.getWritingPhoto().clear();
        }

        // 새로운 사진 저장
        final WritingDiary finalDiary = diary; // effectively final로 변환
        if (photos != null && !photos.isEmpty()) {
            try {
                List<String> photoNames = writingPhotoService.storeFiles(photos, finalDiary);
                List<WritingPhoto> writingPhotos = photoNames.stream()
                        .map(photoName -> {
                            WritingPhoto writingPhoto = new WritingPhoto();
                            writingPhoto.setPhoto(photoName);
                            writingPhoto.setWritingDiary(finalDiary);
                            writingPhoto.setRepresentativeImage(false);
                            return writingPhoto;
                        })
                        .collect(Collectors.toList());

                if (!writingPhotos.isEmpty()) {
                    writingPhotos.get(0).setRepresentativeImage(true); // 첫 번째 사진을 대표 이미지로 설정
                }

                diary.getWritingPhoto().addAll(writingPhotos);
            } catch (IOException e) {
                throw new RuntimeException("사진 업로드 중 오류 발생", e);
            }
        } else {
            WritingPhoto defaultPhoto = new WritingPhoto();
            defaultPhoto.setPhoto(representativeImageUrl.substring(representativeImageUrl.lastIndexOf('/') + 1));
            defaultPhoto.setWritingDiary(finalDiary);
            defaultPhoto.setRepresentativeImage(true); // 대표 이미지 설정
            diary.getWritingPhoto().add(defaultPhoto);
        }

        WritingDiary updatedDiary = writingRepository.save(diary);

        // WritingPhotoDTO 리스트 설정
        List<WritingPhotoDTO> writingPhotoDTOs = updatedDiary.getWritingPhoto().stream()
                .map(wp -> new WritingPhotoDTO(wp.getPhoto(), wp.getId(), wp.getRepresentativeImage()))
                .collect(Collectors.toList());

        WritingDTO responseDto = new WritingDTO();
        responseDto.setId(updatedDiary.getId());
        responseDto.setTitle(updatedDiary.getTitle());
        responseDto.setContent(updatedDiary.getContent());
        responseDto.setTravel_date(updatedDiary.getTravelDate());
        responseDto.setStart_date(updatedDiary.getStartDate());
        responseDto.setFinal_date(updatedDiary.getFinalDate());
        responseDto.setIs_public(updatedDiary.getIsPublic());
        responseDto.setUrl(updatedDiary.getUrl());
        responseDto.setWritingPhotos(writingPhotoDTOs);

        return responseDto;
    }

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
     * 여행 날짜가 유효한지 검사하는 메서드
     *
     * @param travelDate 여행 날짜 (null 값 허용)
     * @param startDate  여행 시작 날짜 (null 값 허용)
     * @param finalDate  여행 종료 날짜 (null 값 허용)
     * @return 유효한지 여부
     */
    private boolean isValidTravelDate(LocalDate travelDate, LocalDate startDate, LocalDate finalDate) {
        if (travelDate == null || startDate == null || finalDate == null) {
            return true;
        }
        return !travelDate.isBefore(startDate) && !travelDate.isAfter(finalDate);
    }

    /**
     * 특정 일기를 특정 앨범으로 이동시키는 메서드
     *
     * @param diaryId 이동할 일기 ID
     * @param albumId 이동할 앨범 ID
     * @return 수정된 일기 정보를 반환
     */
    public WritingDTO moveDiaryToAlbum(int diaryId, int albumId, int userId) {
        WritingDiary diary = writingRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다."));

        // 사용자가 일기의 소유자인지 확인
        if (!diary.getUser().getId().equals(userId)) {
            throw new RuntimeException("사용자가 일기의 소유자가 아닙니다.");
        }

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));

        diary.setAlbum(album);
        WritingDiary updatedDiary = writingRepository.save(diary);

        // 일기에 관련된 모든 사진의 앨범을 업데이트
        List<WritingPhoto> photos = diary.getWritingPhoto();
        for (WritingPhoto photo : photos) {
            photo.setAlbum(album);
            writingPhotoRepository.save(photo);
        }

        // 앨범에 포함된 일기를 기반으로 앨범의 날짜를 업데이트
        updateAlbumDates(album);

        // WritingDTO로 변환
        WritingDTO responseDto = new WritingDTO();
        responseDto.setId(updatedDiary.getId());
        responseDto.setTitle(updatedDiary.getTitle());
        responseDto.setContent(updatedDiary.getContent());
        responseDto.setTravel_date(updatedDiary.getTravelDate());
        responseDto.setStart_date(updatedDiary.getStartDate());
        responseDto.setFinal_date(updatedDiary.getFinalDate());
        responseDto.setIs_public(updatedDiary.getIsPublic());
        responseDto.setUrl(updatedDiary.getUrl());
        responseDto.setAlbumId(updatedDiary.getAlbum().getId()); // 앨범 ID 설정

        // 응답 DTO에 writingPhotos 설정
        List<WritingPhotoDTO> writingPhotoDTOs = updatedDiary.getWritingPhoto().stream()
                .map(photo -> new WritingPhotoDTO(photo.getPhoto(), photo.getId(), photo.getRepresentativeImage()))
                .collect(Collectors.toList());
        responseDto.setWritingPhotos(writingPhotoDTOs);

        return responseDto;
    }

    // 앨범에 포함된 일기를 기반으로 앨범의 날짜를 업데이트하는 메서드
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