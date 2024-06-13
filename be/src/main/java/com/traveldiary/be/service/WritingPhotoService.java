package com.traveldiary.be.service;

import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.WritingPhoto;
import com.traveldiary.be.repository.WritingPhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WritingPhotoService {

    private final WritingPhotoRepository writingPhotoRepository;

    @Autowired
    public WritingPhotoService(WritingPhotoRepository writingPhotoRepository) {
        this.writingPhotoRepository = writingPhotoRepository;
    }

    /**
     * 고유한 파일 이름을 생성하는 메서드
     *
     * @param originalFileName 원본 파일 이름
     * @return 고유한 파일 이름
     */
    public String generateUniqueFileName(String originalFileName) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());

        int lastDotIndex = originalFileName.lastIndexOf('.');
        String fileNameWithoutExtension = lastDotIndex != -1 ? originalFileName.substring(0, lastDotIndex) : originalFileName;
        String fileExtension = lastDotIndex != -1 ? originalFileName.substring(lastDotIndex) : "";

        return fileNameWithoutExtension + "_" + timestamp + fileExtension;
    }

    /**
     * 파일을 삭제하는 메서드
     *
     * @param fileName 삭제할 파일 이름
     */
    public void deleteFile(String fileName) {
        String uploadDir = System.getProperty("user.home") + "/Desktop/uploads"; // 바탕화면의 uploads 디렉토리 경로 설정
        File file = new File(uploadDir + File.separator + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 사진 ID로 사진을 삭제하는 메서드
     *
     * @param photoId 삭제할 사진 ID
     */
    public void deletePhotoById(Long photoId) {
        writingPhotoRepository.deleteById(photoId);
    }

    /**
     * 파일을 저장하고 저장된 파일의 정보를 반환하는 메서드
     *
     * @param file  저장할 파일
     * @param diary 연관된 다이어리 엔터티
     * @return 저장된 파일의 정보
     */
    public FileUploadResponse storeFile(MultipartFile file, WritingDiary diary) {
        String uploadDir = System.getProperty("user.home") + "/Desktop/uploads"; // 바탕화면의 uploads 디렉토리 경로 설정

        if (file.isEmpty()) {
            throw new RuntimeException("Failed to store empty file.");
        }

        try {
            String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
            Path uploadPath = Paths.get(uploadDir, uniqueFileName);
            Files.createDirectories(uploadPath.getParent());

            // 파일을 저장할 경로 설정
            file.transferTo(uploadPath.toFile());

            // WritingPhoto 엔티티 생성 및 저장
            WritingPhoto writingPhoto = new WritingPhoto();
            writingPhoto.setPhoto(uniqueFileName);
            writingPhoto.setDiary(diary);  // 다이어리와 연관 설정
            writingPhoto = writingPhotoRepository.save(writingPhoto);

            // FileUploadResponse 객체 생성 후 photoId와 uniqueFileName 설정
            return new FileUploadResponse(writingPhoto.getPhotoId(), uniqueFileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    /**
     * 여러 파일을 저장하고 저장된 파일들의 정보를 반환하는 메서드
     *
     * @param files 저장할 파일 배열
     * @param diary 연관된 다이어리 엔터티
     * @return 저장된 파일들의 정보 리스트
     */
    public List<FileUploadResponse> storeFiles(MultipartFile[] files, WritingDiary diary) {
        List<FileUploadResponse> fileInfos = new ArrayList<>();
        for (MultipartFile file : files) {
            FileUploadResponse fileInfo = storeFile(file, diary);
            fileInfos.add(fileInfo);
        }
        return fileInfos;
    }
}
