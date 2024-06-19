package com.traveldiary.be.service;

import com.traveldiary.be.dto.WritingDTO;
import com.traveldiary.be.dto.WritingPhotoDTO;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.WritingPhoto;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.Album;
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

        // 여행 날짜가 유효한지 검사
        if (!isValidTravelDate(diaryDto.getTravel_date(), diaryDto.getStart_date(), diaryDto.getFinal_date())) {
            throw new IllegalArgumentException("여행 날짜는 시작 날짜와 종료 날짜 사이에 있어야 합니다.");
        }

        // 새로운 일기 엔티티 생성
        WritingDiary diary = new WritingDiary();
        diary.setTitle(diaryDto.getTitle());
        diary.setContent(diaryDto.getContent());
        diary.setTravelDate(diaryDto.getTravel_date());
        diary.setStartDate(diaryDto.getStart_date());
        diary.setFinalDate(diaryDto.getFinal_date());
        diary.setIsPublic(diaryDto.getIs_public());
        diary.setUrl(diaryDto.getUrl());
        diary.setUser(user);

        // 앨범 설정
        List<Album> albums = albumRepository.findByUserAndStartDateAndFinalDate(user, diaryDto.getStart_date(), diaryDto.getFinal_date());
        Album album;
        if (albums.isEmpty()) {
            album = new Album();
            album.setUser(user);
            album.setStartDate(diaryDto.getStart_date());
            album.setFinalDate(diaryDto.getFinal_date());
            album.setName(generateUniqueAlbumName(user));
            album = albumRepository.save(album);
        } else {
            album = albums.get(0);
        }
        diary.setAlbum(album);

        WritingDiary savedDiary = writingRepository.save(diary);

        // 사진 저장
        final WritingDiary finalSavedDiary = savedDiary; // effectively final로 변환
        if (photos != null && !photos.isEmpty()) {
            try {
                List<String> photoNames = writingPhotoService.storeFiles(photos, finalSavedDiary);
                List<WritingPhoto> writingPhotos = photoNames.stream()
                        .map(photoName -> {
                            WritingPhoto writingPhoto = new WritingPhoto();
                            writingPhoto.setPhoto(photoName);
                            writingPhoto.setDiary(finalSavedDiary);
                            writingPhoto.setRepresentativeImage(false);
                            return writingPhoto;
                        })
                        .collect(Collectors.toList());

                if (!writingPhotos.isEmpty()) {
                    writingPhotos.get(0).setRepresentativeImage(true); // 첫 번째 사진을 대표 이미지로 설정
                }

                savedDiary.setWritingPhoto(writingPhotos);
            } catch (IOException e) {
                throw new RuntimeException("사진 업로드 중 오류 발생", e);
            }
        } else {
            WritingPhoto defaultPhoto = new WritingPhoto();
            defaultPhoto.setPhoto(representativeImageUrl.substring(representativeImageUrl.lastIndexOf('/') + 1));
            defaultPhoto.setDiary(finalSavedDiary);
            defaultPhoto.setRepresentativeImage(true); // 대표 이미지 설정
            savedDiary.setWritingPhoto(List.of(defaultPhoto));
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

        return responseDto;
    }

    /**
     * 기존 일기를 업데이트하는 메서드
     *
     * @param diaryId 수정할 일기 ID
     * @param diaryDto 일기 데이터 전송 객체
     * @param userId 사용자 ID
     * @param photos 첨부된 사진 목록 (선택사항)
     * @return 수정된 일기 정보를 반환
     */
    public WritingDTO updateDiary(int diaryId, WritingDTO diaryDto, Integer userId, List<MultipartFile> photos) {
        Optional<WritingDiary> optionalDiary = writingRepository.findById(diaryId);
        if (optionalDiary.isEmpty()) {
            throw new RuntimeException("일기를 찾을 수 없습니다.");
        }

        WritingDiary diary = optionalDiary.get();

        // 여행 날짜가 유효한지 검사
        if (!isValidTravelDate(diaryDto.getTravel_date(), diaryDto.getStart_date(), diaryDto.getFinal_date())) {
            throw new IllegalArgumentException("여행 날짜는 시작 날짜와 종료 날짜 사이에 있어야 합니다.");
        }

        diary.setTitle(diaryDto.getTitle());
        diary.setContent(diaryDto.getContent());
        diary.setTravelDate(diaryDto.getTravel_date());
        diary.setStartDate(diaryDto.getStart_date());
        diary.setFinalDate(diaryDto.getFinal_date());
        diary.setIsPublic(diaryDto.getIs_public());
        diary.setUrl(diaryDto.getUrl());

        // 앨범 업데이트
        List<Album> albums = albumRepository.findByUserAndStartDateAndFinalDate(diary.getUser(), diaryDto.getStart_date(), diaryDto.getFinal_date());
        Album newAlbum;
        if (albums.isEmpty()) {
            newAlbum = new Album();
            newAlbum.setUser(diary.getUser());
            newAlbum.setStartDate(diaryDto.getStart_date());
            newAlbum.setFinalDate(diaryDto.getFinal_date());
            newAlbum.setName(diaryDto.getTitle() + " 앨범");
            newAlbum = albumRepository.save(newAlbum);
        } else {
            newAlbum = albums.get(0);
        }
        diary.setAlbum(newAlbum);

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
                            writingPhoto.setDiary(finalDiary);
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
            defaultPhoto.setDiary(finalDiary);
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
     * 여행 날짜가 유효한지 검사하는 메서드
     *
     * @param travelDate 여행 날짜
     * @param startDate 여행 시작 날짜
     * @param finalDate 여행 종료 날짜
     * @return 유효한지 여부
     */
    private boolean isValidTravelDate(LocalDate travelDate, LocalDate startDate, LocalDate finalDate) {
        return !travelDate.isBefore(startDate) && !travelDate.isAfter(finalDate);
    }

    /**
     * 임의의 앨범 이름 생성 메서드
     *
     * @param user 사용자
     * @return 생성된 앨범 이름
     */
    private String generateUniqueAlbumName(Users user) {
        long albumCount = albumRepository.countByUser(user);
        return "여행일기 " + (albumCount + 1);
    }
}
