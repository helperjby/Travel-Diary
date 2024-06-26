package com.traveldiary.be.service;

import com.traveldiary.be.dto.AlbumDTO;
import com.traveldiary.be.entity.Album;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.WritingPhoto;
import com.traveldiary.be.repository.AlbumRepository;
import com.traveldiary.be.repository.UserRepository;
import com.traveldiary.be.repository.WritingRepository;
import com.traveldiary.be.repository.WritingPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AlbumService {

    private static final Logger logger = LoggerFactory.getLogger(AlbumService.class);

    @Value("${representative.image-url}")
    private String representativeImageUrl;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WritingRepository writingRepository;

    @Autowired
    private WritingPhotoRepository writingPhotoRepository;

    @Autowired
    private WritingPhotoService writingPhotoService;


    /**
     * 앨범 이름 업데이트 메서드
     *
     * @param albumId   앨범 ID
     * @param name      새로운 앨범 이름
     * @param userId 사용자 ID
     * @return 업데이트된 앨범 DTO
     */
    @Transactional
    public AlbumDTO updateAlbumName(int albumId, String name, Integer userId) {
        logger.info("ID: {} 앨범의 이름을 {}(으)로 업데이트합니다.", albumId, name);

        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
        logger.info("사용자를 찾았습니다: {}", user);

        // 앨범 조회
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다: " + albumId));
        logger.info("앨범을 찾았습니다: {}", album);

        // 사용자가 앨범의 소유자인지 확인
        if (!album.getUser().equals(user)) {
            logger.warn("사용자가 앨범을 업데이트할 권한이 없습니다: {}", user);
            throw new RuntimeException("사용자가 앨범을 업데이트할 권한이 없습니다.");
        }

        // 앨범 이름이 비어 있거나 null인 경우 임의의 이름 설정
        if (name == null || name.trim().isEmpty()) {
            name = generateAlbumName(user);
        }

        // 앨범 이름 업데이트
        album.setName(name);
        album.setUpdatedAt(LocalDateTime.now());
        Album updatedAlbum = albumRepository.save(album);
        logger.info("앨범이 업데이트되었습니다: {}", updatedAlbum);

        // 업데이트된 앨범 DTO 반환
        AlbumDTO albumDTO = convertToAlbumDTO(updatedAlbum);

        return albumDTO;
    }

    /**
     * 새로운 앨범 생성 메서드
     *
     * @param request    앨범 DTO
     * @param userId 사용자 ID
     * @return 생성된 앨범 DTO
     */
    @Transactional
    public AlbumDTO createAlbum(AlbumDTO request, Integer userId) {
        logger.info("새로운 앨범을 생성합니다. 이름: {}", request.getName());

        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
        logger.info("사용자를 찾았습니다: {}", user);

        // 앨범 이름이 비어 있거나 null인 경우 임의의 이름 설정
        String name = request.getName();
        if (name == null || name.trim().isEmpty()) {
            name = generateAlbumName(user);
        }

        // 앨범 생성
        Album album = new Album();
        album.setName(name);
        album.setStartDate(request.getStartDate());
        album.setFinalDate(request.getFinalDate());
        album.setUser(user);
        album.setCreatedAt(LocalDateTime.now());

        Album savedAlbum = albumRepository.save(album);
        logger.info("앨범이 생성되었습니다: {}", savedAlbum);

        // 기본 대표 이미지 설정
        setDefaultRepresentativeImage(savedAlbum);

        // 생성된 앨범 DTO 반환
        AlbumDTO albumDTO = convertToAlbumDTO(savedAlbum);

        return albumDTO;
    }


    /**
     * 앨범 삭제 메서드
     *
     * @param albumId    앨범 ID
     * @param userId 사용자 ID
     */
//    @Transactional
//    public void deleteAlbum(int albumId, Integer userId) {
//        logger.info("이제 앨범 삭제합니다 ID: {}", albumId);
//
//        // 사용자 조회
//        Users user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.: " + userId));
//        logger.info("사용자 찾음: {}", user);
//
//        // 앨범 조회
//        Album album = albumRepository.findById(albumId)
//                .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다: " + albumId));
//        logger.info("앨범 찾음: {}", album);
//
//        // 사용자가 앨범의 소유자인지 확인
//        if (!album.getUser().equals(user)) {
//            logger.warn("삭제 권한있는 사용자가 아닙니다: {}", user);
//            throw new RuntimeException("사용자가 앨범의 소유자가 아닙니다.");
//        }
//
//        // 앨범에 포함된 일기와 사진 삭제
//        List<WritingDiary> diaries = writingRepository.findByAlbum(album);
//        for (WritingDiary diary : diaries) {
//            logger.info("일기 삭제 중: {}", diary.getId());
//
//            // 일기에 첨부된 모든 사진 삭제
//            if (diary.getWritingPhoto() != null && !diary.getWritingPhoto().isEmpty()) {
//                List<WritingPhoto> existingPhotos = new ArrayList<>(diary.getWritingPhoto());
//                for (WritingPhoto existingPhoto : existingPhotos) {
//                    logger.info("사진 삭제 중: {}", existingPhoto.getId());
//                    writingPhotoService.deleteFile(existingPhoto.getPhoto());
//                    existingPhoto.setWritingDiary(null);  // 외래 키 참조 제거
//                    existingPhoto.setAlbum(null);         // 외래 키 참조 제거
//                    writingPhotoRepository.delete(existingPhoto);
//                }
//            }
//
//            // 일기 삭제
//            try {
//                writingRepository.delete(diary);
//                logger.info("일기 삭제 완료: {}", diary.getId());
//            } catch (Exception e) {
//                logger.error("일기 삭제 중 오류 발생: {}", diary.getId(), e);
//            }
//        }
//
//        // 앨범 삭제
//        try {
//            albumRepository.delete(album);
//            logger.info("앨범 삭제 완료 ID: {}", albumId);
//        } catch (Exception e) {
//            logger.error("앨범 삭제 중 오류 발생 ID: {}", albumId, e);
//        }
//
//        logger.info("앨범과 그 안의 일기, 사진이 모두 삭제되었습니다.");
//    }

    @Transactional
    public void deleteAlbum(int albumId, Integer userId) {
        logger.info("이제 앨범 삭제합니다 ID: {}", albumId);

        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.: " + userId));
        logger.info("사용자 찾음: {}", user);

        // 앨범 조회
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다: " + albumId));
        logger.info("앨범 찾음: {}", album);

        // 사용자가 앨범의 소유자인지 확인
        if (!album.getUser().equals(user)) {
            logger.warn("삭제 권한있는 사용자가 아닙니다: {}", user);
            throw new RuntimeException("사용자가 앨범의 소유자가 아닙니다.");
        }

        // 앨범에 포함된 모든 사진 삭제
        List<WritingPhoto> photos = writingPhotoRepository.findByAlbum(album);
        for (WritingPhoto photo : photos) {
            writingPhotoService.deleteFile(photo.getPhoto());
        }
        writingPhotoRepository.deleteByAlbumId(albumId);
        logger.info("앨범에 포함된 사진 삭제 완료");

        // 앨범에 포함된 사진 삭제
        writingPhotoRepository.deleteByAlbumId(albumId);
        logger.info("앨범에 포함된 사진 삭제 완료");

        // 앨범에 포함된 일기 삭제
        writingRepository.deleteByAlbumId(albumId);
        logger.info("앨범에 포함된 일기 삭제 완료");

        // 앨범 삭제
        albumRepository.delete(album);
        logger.info("앨범 삭제 완료 ID: {}", albumId);
        logger.info("앨범과 그 안의 일기, 사진이 모두 삭제되었습니다.");
    }


    /**
     * 사용자의 모든 앨범 조회 메서드
     *
     * @param userId 사용자 ID
     * @return 사용자의 모든 앨범 리스트
     */
    public List<AlbumDTO> getUserAlbums(Integer userId) {
        logger.info("ID가 {}인 사용자의 앨범을 조회합니다.", userId);

        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));
        logger.info("사용자를 찾았습니다: {}", user);

        // 사용자의 앨범 조회
        List<Album> albums = albumRepository.findByUser(user);
        return albums.stream().map(this::convertToAlbumDTO).collect(Collectors.toList());
    }

    /**
     * 임의의 앨범 이름 생성 메서드
     *
     * @param user 사용자
     * @return 생성된 앨범 이름
     */
    private String generateAlbumName(Users user) {
        long albumCount = albumRepository.countByUser(user);
        return "여행일기 " + (albumCount + 1);
    }


    private AlbumDTO convertToAlbumDTO(Album album) {
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setId(album.getId());
        albumDTO.setName(album.getName());
        albumDTO.setStartDate(album.getStartDate());
        albumDTO.setFinalDate(album.getFinalDate());
        albumDTO.setCreatedAt(album.getCreatedAt());
        albumDTO.setUpdatedAt(album.getUpdatedAt());

        WritingPhoto representativePhoto = writingPhotoRepository.findTopByAlbumAndRepresentativeImageTrue(album);
        if (representativePhoto != null) {
            albumDTO.setRepresentativeImage(representativePhoto.getPhoto());
        } else {
            albumDTO.setRepresentativeImage(representativeImageUrl);
        }

        return albumDTO;
    }

    //기본 이미지를 설정하는 메서드
    private void setDefaultRepresentativeImage(Album album) {
        WritingPhoto defaultPhoto = new WritingPhoto();
        defaultPhoto.setPhoto(representativeImageUrl);
        defaultPhoto.setAlbum(album);
        defaultPhoto.setRepresentativeImage(true);

        writingPhotoRepository.save(defaultPhoto);
    }

}