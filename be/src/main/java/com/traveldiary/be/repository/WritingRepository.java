package com.traveldiary.be.repository;

import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WritingRepository extends JpaRepository<WritingDiary, Integer> {
    List<WritingDiary> findByUser(Users user);
    List<WritingDiary> findByTravelDateAndUser(LocalDate travelDate, Users user);
    List<WritingDiary> findByTravelDateBetweenAndUser(LocalDate startDate, LocalDate endDate, Users user);
    List<WritingDiary> findByIsPublicTrue();
    List<WritingDiary> findByAlbum(Album album);  // 앨범에 포함된 일기 조회
    void deleteByAlbum(Album album);
}
