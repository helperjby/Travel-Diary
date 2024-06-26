package com.traveldiary.be.controller;

import com.traveldiary.be.dto.AlbumDTO;
import com.traveldiary.be.dto.WritingPhotoDTO;
import com.traveldiary.be.service.AlbumService;
import com.traveldiary.be.service.WritingPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

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
     * 앨범에 해당하는 사진 조회 메서드
     *
     * @param albumId 앨범 ID
     * @param userId 사용자 ID
     * @return 앨범에 속한 모든 사진 리스트를 반환
     */
    @GetMapping("/{albumId}/photos")
    public ResponseEntity<List<WritingPhotoDTO>> getPhotosByAlbum(@PathVariable int albumId, @RequestParam Integer userId) {
        try {
            List<WritingPhotoDTO> photos = writingPhotoService.getPhotosByAlbum(albumId, userId);
            return ResponseEntity.ok(photos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }


    /**
     * 특정 사진의 상세 정보를 조회하는 메서드
     *
     * @param photoId 사진 ID
     * @param userId 사용자 ID
     * @return 사진의 상세 정보를 반환
     */
//    @GetMapping("/photos/{photoId}")
//    public ResponseEntity<WritingPhotoDTO> getPhotoById(@PathVariable int photoId, @RequestParam Integer userId) {
//        try {
//            WritingPhotoDTO photo = writingPhotoService.getPhotoById(photoId, userId);
//            return ResponseEntity.ok(photo);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }

    /**
     * 특정 사진의 상세 정보를 조회하는 메서드
     *
     * @param photoId 사진 ID
     * @param userId 사용자 ID
     * @return 사진의 상세 정보를 반환
     */
    @GetMapping("/photos/{photoId}")
    public ResponseEntity<WritingPhotoDTO> getPhotoById(@PathVariable int photoId, @RequestParam Integer userId) {
        try {
            WritingPhotoDTO photo = writingPhotoService.getPhotoById(photoId, userId);
            if (photo == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(photo);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}



