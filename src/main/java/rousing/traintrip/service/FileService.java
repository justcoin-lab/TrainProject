package rousing.traintrip.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 파일을 업로드하고 접근 가능한 URL을 반환합니다.
     * @param file 업로드할 파일
     * @return 접근 가능한 파일 URL
     * @throws IOException 파일 업로드 중 오류가 발생한 경우
     */
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        }

        // 업로드 디렉토리가 없으면 생성
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            if(!uploadDirectory.mkdirs()) {
                log.error("업로드 디렉토리를 생성하지 못했습니다: {}", uploadDir);
                throw new IOException("업로드 디렉토리를 생성할 수 없습니다.");
            }
        }

        // 원본 파일명에서 확장자 추출
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "unknown";
        }
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        // 파일명 충돌 방지를 위한 고유 파일명 생성 (UUID + 날짜 + 확장자)
        String newFilename = UUID.randomUUID().toString() + "_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                extension;
        
        // 파일 저장 경로
        Path targetPath = Paths.get(uploadDir, newFilename);
        
        // 파일 저장 (만약 이미 있는 파일이면 오버라이트)
        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("파일 업로드 성공: {} -> {}", originalFilename, targetPath);
        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw new IOException("파일을 저장하는 도중 오류가 발생했습니다: " + e.getMessage(), e);
        }
        
        // 접근 가능한 URL 반환 (/uploads/images/파일명)
        return "/uploads/images/" + newFilename;
    }
    
    /**
     * 기존 파일을 삭제합니다.
     * @param fileUrl 삭제할 파일의 URL
     * @return 삭제 성공 여부
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        
        try {
            // URL에서 파일명 추출 (/uploads/images/파일명 -> 파일명)
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            
            // 파일 삭제
            Path filePath = Paths.get(uploadDir, filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("파일 삭제 성공: {}", filePath);
                return true;
            } else {
                log.warn("삭제할 파일을 찾을 수 없습니다: {}", filePath);
                return false;
            }
        } catch (Exception e) {
            log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }
}
