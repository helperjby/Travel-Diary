package com.traveldiary.be.repository;

import com.traveldiary.be.entity.Locations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Locations, Long> {
    List<Locations> findAllByRecordTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Locations> findAllByUserIdAndRecordTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
