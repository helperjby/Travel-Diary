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

    @Transactional
    public LikeDTO likeStatus(int writeId, int userId) {
        // 사용자 및 일기 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        WritingDiary write = writingRepository.findById(writeId)
                .orElseThrow(() -> new RuntimeException("WritingDiary not found: " + writeId));

        // 기존 좋아요 조회
        Like existingLike = likeRepository.findByUserAndWrite(user, write)
                .orElse(null);

        if (existingLike == null) {
            // 좋아요를 처음 누르는 경우
            Like newLike = new Like();
            newLike.setUser(user);
            newLike.setWrite(write);
            newLike.setCreatedAt(LocalDateTime.now());
            newLike.setLikeCount(1);
            newLike.setLiked(true);
            likeRepository.save(newLike);
            write.setLikeCount(write.getLikeCount() + 1);  // WritingDiary의 좋아요 횟수 업데이트
            writingRepository.save(write);
            return convertToDTO(newLike);
        } else {
            // 이미 좋아요를 눌렀던 경우
            if (existingLike.isLiked()) {
                existingLike.setLiked(false);
                if (write.getLikeCount() > 0) {
                    existingLike.setLikeCount(existingLike.getLikeCount() - 1);
                    write.setLikeCount(write.getLikeCount() - 1);  // WritingDiary의 좋아요 횟수 업데이트
                }
            } else {
                existingLike.setLiked(true);
                existingLike.setLikeCount(existingLike.getLikeCount() + 1);
                write.setLikeCount(write.getLikeCount() + 1);  // WritingDiary의 좋아요 횟수 업데이트
            }
            likeRepository.save(existingLike);
            writingRepository.save(write);
            return convertToDTO(existingLike);
        }
    }

    public LikeDTO getLikeStatus(int writeId, int userId) {
        // 사용자 및 일기 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        WritingDiary write = writingRepository.findById(writeId)
                .orElseThrow(() -> new RuntimeException("WritingDiary not found: " + writeId));

        // 기존 좋아요 조회
        Optional<Like> existingLike = likeRepository.findByUserAndWrite(user, write);

        // 좋아요가 없을 경우에도 기본값 반환
        LikeDTO likeDTO;
        if (existingLike.isPresent()) {
            likeDTO = convertToDTO(existingLike.get());
        } else {
            likeDTO = new LikeDTO();
            likeDTO.setWriteId(writeId);
            likeDTO.setUserId(userId);
            likeDTO.setLikeCount(0);  // likeCount 0으로 설정
            likeDTO.setLiked(false);  // liked false로 설정
            likeDTO.setId(0); // id를 설정하지 않음
        }

        return likeDTO;
    }

    private LikeDTO convertToDTO(Like like) {
        LikeDTO likeDTO = new LikeDTO();
        likeDTO.setId(like.getId());
        likeDTO.setUserId(like.getUser().getId());
        likeDTO.setWriteId(like.getWrite().getId());
        likeDTO.setCreatedAt(like.getCreatedAt());
        likeDTO.setLikeCount(like.getLikeCount());
        likeDTO.setLiked(like.isLiked());
        return likeDTO;
    }
}
