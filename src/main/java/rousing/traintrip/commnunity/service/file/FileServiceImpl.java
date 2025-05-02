package rousing.traintrip.commnunity.service.file;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 서비스 초기화 시 업로드 디렉토리 생성
     */
    @PostConstruct
    public void init() {
        try {
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (created) {
                    log.info("업로드 디렉토리 생성 완료: {}", uploadDir);
                } else {
                    log.warn("업로드 디렉토리 생성 실패: {}", uploadDir);
                }
            }
        } catch (Exception e) {
            log.error("업로드 디렉토리 초기화 오류", e);
        }
    }

    @Override
    public Map<String, String> uploadImage(MultipartFile file) throws IOException {
        Map<String, String> result = new HashMap<>();

        try {
            // 원본 파일명 저장
            String originalFileName = file.getOriginalFilename();
            
            // 파일명 무결성 대책
            if (originalFileName == null || originalFileName.isEmpty()) {
                originalFileName = "image_" + System.currentTimeMillis();
            }
            
            // 특수문자 파일명 예외 처리
            originalFileName = originalFileName.replaceAll("[\\\\/:\\*\\?\"<>|]", "_");
            
            // 확장자 추출
            String extension = "";
            if (originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            } else {
                // 확장자가 없는 경우 컨텐트 타입에 맞게 추가
                String contentType = file.getContentType();
                if (contentType != null) {
                    if (contentType.contains("jpeg") || contentType.contains("jpg")) {
                        extension = ".jpg";
                    } else if (contentType.contains("png")) {
                        extension = ".png";
                    } else if (contentType.contains("gif")) {
                        extension = ".gif";
                    } else {
                        extension = ".jpg"; // 기본값
                    }
                } else {
                    extension = ".jpg"; // 컨텐트 타입이 없는 경우 기본값
                }
            }
            
            // 고유한 파일명 생성 (UUID + 원본 확장자)
            String newFileName = UUID.randomUUID().toString() + extension;
            
            // 업로드 디렉토리 확인 및 생성
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                boolean created = uploadPath.mkdirs();
                if (!created) {
                    throw new IOException("업로드 디렉토리를 생성할 수 없습니다: " + uploadDir);
                }
            }
            
            log.info("업로드 파일 정보 - 파일명: {}, 파일크기: {}, 디렉토리: {}, 디렉토리존재여부: {}", 
                    originalFileName, file.getSize(), uploadDir, uploadPath.exists());
            
            // 파일 저장
            Path targetPath = Paths.get(uploadDir, newFileName);
            Files.copy(file.getInputStream(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            // 파일 저장 확인
            if (!Files.exists(targetPath)) {
                throw new IOException("파일이 저장되지 않았습니다: " + targetPath);
            }
            
            // 접근 URL 생성
            String fileUrl = "/uploads/images/" + newFileName;
            
            log.info("이미지 업로드 완료: {}, URL: {}", originalFileName, fileUrl);
            
            result.put("url", fileUrl);
            result.put("originalFileName", originalFileName);
            result.put("newFileName", newFileName);
            result.put("savedPath", targetPath.toString());
            
            return result;
        } catch (Exception e) {
            log.error("파일 업로드 오류", e);
            throw new IOException("파일 업로드 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
