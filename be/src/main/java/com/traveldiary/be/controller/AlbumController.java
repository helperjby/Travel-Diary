package com.traveldiary.be.controller;

import com.traveldiary.be.dto.AlbumDTO;
import com.traveldiary.be.entity.WritingPhoto;
import com.traveldiary.be.service.AlbumService;
import com.traveldiary.be.service.WritingPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @Autowired
    private WritingPhotoService writingPhotoService; // WritingPhotoService 주입

    /**
     * 앨범 이름 업데이트 메서드
     *
     * @param albumId 앨범 ID
     * @param name 새로운 앨범 이름
     * @param userId 사용자 ID
     * @return 업데이트된 앨범 정보를 반환
     */
    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumDTO> updateAlbumName(@PathVariable int albumId, @RequestParam String name, @RequestParam Integer userId) {
        try {
            AlbumDTO updatedAlbum = albumService.updateAlbumName(albumId, name, userId);
            return ResponseEntity.ok(updatedAlbum);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * 새로운 앨범 생성 메서드
     *
     * @param request 앨범 생성 요청 데이터
     * @param userId 사용자 ID
     * @return 생성된 앨범 정보를 반환
     */
    @PostMapping
    public ResponseEntity<AlbumDTO> createAlbum(@RequestBody AlbumDTO request, @RequestParam Integer userId) {
        try {
            AlbumDTO createdAlbum = albumService.createAlbum(request, userId);
            return ResponseEntity.ok(createdAlbum);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * 앨범 삭제 메서드
     *
     * @param albumId 앨범 ID
     * @param userId 사용자 ID
     * @return 성공적으로 삭제되었음을 나타내는 응답
     */
    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable int albumId, @RequestParam Integer userId) {
        try {
            albumService.deleteAlbum(albumId, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 사용자의 모든 앨범 조회 메서드
     *
     * @param userId 사용자 ID
     * @return 사용자의 모든 앨범 리스트를 반환
     */
    @GetMapping
    public ResponseEntity<List<AlbumDTO>> getUserAlbums(@RequestParam Integer userId) {
        try {
            List<AlbumDTO> albums = albumService.getUserAlbums(userId);
            return ResponseEntity.ok(albums);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * 앨범의 여행 기간에 해당하는 사진들을 조회하는 메서드
     *
     * @param albumId 앨범 ID
     * @return 앨범에 속한 모든 사진 리스트를 반환
     */
    @GetMapping("/{albumId}/photos")
    public ResponseEntity<List<WritingPhoto>> getPhotosByAlbum(@PathVariable int albumId) {
        try {
            List<WritingPhoto> photos = writingPhotoService.getPhotosByAlbum(albumId);
            return ResponseEntity.ok(photos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
