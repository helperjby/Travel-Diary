package com.traveldiary.be.repository;

import com.traveldiary.be.entity.WritingPhoto;
import com.traveldiary.be.entity.WritingDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface WritingPhotoRepository extends JpaRepository<WritingPhoto, Integer> {
    List<WritingPhoto> findByWritingDiary(WritingDiary writingDiary);//특정 일기에 첨부된 모든 사진 조회
    WritingPhoto findByPhoto(String photo);//사진 URL로 사진 조회
    boolean existsByPhoto(String photo);

    //앨범의 여행 기간에 해당하는 사진들을 조회
    @Query("SELECT wp FROM WritingPhoto wp JOIN wp.writingDiary d WHERE d.album.id = :albumId AND d.startDate >= :startDate AND d.finalDate <= :endDate")
    List<WritingPhoto> findPhotosByAlbumAndDateRange(@Param("albumId") int albumId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
