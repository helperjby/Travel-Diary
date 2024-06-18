package com.traveldiary.be.service;

import com.traveldiary.be.dto.AlbumDTO;
import com.traveldiary.be.entity.Album;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.repository.AlbumRepository;
import com.traveldiary.be.repository.UserRepository;
import com.traveldiary.be.repository.WritingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private static final Logger logger = LoggerFactory.getLogger(AlbumService.class);

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WritingRepository writingRepository;

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
        logger.info("Updating album with ID: {} with new name: {}", albumId, name);

        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        logger.info("User found: {}", user);

        // 앨범 조회
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found: " + albumId));
        logger.info("Album found: {}", album);

        // 사용자가 앨범의 소유자인지 확인
        if (!album.getUser().equals(user)) {
            logger.warn("Unauthorized attempt to update album by user: {}", user);
            throw new RuntimeException("Unauthorized");
        }

        // 앨범 이름이 비어 있거나 null인 경우 임의의 이름 설정
        if (name == null || name.trim().isEmpty()) {
            name = generateAlbumName(user);
        }

        // 앨범 이름 업데이트
        album.setName(name);
        album.setUpdatedAt(LocalDateTime.now());
        Album updatedAlbum = albumRepository.save(album);
        logger.info("Album updated: {}", updatedAlbum);

        // 업데이트된 앨범 DTO 반환
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setId(updatedAlbum.getId());
        albumDTO.setName(updatedAlbum.getName());
        albumDTO.setStartDate(updatedAlbum.getStartDate());
        albumDTO.setFinalDate(updatedAlbum.getFinalDate());
        albumDTO.setCreatedAt(updatedAlbum.getCreatedAt());
        albumDTO.setUpdatedAt(updatedAlbum.getUpdatedAt());

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
        logger.info("Creating new album with name: {}", request.getName());

        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        logger.info("User found: {}", user);

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
        logger.info("Album created: {}", savedAlbum);

        // 생성된 앨범 DTO 반환
        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setId(savedAlbum.getId());
        albumDTO.setName(savedAlbum.getName());
        albumDTO.setStartDate(savedAlbum.getStartDate());
        albumDTO.setFinalDate(savedAlbum.getFinalDate());
        albumDTO.setCreatedAt(savedAlbum.getCreatedAt());
        albumDTO.setUpdatedAt(savedAlbum.getUpdatedAt());

        return albumDTO;
    }

    /**
     * 앨범 삭제 메서드
     *
     * @param albumId    앨범 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteAlbum(int albumId, Integer userId) {
        logger.info("Deleting album with ID: {}", albumId);

        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        logger.info("User found: {}", user);

        // 앨범 조회
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found: " + albumId));
        logger.info("Album found: {}", album);

        // 사용자가 앨범의 소유자인지 확인
        if (!album.getUser().equals(user)) {
            logger.warn("Unauthorized attempt to delete album by user: {}", user);
            throw new RuntimeException("Unauthorized");
        }

        // 앨범과 해당 앨범의 일기 삭제
        writingRepository.deleteByAlbum(album);
        albumRepository.delete(album);
        logger.info("Album and its diaries deleted successfully");
    }

    /**
     * 사용자의 모든 앨범 조회 메서드
     *
     * @param userId 사용자 ID
     * @return 사용자의 모든 앨범 리스트
     */
    public List<AlbumDTO> getUserAlbums(Integer userId) {
        logger.info("Fetching albums for user with ID: {}", userId);

        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        logger.info("User found: {}", user);

        // 사용자의 앨범 조회
        List<Album> albums = albumRepository.findByUser(user);
        return albums.stream().map(album -> {
            AlbumDTO albumDTO = new AlbumDTO();
            albumDTO.setId(album.getId());
            albumDTO.setName(album.getName());
            albumDTO.setStartDate(album.getStartDate());
            albumDTO.setFinalDate(album.getFinalDate());
            albumDTO.setCreatedAt(album.getCreatedAt());
            albumDTO.setUpdatedAt(album.getUpdatedAt());
            return albumDTO;
        }).collect(Collectors.toList());
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
}
