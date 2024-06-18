package com.traveldiary.be.repository;

import com.traveldiary.be.entity.Album;
import com.traveldiary.be.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Integer> {
    List<Album> findByUserAndStartDateAndFinalDate(Users user, LocalDate startDate, LocalDate finalDate); // 여러 결과를 반환하도록 변경
    long countByUser(Users user); // 특정 사용자가 소유한 앨범의 개수
    List<Album> findByUser(Users user); // 특정 사용자가 소유한 모든 앨범
}
