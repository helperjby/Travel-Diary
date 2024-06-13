package com.traveldiary.be.repository;

import com.traveldiary.be.entity.Album;
import com.traveldiary.be.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Integer> {
    Optional<Album> findByUserAndStartDateAndFinalDate(Users user, LocalDate startDate, LocalDate finalDate);
    long countByUser(Users user);

    List<Album> findByUser(Users user);
}
