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

    @Transactional
    public AlbumDTO updateAlbumName(int albumId, String name, String providerId) {
        logger.info("Updating album with ID: {} with new name: {}", albumId, name);

        Users user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + providerId));
        logger.info("User found: {}", user);

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found: " + albumId));
        logger.info("Album found: {}", album);

        if (!album.getUser().equals(user)) {
            logger.warn("Unauthorized attempt to update album by user: {}", user);
            throw new RuntimeException("Unauthorized");
        }

        album.setName(name);
        album.setUpdatedAt(LocalDateTime.now());
        Album updatedAlbum = albumRepository.save(album);
        logger.info("Album updated: {}", updatedAlbum);

        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setId(updatedAlbum.getId());
        albumDTO.setName(updatedAlbum.getName());
        albumDTO.setStartDate(updatedAlbum.getStartDate());
        albumDTO.setFinalDate(updatedAlbum.getFinalDate());
        albumDTO.setCreatedAt(updatedAlbum.getCreatedAt());
        albumDTO.setUpdatedAt(updatedAlbum.getUpdatedAt());

        return albumDTO;
    }

    @Transactional
    public AlbumDTO createAlbum(AlbumDTO request, String providerId) {
        logger.info("Creating new album with name: {}", request.getName());

        Users user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + providerId));
        logger.info("User found: {}", user);

        Album album = new Album();
        album.setName(request.getName());
        album.setStartDate(request.getStartDate());
        album.setFinalDate(request.getFinalDate());
        album.setUser(user);
        album.setCreatedAt(LocalDateTime.now());

        Album savedAlbum = albumRepository.save(album);
        logger.info("Album created: {}", savedAlbum);

        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setId(savedAlbum.getId());
        albumDTO.setName(savedAlbum.getName());
        albumDTO.setStartDate(savedAlbum.getStartDate());
        albumDTO.setFinalDate(savedAlbum.getFinalDate());
        albumDTO.setCreatedAt(savedAlbum.getCreatedAt());
        albumDTO.setUpdatedAt(savedAlbum.getUpdatedAt());

        return albumDTO;
    }

    @Transactional
    public void deleteAlbum(int albumId, String providerId) {
        logger.info("Deleting album with ID: {}", albumId);

        Users user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + providerId));
        logger.info("User found: {}", user);

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found: " + albumId));
        logger.info("Album found: {}", album);

        if (!album.getUser().equals(user)) {
            logger.warn("Unauthorized attempt to delete album by user: {}", user);
            throw new RuntimeException("Unauthorized");
        }

        writingRepository.deleteByAlbum(album);
        albumRepository.delete(album);
        logger.info("Album and its diaries deleted successfully");
    }

    public List<AlbumDTO> getUserAlbums(String providerId) {
        logger.info("Fetching albums for user with providerId: {}", providerId);

        Users user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + providerId));
        logger.info("User found: {}", user);

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
}
