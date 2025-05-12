package rousing.traintrip.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * 파일 시스템 기반 저장소 서비스 구현체
 */
@Service
@Slf4j
public class FileSystemStorageService implements StorageService {
    
    private final Path rootLocation;
    private final String urlPrefix;
    
    public FileSystemStorageService(
            @Value("${file.upload-dir}") String uploadDir,
            @Value("${file.url-prefix:/uploads/images/}") String urlPrefix) {
        this.rootLocation = Paths.get(uploadDir);
        this.urlPrefix = urlPrefix;
    }
    
    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
            log.info("스토리지 디렉토리 초기화 완료: {}", rootLocation);
        } catch (IOException e) {
            throw new StorageException("스토리지 디렉토리를 초기화할 수 없습니다", e);
        }
    }
    
    @Override
    public String store(MultipartFile file) throws StorageException {
        try {
            if (file.isEmpty()) {
                throw new StorageException("빈 파일은 저장할 수 없습니다");
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
            
            // 고유 파일명 생성 (UUID + 날짜 + 확장자)
            String newFilename = UUID.randomUUID().toString() + "_" + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
                    extension;
            
            // 파일 저장 경로
            Path destinationPath = this.rootLocation.resolve(
                    Paths.get(newFilename))
                    .normalize().toAbsolutePath();
            
            // 대상 경로가 rootLocation 외부에 있는지 확인 (디렉토리 순회 공격 방지)
            if (!destinationPath.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("저장 위치 외부에 파일을 저장할 수 없습니다");
            }
            
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                log.info("파일 저장 완료: {} -> {}", originalFilename, destinationPath);
            }
            
            return getUrlFromPath(destinationPath);
        } catch (IOException e) {
            throw new StorageException("파일 저장 중 오류가 발생했습니다", e);
        }
    }
    
    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new StorageException("파일 목록을 불러올 수 없습니다", e);
        }
    }
    
    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }
    
    @Override
    public boolean delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        
        try {
            Path filePath = getPathFromUrl(fileUrl);
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
            throw new StorageException("파일 삭제 중 오류가 발생했습니다", e);
        }
    }
    
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }
    
    @Override
    public Path getPathFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new StorageException("파일 URL이 유효하지 않습니다");
        }
        
        // URL에서 파일명 추출 (/uploads/images/파일명 -> 파일명)
        if (!fileUrl.startsWith(urlPrefix)) {
            throw new StorageException("지원하지 않는 파일 URL 형식입니다: " + fileUrl);
        }
        
        String filename = fileUrl.substring(urlPrefix.length());
        return rootLocation.resolve(filename);
    }
    
    @Override
    public String getUrlFromPath(Path path) {
        String filename = path.getFileName().toString();
        return urlPrefix + filename;
    }
}