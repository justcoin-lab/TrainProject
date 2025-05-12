package rousing.traintrip.community.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import rousing.traintrip.storage.StorageService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    
    private final StorageService storageService;

    @Override
    public Map<String, String> uploadImage(MultipartFile file) throws IOException {
        Map<String, String> result = new HashMap<>();

        try {
            // 원본 파일명 저장
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isEmpty()) {
                originalFileName = "image_" + System.currentTimeMillis();
            }
            
            // StorageService를 통해 파일 저장
            String fileUrl = storageService.store(file);
            
            // 파일명 추출
            String newFileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            
            log.info("이미지 업로드 완료: {}, URL: {}", originalFileName, fileUrl);
            
            result.put("url", fileUrl);
            result.put("originalFileName", originalFileName);
            result.put("newFileName", newFileName);
            
            return result;
        } catch (Exception e) {
            log.error("파일 업로드 오류", e);
            throw new IOException("파일 업로드 중 오류 발생: " + e.getMessage(), e);
        }
    }
}