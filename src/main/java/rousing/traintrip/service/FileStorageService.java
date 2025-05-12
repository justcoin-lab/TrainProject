package rousing.traintrip.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import rousing.traintrip.dto.TrainDto;
import rousing.traintrip.dto.TrainFormDto;
import rousing.traintrip.storage.StorageException;
import rousing.traintrip.storage.StorageService;

/**
 * 파일 저장 및 관리를 담당하는 통합 서비스입니다.
 * 파일 업로드, 삭제, 교체 등의 기능을 제공합니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    private final StorageService storageService;

    /**
     * 파일을 업로드하고 접근 가능한 URL을 반환합니다.
     * 
     * @param file 업로드할 파일
     * @return 접근 가능한 파일 URL
     * @throws StorageException 파일 업로드 중 오류가 발생한 경우
     */
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        try {
            return storageService.store(file);
        } catch (StorageException e) {
            log.error("파일 업로드 실패: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 기존 파일을 삭제합니다.
     * 
     * @param fileUrl 삭제할 파일의 URL
     * @return 삭제 성공 여부
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        
        try {
            return storageService.delete(fileUrl);
        } catch (StorageException e) {
            log.error("파일 삭제 실패: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 기존 파일을 교체합니다.
     * 기존 파일을 삭제하고 새 파일을 업로드합니다.
     * 
     * @param currentFileUrl 현재 파일 URL
     * @param newFile 새 파일
     * @return 새 파일 URL
     */
    public String replaceFile(String currentFileUrl, MultipartFile newFile) {
        if (newFile == null || newFile.isEmpty()) {
            return currentFileUrl;
        }
        
        deleteFile(currentFileUrl);
        return uploadFile(newFile);
    }
    
    /**
     * 기차 정보 폼에서 파일을 처리하고 업데이트된 DTO를 반환합니다.
     * 트랜잭션과 별개로 동작하여 DB 롤백 시에도 파일은 유지됩니다.
     * 
     * @param formDto 기차여행 폼 DTO
     * @return 업로드된 이미지 URL이 포함된 TrainDto
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TrainDto processTrainFiles(TrainFormDto formDto) {
        TrainDto trainDto = formDto.getTrainDto();
        
        try {
            // 대표 이미지 처리
            if (formDto.getImageFile() != null && !formDto.getImageFile().isEmpty()) {
                String imageUrl = replaceFile(trainDto.getImageUrl(), formDto.getImageFile());
                trainDto.setImageUrl(imageUrl);
            }
            
            // 노선 이미지 처리
            if (formDto.getRouteImageFile() != null && !formDto.getRouteImageFile().isEmpty()) {
                String routeImageUrl = replaceFile(trainDto.getRouteImageUrl(), formDto.getRouteImageFile());
                trainDto.setRouteImageUrl(routeImageUrl);
            }
            
            return trainDto;
        } catch (StorageException e) {
            log.error("파일 업로드 중 오류 발생: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 업로드에 실패한 파일들을 정리합니다.
     * 
     * @param formDto 기차여행 폼 DTO
     */
    public void cleanupUploadedFiles(TrainFormDto formDto) {
        TrainDto trainDto = formDto.getTrainDto();
        
        // 이미지 파일이 업로드되었다면 삭제
        if (formDto.getImageFile() != null && !formDto.getImageFile().isEmpty() && 
            trainDto.getImageUrl() != null && !trainDto.getImageUrl().isEmpty()) {
            deleteFile(trainDto.getImageUrl());
        }
        
        // 노선 이미지 파일이 업로드되었다면 삭제
        if (formDto.getRouteImageFile() != null && !formDto.getRouteImageFile().isEmpty() && 
            trainDto.getRouteImageUrl() != null && !trainDto.getRouteImageUrl().isEmpty()) {
            deleteFile(trainDto.getRouteImageUrl());
        }
    }
}