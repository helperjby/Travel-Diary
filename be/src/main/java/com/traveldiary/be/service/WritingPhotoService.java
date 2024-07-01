//s3에 사진 저장

package com.traveldiary.be.service;

import com.traveldiary.be.dto.WritingPhotoDTO;
import com.traveldiary.be.entity.Album;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.entity.WritingPhoto;
import com.traveldiary.be.repository.AlbumRepository;
import com.traveldiary.be.repository.UserRepository;
import com.traveldiary.be.repository.WritingPhotoRepository;
import com.traveldiary.be.repository.WritingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WritingPhotoService {

    private static final Logger logger = LoggerFactory.getLogger(WritingPhotoService.class);

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.upload-dir}")
    private String uploadDir;

    @Value("${representative.image-url}")
    private String representativeImageUrl;

    @Autowired
    private WritingPhotoRepository writingPhotoRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WritingRepository writingRepository;

    @Autowired
    private S3Client s3Client;

    /**
     * 파일을 저장하는 메서드
     *
     * @param files 저장할 파일 리스트
     * @param diary 일기 엔티티
     * @return 저장된 파일 URL 리스트
     * @throws IOException 파일 저장 중 발생할 수 있는 예외 (예: S3 업로드 오류)
     */
    public List<String> storeFiles(List<MultipartFile> files, WritingDiary diary) throws IOException {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                fileUrls.add(representativeImageUrl); // 기본 이미지를 추가
                continue;
            }

            String originalFilename = file.getOriginalFilename();
            String uniqueFileName = generateUniqueFileName(originalFilename, diary.getId());
            String contentType = getContentType(originalFilename);

            // S3에 파일 업로드
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(uploadDir + uniqueFileName)
                            .contentType(contentType)// Content-Type 설정
                            .contentDisposition("inline")
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            String fileUrl = String.format("https://%s.s3.amazonaws.com/%s%s", bucketName, uploadDir, uniqueFileName);
            fileUrls.add(fileUrl);
        }

        return fileUrls;
    }

    /**
     * 파일의 Content-Type을 결정하는 메서드
     *
     * @param fileName 파일 이름
     * @return Content-Type 문자열
     */
    private String getContentType(String fileName) {
        String contentType;
        String fileExtension = getFileExtension(fileName);

        switch (fileExtension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
                contentType = "image/jpeg";
                break;
            case ".png":
                contentType = "image/png";
                break;
            case ".gif":
                contentType = "image/gif";
                break;
            default:
                contentType = "application/octet-stream";
                break;
        }

        return contentType;
    }

    /**
     * 파일의 확장자를 추출하는 메서드
     *
     * @param filename 파일 이름
     * @return 파일 확장자
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        return (lastDotIndex != -1) ? filename.substring(lastDotIndex) : "";
    }






    /**
     * 고유한 파일 이름을 생성하는 메서드
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
     * 파일을 삭제하는 메서드
     *
     * @param fileUrl 삭제할 파일 URL
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl != null && !fileUrl.isEmpty()) {
            String key = fileUrl.substring(fileUrl.indexOf(uploadDir));
            try {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build());
            } catch (Exception e) {
                logger.error("파일 삭제 실패: " + fileUrl, e);
            }
        }
    }

    /**
     * 일기에 사진을 추가하는 메서드
     *
     * @param diaryId 일기 ID
     * @param userId 사용자 ID
     * @param photos 추가할 사진 리스트
     * @return 추가된 사진 DTO 리스트
     * @throws IOException 파일 저장 중 발생할 수 있는 예외 (예: S3 업로드 오류)
     */
    public List<WritingPhotoDTO> addPhotosToDiary(int diaryId, int userId, List<MultipartFile> photos) throws IOException {
        WritingDiary diary = writingRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다."));

        List<WritingPhoto> writingPhotos = new ArrayList<>();
        if (photos != null && !photos.isEmpty()) {
            List<String> photoNames = storeFiles(photos, diary);
            writingPhotos = photoNames.stream()
                    .map(photoName -> {
                        WritingPhoto writingPhoto = new WritingPhoto();
                        writingPhoto.setPhoto(photoName);
                        writingPhoto.setWritingDiary(diary);
                        writingPhoto.setAlbum(diary.getAlbum());
                        writingPhoto.setRepresentativeImage(false);
                        return writingPhoto;
                    })
                    .collect(Collectors.toList());

            if (!writingPhotos.isEmpty()) {
                writingPhotos.get(0).setRepresentativeImage(true);
            }

            writingPhotoRepository.saveAll(writingPhotos);
        } else {
            WritingPhoto defaultPhoto = new WritingPhoto();
            defaultPhoto.setPhoto(representativeImageUrl);
            defaultPhoto.setWritingDiary(diary);
            defaultPhoto.setAlbum(diary.getAlbum());
            defaultPhoto.setRepresentativeImage(true);
            writingPhotos.add(defaultPhoto);
            writingPhotoRepository.save(defaultPhoto);
        }

        return writingPhotos.stream()
                .map(wp -> new WritingPhotoDTO(wp.getPhoto(), wp.getId(), wp.getRepresentativeImage()))
                .collect(Collectors.toList());
    }

    /**
     * 일기에 있는 사진을 수정하는 메서드
     *
     * @param diaryId 일기 ID
     * @param userId 사용자 ID
     * @param photos 수정할 사진 리스트
     * @return 수정된 사진 DTO 리스트
     * @throws IOException 파일 저장 중 발생할 수 있는 예외 (예: S3 업로드 오류)
     */
    public List<WritingPhotoDTO> updatePhotosInDiary(int diaryId, int userId, List<MultipartFile> photos) throws IOException {
        WritingDiary diary = writingRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다."));

        if (diary.getWritingPhoto() != null && !diary.getWritingPhoto().isEmpty()) {
            List<WritingPhoto> existingPhotos = new ArrayList<>(diary.getWritingPhoto());
            for (WritingPhoto existingPhoto : existingPhotos) {
                deleteFile(existingPhoto.getPhoto());
                writingPhotoRepository.delete(existingPhoto);
            }
            diary.getWritingPhoto().clear();
        }

        return addPhotosToDiary(diaryId, userId, photos);
    }


    /**
     * 앨범에 속한 사진을 조회하는 메서드
     *
     * @param albumId 앨범 ID
     * @param userId 사용자 ID
     * @return 앨범에 속한 사진 DTO 리스트
     */
    public List<WritingPhotoDTO> getPhotosByAlbum(int albumId, Integer userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Album album = albumRepository.findById(albumId).orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));
        if (!album.getUser().equals(user)) {
            throw new RuntimeException("사용자 ID가 앨범의 소유자가 아닙니다.");
        }

        List<WritingPhotoDTO> photoDTOs = new ArrayList<>();
        for (WritingDiary diary : album.getWritingDiaries()) {
            List<WritingPhoto> photos = diary.getWritingPhoto();
            if (photos == null || photos.isEmpty()) {
                WritingPhotoDTO defaultPhotoDTO = new WritingPhotoDTO(representativeImageUrl, 0, true);
                photoDTOs.add(defaultPhotoDTO);
            } else {
                for (WritingPhoto photo : photos) {
                    WritingPhotoDTO photoDTO = new WritingPhotoDTO(
                            photo.getPhoto() != null ? photo.getPhoto() : representativeImageUrl,
                            photo.getId(),
                            photo.getRepresentativeImage()
                    );
                    photoDTOs.add(photoDTO);
                }
            }
        }
        return photoDTOs;
    }


    /**
     * 사진 ID로 사진을 조회하는 메서드
     *
     * @param photoId 사진 ID
     * @param userId  사용자 ID
     * @return 사진 DTO
     */
    public WritingPhotoDTO getPhotoById(int photoId, Integer userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        WritingPhoto photo = writingPhotoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("사진을 찾을 수 없습니다."));
        if (!photo.getWritingDiary().getUser().equals(user)) {
            throw new RuntimeException("사용자 ID가 사진의 소유자가 아닙니다.");
        }
        return convertToDTO(photo);
    }

    /**
     * WritingPhoto 엔티티를 DTO로 변환하는 메서드
     *
     * @param photo 사진 엔티티
     * @return 사진 DTO
     */
    private WritingPhotoDTO convertToDTO(WritingPhoto photo) {
        WritingPhotoDTO photoDTO = new WritingPhotoDTO();
        photoDTO.setPhoto(photo.getPhoto());
        photoDTO.setPhotoId(photo.getId());
        photoDTO.setRepresentativeImage(photo.getRepresentativeImage());
        return photoDTO;
    }
}
















//로컬로 사진 저장

//package com.traveldiary.be.service;
//
//import com.traveldiary.be.dto.WritingPhotoDTO;
//import com.traveldiary.be.entity.Album;
//import com.traveldiary.be.entity.Users;
//import com.traveldiary.be.entity.WritingDiary;
//import com.traveldiary.be.entity.WritingPhoto;
//import com.traveldiary.be.repository.AlbumRepository;
//import com.traveldiary.be.repository.UserRepository;
//import com.traveldiary.be.repository.WritingPhotoRepository;
//import com.traveldiary.be.repository.WritingRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Service
//public class WritingPhotoService {
//
//    private static final Logger logger = LoggerFactory.getLogger(WritingPhotoService.class);
//
//    @Value("${file.upload-dir}")
//    private String uploadDir;
//
//    @Value("${representative.image-url}")
//    private String representativeImageUrl;
//
//    @Autowired
//    private WritingPhotoRepository writingPhotoRepository;
//
//    @Autowired
//    private AlbumRepository albumRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private WritingRepository writingRepository;
//
//    /**
//     * 파일을 저장하는 메서드
//     *
//     * @param files 저장할 파일 리스트
//     * @param diary 일기 엔티티
//     * @return 저장된 파일 이름 리스트
//     * @throws IOException 파일 저장 중 발생할 수 있는 예외
//     */
//    public List<String> storeFiles(List<MultipartFile> files, WritingDiary diary) throws IOException {
//        List<String> fileNames = new ArrayList<>();
//        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
//
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        for (MultipartFile file : files) {
//            if (file.isEmpty()) {
//                fileNames.add(representativeImageUrl); // 기본 이미지를 추가
//                continue;
//            }
//
//            String originalFilename = file.getOriginalFilename();
//            String uniqueFileName = generateUniqueFileName(originalFilename, diary.getId());
//            Path destinationPath = uploadPath.resolve(uniqueFileName).normalize();
//
//            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
//
//            fileNames.add(uniqueFileName);
//        }
//
//        return fileNames;
//    }
//
//    /**
//     * 고유한 파일 이름을 생성하는 메서드
//     *
//     * @param originalFilename 원본 파일 이름
//     * @param diaryId 일기 ID
//     * @return 고유한 파일 이름
//     */
//    private String generateUniqueFileName(String originalFilename, Number diaryId) {
//        String uniqueFileName = diaryId + "_" + UUID.randomUUID() + "_" + originalFilename;
//
//        while (writingPhotoRepository.existsByPhoto(uniqueFileName)) {
//            uniqueFileName = diaryId + "_" + UUID.randomUUID() + "_" + originalFilename;
//        }
//
//        return uniqueFileName;
//    }
//
//    /**
//     * 파일을 삭제하는 메서드
//     *
//     * @param fileName 삭제할 파일 이름
//     */
//    public void deleteFile(String fileName) {
//        if (fileName != null && !fileName.isEmpty()) {
//            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
//            try {
//                Files.deleteIfExists(filePath);
//            } catch (IOException e) {
//                logger.error("파일 삭제 실패: " + fileName, e);
//            }
//        }
//    }
//
//    /**
//     * 일기에 사진을 추가하는 메서드
//     *
//     * @param diaryId 일기 ID
//     * @param userId 사용자 ID
//     * @param photos 추가할 사진 리스트
//     * @return 추가된 사진 DTO 리스트
//     * @throws IOException 파일 저장 중 발생할 수 있는 예외
//     */
//    public List<WritingPhotoDTO> addPhotosToDiary(int diaryId, int userId, List<MultipartFile> photos) throws IOException {
//        WritingDiary diary = writingRepository.findById(diaryId)
//                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다."));
//
//        List<WritingPhoto> writingPhotos = new ArrayList<>();
//        if (photos != null && !photos.isEmpty()) {
//            List<String> photoNames = storeFiles(photos, diary);
//            writingPhotos = photoNames.stream()
//                    .map(photoName -> {
//                        WritingPhoto writingPhoto = new WritingPhoto();
//                        writingPhoto.setPhoto(photoName);
//                        writingPhoto.setWritingDiary(diary);
//                        writingPhoto.setAlbum(diary.getAlbum());
//                        writingPhoto.setRepresentativeImage(false);
//                        return writingPhoto;
//                    })
//                    .collect(Collectors.toList());
//
//            if (!writingPhotos.isEmpty()) {
//                writingPhotos.get(0).setRepresentativeImage(true);
//            }
//
//            writingPhotoRepository.saveAll(writingPhotos);
//        } else {
//            WritingPhoto defaultPhoto = new WritingPhoto();
//            defaultPhoto.setPhoto(representativeImageUrl);
//            defaultPhoto.setWritingDiary(diary);
//            defaultPhoto.setAlbum(diary.getAlbum());
//            defaultPhoto.setRepresentativeImage(true);
//            writingPhotos.add(defaultPhoto);
//            writingPhotoRepository.save(defaultPhoto);
//        }
//
//        return writingPhotos.stream()
//                .map(wp -> new WritingPhotoDTO(wp.getPhoto(), wp.getId(), wp.getRepresentativeImage()))
//                .collect(Collectors.toList());
//    }
//
//    /**
//     * 일기에 있는 사진을 수정하는 메서드
//     *
//     * @param diaryId 일기 ID
//     * @param userId 사용자 ID
//     * @param photos 수정할 사진 리스트
//     * @return 수정된 사진 DTO 리스트
//     * @throws IOException 파일 저장 중 발생할 수 있는 예외
//     */
//    public List<WritingPhotoDTO> updatePhotosInDiary(int diaryId, int userId, List<MultipartFile> photos) throws IOException {
//        WritingDiary diary = writingRepository.findById(diaryId)
//                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다."));
//
//        if (diary.getWritingPhoto() != null && !diary.getWritingPhoto().isEmpty()) {
//            List<WritingPhoto> existingPhotos = new ArrayList<>(diary.getWritingPhoto());
//            for (WritingPhoto existingPhoto : existingPhotos) {
//                deleteFile(existingPhoto.getPhoto());
//                writingPhotoRepository.delete(existingPhoto);
//            }
//            diary.getWritingPhoto().clear();
//        }
//
//        return addPhotosToDiary(diaryId, userId, photos);
//    }
//
//    /**
//     * 앨범에 속한 사진을 조회하는 메서드
//     *
//     * @param albumId 앨범 ID
//     * @param userId 사용자 ID
//     * @return 앨범에 속한 사진 DTO 리스트
//     */
//    public List<WritingPhotoDTO> getPhotosByAlbum(int albumId, Integer userId) {
//        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
//        Album album = albumRepository.findById(albumId).orElseThrow(() -> new RuntimeException("앨범을 찾을 수 없습니다."));
//        if (!album.getUser().equals(user)) {
//            throw new RuntimeException("사용자 ID가 앨범의 소유자가 아닙니다.");
//        }
//
//        List<WritingPhotoDTO> photoDTOs = new ArrayList<>();
//        for (WritingDiary diary : album.getWritingDiaries()) {
//            List<WritingPhoto> photos = diary.getWritingPhoto();
//            if (photos == null || photos.isEmpty()) {
//                WritingPhotoDTO defaultPhotoDTO = new WritingPhotoDTO(representativeImageUrl, 0, true);
//                photoDTOs.add(defaultPhotoDTO);
//            } else {
//                for (WritingPhoto photo : photos) {
//                    WritingPhotoDTO photoDTO = new WritingPhotoDTO(
//                            photo.getPhoto() != null ? photo.getPhoto() : representativeImageUrl,
//                            photo.getId(),
//                            photo.getRepresentativeImage()
//                    );
//                    photoDTOs.add(photoDTO);
//                }
//            }
//        }
//        return photoDTOs;
//    }
//
//    /**
//     * 사진 ID로 사진을 조회하는 메서드
//     *
//     * @param photoId 사진 ID
//     * @param userId 사용자 ID
//     * @return 사진 DTO
//     */
//    public WritingPhotoDTO getPhotoById(int photoId, Integer userId) {
//        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
//        WritingPhoto photo = writingPhotoRepository.findById(photoId)
//                .orElseThrow(() -> new RuntimeException("사진을 찾을 수 없습니다."));
//        if (!photo.getWritingDiary().getUser().equals(user)) {
//            throw new RuntimeException("사용자 ID가 사진의 소유자가 아닙니다.");
//        }
//        return convertToDTO(photo);
//    }
//
//    /**
//     * WritingPhoto 엔티티를 DTO로 변환하는 메서드
//     *
//     * @param photo 사진 엔티티
//     * @return 사진 DTO
//     */
//    private WritingPhotoDTO convertToDTO(WritingPhoto photo) {
//        WritingPhotoDTO photoDTO = new WritingPhotoDTO();
//        photoDTO.setPhoto(photo.getPhoto());
//        photoDTO.setPhotoId(photo.getId());
//        photoDTO.setRepresentativeImage(photo.getRepresentativeImage());
//        return photoDTO;
//    }
//}
