package rousing.traintrip.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import rousing.traintrip.storage.StorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 파일 저장소 관련 설정 클래스
 * 기존 FileStorageConfig와 StorageConfig를 통합하여 일관된 인터페이스 제공
 */
@Configuration
@Slf4j
public class StorageConfig {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    /**
     * 저장소 서비스를 사용하여 저장소 디렉토리를 초기화합니다.
     */
    @Bean
    CommandLineRunner initStorage(StorageService storageService) {
        return args -> {
            // StorageService를 통한 초기화
            log.info("StorageService를 통한 저장소 초기화 시작");
            storageService.init();
            log.info("StorageService를 통한 저장소 초기화 완료");
        };
    }
    
    /**
     * 업로드 디렉토리 초기화 메서드
     * 프로젝트 실행 시 자동으로 업로드 디렉토리를 생성합니다.
     */
    @Bean
    @Primary
    public String initStorageDirectory() {
        Path storagePath;
        
        try {
            // 프로젝트 루트 디렉토리 기준 경로 사용
            storagePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            
            if (!Files.exists(storagePath)) {
                try {
                    Files.createDirectories(storagePath);
                    log.info("업로드 디렉토리 생성 완료: {}", storagePath);
                } catch (IOException e) {
                    log.warn("{}에 디렉토리 생성 실패: {}", storagePath, e.getMessage());
                    // 실패 시 대체 경로 시도
                    try {
                        Resource resource = new ClassPathResource("static");
                        Path staticPath = Paths.get(resource.getFile().getAbsolutePath());
                        storagePath = staticPath.resolve("uploads/images");
                        if (!Files.exists(storagePath)) {
                            Files.createDirectories(storagePath);
                        }
                        log.info("대체 업로드 디렉토리 생성 완료: {}", storagePath);
                    } catch (IOException ex) {
                        log.error("업로드 디렉토리 생성 실패", ex);
                        throw new RuntimeException("업로드 디렉토리를 생성할 수 없습니다", ex);
                    }
                }
            }
            
            // 디렉토리 접근 권한 확인
            if (!Files.isWritable(storagePath)) {
                log.error("업로드 디렉토리에 쓰기 권한이 없습니다: {}", storagePath);
                throw new RuntimeException("업로드 디렉토리에 쓰기 권한이 없습니다: " + storagePath);
            }
            
            log.info("업로드 디렉토리 초기화 완료: {}", storagePath);
            return storagePath.toString();
            
        } catch (Exception e) {
            log.error("업로드 디렉토리 초기화 오류", e);
            throw new RuntimeException("업로드 디렉토리를 초기화할 수 없습니다", e);
        }
    }
}