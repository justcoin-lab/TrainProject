package rousing.traintrip.commnunity.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class FileStorageConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 업로드 디렉토리 초기화 메서드
     * 프로젝트 실행 시 자동으로 업로드 디렉토리를 생성합니다.
     */
    @Bean
    public String initStorageDirectory() {
        Path storagePath;
        
        try {
            // 프로젝트 루트 디렉토리 기준 경로 사용
            storagePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            
            if (!Files.exists(storagePath)) {
                try {
                    Files.createDirectories(storagePath);
                    log.info("Created upload directory: {}", storagePath);
                } catch (IOException e) {
                    log.warn("Failed to create directory at {}: {}", storagePath, e.getMessage());
                    // 실패 시 대체 경로 시도
                    try {
                        Resource resource = new ClassPathResource("static");
                        Path staticPath = Paths.get(resource.getFile().getAbsolutePath());
                        storagePath = staticPath.resolve("uploads/images");
                        if (!Files.exists(storagePath)) {
                            Files.createDirectories(storagePath);
                        }
                        log.info("Created alternate upload directory: {}", storagePath);
                    } catch (IOException ex) {
                        log.error("Failed to create upload directory", ex);
                        throw new RuntimeException("Could not create upload directory", ex);
                    }
                }
            }
            
            // 디렉토리 접근 권한 확인
            if (!Files.isWritable(storagePath)) {
                log.error("Upload directory is not writable: {}", storagePath);
                throw new RuntimeException("Upload directory is not writable: " + storagePath);
            }
            
            log.info("Upload directory initialized at: {}", storagePath);
            return storagePath.toString();
            
        } catch (Exception e) {
            log.error("Error initializing upload directory", e);
            throw new RuntimeException("Could not initialize upload directory", e);
        }
    }
}
