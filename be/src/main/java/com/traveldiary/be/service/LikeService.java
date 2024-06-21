package com.traveldiary.be.service;

import com.traveldiary.be.dto.LikeDTO;
import com.traveldiary.be.entity.Like;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.repository.LikeRepository;
import com.traveldiary.be.repository.UserRepository;
import com.traveldiary.be.repository.WritingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WritingRepository writingRepository;

    /**
     * 사용자의 특정 일기에 대한 좋아요 상태를 업데이트
     *
     * @param writingDiaryId 일기 ID
     * @param userId 사용자 ID
     * @return 업데이트된 좋아요 상태를 담은 LikeDTO
     */
    @Transactional
    public LikeDTO likeStatus(int writingDiaryId, int userId) {
        // 사용자 및 일기 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        WritingDiary writingDiary = writingRepository.findById(writingDiaryId)
                .orElseThrow(() -> new RuntimeException("WritingDiary not found: " + writingDiaryId));

        // 기존 좋아요 조회
        Like existingLike = likeRepository.findByUserAndWritingDiary(user, writingDiary)
                .orElse(null);

        if (existingLike == null) {
            // 좋아요를 처음 누르는 경우
            Like newLike = new Like();
            newLike.setUser(user);
            newLike.setWritingDiary(writingDiary);
            newLike.setCreatedAt(LocalDateTime.now());
            newLike.setLiked(true);
            likeRepository.save(newLike);
            return convertToDTO(newLike);
        } else {
            // 이미 좋아요를 눌렀던 경우
            existingLike.setLiked(!existingLike.isLiked());
            likeRepository.save(existingLike);
            return convertToDTO(existingLike);
        }
    }

    /**
     * 사용자가 특정 일기에 대한 좋아요 상태를 조회
     *
     * @param writingDiaryId 일기 ID
     * @param userId 사용자 ID
     * @return 좋아요 상태를 담은 LikeDTO
     */
    public LikeDTO getLikeStatus(int writingDiaryId, int userId) {
        // 사용자 및 일기 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        WritingDiary writingDiary = writingRepository.findById(writingDiaryId)
                .orElseThrow(() -> new RuntimeException("WritingDiary not found: " + writingDiaryId));

        // 기존 좋아요 조회
        Optional<Like> existingLike = likeRepository.findByUserAndWritingDiary(user, writingDiary);

        LikeDTO likeDTO;
        if (existingLike.isPresent()) {
            likeDTO = convertToDTO(existingLike.get());
        } else {
            // 좋아요가 없을 경우 기본값 반환
            likeDTO = new LikeDTO();
            likeDTO.setWritingDiaryId(writingDiaryId);
            likeDTO.setUserId(userId);
            likeDTO.setLikeCount(0);
            likeDTO.setLiked(false);
            likeDTO.setId(0);
        }

        return likeDTO;
    }

    /**
     * Like 엔티티를 LikeDTO로 변환
     *
     * @param like Like 엔티티
     * @return 변환된 LikeDTO
     */
    private LikeDTO convertToDTO(Like like) {
        LikeDTO likeDTO = new LikeDTO();
        likeDTO.setId(like.getId());
        likeDTO.setUserId(like.getUser().getId());
        likeDTO.setWritingDiaryId(like.getWritingDiary().getId());
        likeDTO.setCreatedAt(like.getCreatedAt());
        likeDTO.setLikeCount((int) likeRepository.countByWritingDiary(like.getWritingDiary()));
        likeDTO.setLiked(like.isLiked());
        return likeDTO;
    }
}
