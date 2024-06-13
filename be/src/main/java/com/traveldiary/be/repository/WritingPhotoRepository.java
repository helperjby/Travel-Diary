package com.traveldiary.be.repository;

import com.traveldiary.be.entity.WritingPhoto;
import com.traveldiary.be.entity.WritingDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WritingPhotoRepository extends JpaRepository<WritingPhoto, Long> {
    List<WritingPhoto> findByDiary(WritingDiary diary);
    WritingPhoto findByPhoto(String photo);
}
