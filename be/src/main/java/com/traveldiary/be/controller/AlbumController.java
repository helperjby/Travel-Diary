package com.traveldiary.be.controller;

import com.traveldiary.be.dto.AlbumDTO;
import com.traveldiary.be.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    /**
     * 앨범 이름 변경 메서드
     *
     * @param albumId 앨범 ID
     * @param name 새 앨범 이름
     * @param providerId 사용자 소셜 아이디
     * @return 업데이트된 앨범 엔터티와 함께 응답
     */
    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumDTO> updateAlbumName(@PathVariable int albumId, @RequestParam String name, @RequestParam String providerId) {
        try {
            AlbumDTO updatedAlbum = albumService.updateAlbumName(albumId, name, providerId);
            return ResponseEntity.ok(updatedAlbum);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * 새로운 앨범 생성 메서드
     *
     * @param request 앨범 생성 요청 데이터
     * @param providerId 사용자 소셜 아이디
     * @return 생성된 앨범 엔터티와 함께 응답
     */
    @PostMapping
    public ResponseEntity<AlbumDTO> createAlbum(@RequestBody AlbumDTO request, @RequestParam String providerId) {
        try {
            AlbumDTO createdAlbum = albumService.createAlbum(request, providerId);
            return ResponseEntity.ok(createdAlbum);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * 앨범 삭제 메서드
     *
     * @param albumId 앨범 ID
     * @param providerId 사용자 소셜 아이디
     * @return 삭제 상태
     */
    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable int albumId, @RequestParam String providerId) {
        try {
            albumService.deleteAlbum(albumId, providerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 사용자의 모든 앨범 조회 메서드
     *
     * @param providerId 사용자 소셜 아이디
     * @return 사용자의 모든 앨범 리스트
     */
    @GetMapping
    public ResponseEntity<List<AlbumDTO>> getUserAlbums(@RequestParam String providerId) {
        try {
            List<AlbumDTO> albums = albumService.getUserAlbums(providerId);
            return ResponseEntity.ok(albums);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
