package com.traveldiary.be.repository;

import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WritingRepository extends JpaRepository<WritingDiary, Integer> {
    List<WritingDiary> findByUser(Users user); // 사용자의 모든 일기 조회
    List<WritingDiary> findByTravelDateAndUser(LocalDate travelDate, Users user); // 특정 날짜에 작성된 사용자의 일기
    List<WritingDiary> findByTravelDateBetweenAndUser(LocalDate startDate, LocalDate endDate, Users user); // 특정 기간 동안 작성된 사용자의 일기
    List<WritingDiary> findByIsPublicTrue(); // 공개된 모든 일기 조회
    List<WritingDiary> findByAlbum(Album album);  // 특정 앨범에 포함된 일기 조회
    void deleteByAlbum(Album album); // 특정 앨범에 포함된 모든 일기 삭제

    Optional<WritingDiary> findTopByUserOrderByCreatedAtDesc(Users user);// 사용자의 가장 최근 일기 조회

    @Query("SELECT MAX(w.id) FROM WritingDiary w")
    Integer findMaxId();//최대 id를 찾는 메서드
}
