package com.traveldiary.be.service;

import com.traveldiary.be.dto.WritingPhotoDTO;
import com.traveldiary.be.repository.WritingPhotoRepository;
import com.traveldiary.be.repository.AlbumRepository;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.WritingPhoto;
import com.traveldiary.be.entity.Album;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class WritingPhotoService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private WritingPhotoRepository writingPhotoRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 파일 저장 메서드
     *
     * @param files 파일 목록
     * @param diary 일기 엔티티
     * @return 저장된 파일 이름 목록
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    public List<String> storeFiles(List<MultipartFile> files, WritingDiary diary) throws IOException {
        List<String> fileNames = new ArrayList<>();
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        // 업로드 디렉토리가 존재하지 않으면 생성
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String uniqueFileName = generateUniqueFileName(originalFilename, diary.getId());
            Path destinationPath = uploadPath.resolve(uniqueFileName).normalize();

            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            fileNames.add(uniqueFileName);
        }

        return fileNames;
    }

    /**
     * 고유한 파일 이름 생성 메서드
     *
     * @param originalFilename 원본 파일 이름
     * @param diaryId 일기 ID
     * @return 고유한 파일 이름
     */
    private String generateUniqueFileName(String originalFilename, Number diaryId) {
        String uniqueFileName = diaryId + "_" + UUID.randomUUID() + "_" + originalFilename;

        while (writingPhotoRepository.existsByPhoto(uniqueFileName)) {
            uniqueFileName = diaryId + "_" + UUID.randomUUID() + "_" + originalFilename;
        }

        return uniqueFileName;
    }

    /**
     * 파일 삭제 메서드
     *
     * @param fileName 파일 이름
     */
    public void deleteFile(String fileName) {
        Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 로그에 오류 기록
            e.printStackTrace();
        }
    }

    /**
     * 앨범에 해당하는 사진 조회 메서드
     *
     * @param albumId 앨범 ID
     * @param userId 사용자 ID
     * @return 사진 목록
     */
    public List<WritingPhoto> getPhotosByAlbum(int albumId, int userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));
        if (!album.getUser().equals(user)) {
            throw new RuntimeException("사용자 ID가 앨범의 소유자가 아닙니다.");
        }

        List<WritingPhoto> photos = new ArrayList<>();
        for (WritingDiary diary : album.getWritingDiaries()) {
            photos.addAll(diary.getWritingPhoto());
        }
        return photos;
    }


    /**
     * 특정 사진의 상세 정보를 조회하는 메서드
     *
     * @param photoId 사진 ID
     * @param userId 사용자 ID
     * @return 사진의 상세 정보를 담은 WritingPhotoDTO
     */
    public WritingPhotoDTO getPhotoById(int photoId, int userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        WritingPhoto photo = writingPhotoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("사진을 찾을 수 없습니다."));
        if (!photo.getWritingDiary().getUser().equals(user)) {
            throw new RuntimeException("사용자 ID가 사진의 소유자가 아닙니다.");
        }
        return convertToDTO(photo);
    }

    /**
     * WritingPhoto 엔티티를 WritingPhotoDTO로 변환하는 메서드
     *
     * @param photo WritingPhoto 엔티티
     * @return 변환된 WritingPhotoDTO
     */
    private WritingPhotoDTO convertToDTO(WritingPhoto photo) {
        WritingPhotoDTO photoDTO = new WritingPhotoDTO();
        photoDTO.setPhoto(photo.getPhoto());
        photoDTO.setPhotoId(photo.getId());
        photoDTO.setRepresentativeImage(photo.getRepresentativeImage());
        return photoDTO;
    }
}
