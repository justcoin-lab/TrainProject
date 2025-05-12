package rousing.traintrip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rousing.traintrip.domain.Region;
import rousing.traintrip.domain.Train;
import rousing.traintrip.dto.TrainDetailDto;
import rousing.traintrip.dto.TrainDto;
import rousing.traintrip.dto.TrainFormDto;
import rousing.traintrip.dto.TrainSummaryDto;
import rousing.traintrip.exception.ResourceNotFoundException;
import rousing.traintrip.mapper.TrainMapper;
import rousing.traintrip.repository.BookmarkRepository;
import rousing.traintrip.repository.TrainRepository;

import java.util.List;

/**
 * 기차여행 정보 관리를 담당하는 서비스 클래스입니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TrainService {
    private final TrainRepository trainRepository;
    private final BookmarkRepository bookmarkRepository;
    private final EntityFinder entityFinder;
    private final FileStorageService fileStorageService;
    private final TrainMapper trainMapper;

    /**
     * 새로운 기차여행 정보를 생성합니다.
     * 
     * @param formDto 기차여행 폼 DTO
     * @return 생성된 기차여행 상세 정보
     */
    @Transactional
    public TrainDetailDto createTrain(TrainFormDto formDto) {
        try {
            // 1. 파일 처리
            TrainDto processedDto = fileStorageService.processTrainFiles(formDto);
            
            // 2. 데이터베이스에 저장
            Region region = entityFinder.findRegionById(processedDto.getRegionId());
            Train train = trainMapper.toEntity(processedDto, region);
            Train savedTrain = trainRepository.save(train);
            
            return trainMapper.toDetailDto(savedTrain, false);
        } catch (Exception e) {
            // 실패 시 업로드된 파일 정리
            fileStorageService.cleanupUploadedFiles(formDto);
            throw e;
        }
    }

    /**
     * 기존 기차여행 정보를 업데이트합니다.
     * 
     * @param formDto 기차여행 폼 DTO
     * @return 업데이트된 기차여행 상세 정보
     */
    @Transactional
    public TrainDetailDto updateTrain(TrainFormDto formDto) {
        try {
            // 1. 기존 기차여행 정보 확인
            Long trainId = formDto.getTrainDto().getId();
            Train train = entityFinder.findTrainById(trainId);
            
            // 2. 파일 처리
            TrainDto processedDto = fileStorageService.processTrainFiles(formDto);
            
            // 3. 데이터베이스 업데이트
            Region region = entityFinder.findRegionById(processedDto.getRegionId());
            
            train.update(
                processedDto.getName(),
                processedDto.getDescription(),
                processedDto.getImageUrl(),
                processedDto.getOperatingDays(),
                processedDto.getFare(),
                processedDto.getRouteImageUrl(),
                processedDto.getBookingUrl(),
                processedDto.getSiteUrl(),
                region
            );
            
            Train updatedTrain = trainRepository.save(train);
            return trainMapper.toDetailDto(updatedTrain, false);
        } catch (Exception e) {
            // 실패 시 업로드된 파일 정리
            fileStorageService.cleanupUploadedFiles(formDto);
            throw e;
        }
    }

    /**
     * 모든 기차여행 정보를 요약 형태로 조회합니다.
     * 
     * @return 기차여행 요약 정보 목록
     */
    @Transactional(readOnly = true)
    public List<TrainSummaryDto> getAllTrains() {
        List<Train> trains = trainRepository.findAll();
        return trainMapper.toSummaryDtoList(trains);
    }

    /**
     * 특정 지역의 기차여행 정보를 요약 형태로 조회합니다.
     * 
     * @param regionId 지역 ID
     * @return 기차여행 요약 정보 목록
     */
    @Transactional(readOnly = true)
    public List<TrainSummaryDto> getTrainsByRegionId(Long regionId) {
        List<Train> trains = trainRepository.findByRegionId(regionId);
        return trainMapper.toSummaryDtoList(trains);
    }

    /**
     * ID로 기차여행 상세 정보를 조회합니다.
     * 
     * @param id 기차여행 ID
     * @param userId 사용자 ID (북마크 확인용, null 가능)
     * @return 기차여행 상세 정보
     */
    @Transactional(readOnly = true)
    public TrainDetailDto getTrainById(Long id, Long userId) {
        Train train = entityFinder.findTrainById(id);

        boolean bookmarked = false;
        if (userId != null) {
            bookmarked = bookmarkRepository.existsByUserIdAndTrainId(userId, id);
        }

        return trainMapper.toDetailDto(train, bookmarked);
    }

    /**
     * 기차여행 정보를 삭제합니다.
     * 
     * @param id 기차여행 ID
     */
    @Transactional
    public void deleteTrain(Long id) {
        Train train = entityFinder.findTrainById(id);
        
        // 연결된 이미지 파일 삭제
        if (train.getImageUrl() != null && !train.getImageUrl().isEmpty()) {
            fileStorageService.deleteFile(train.getImageUrl());
        }
        
        if (train.getRouteImageUrl() != null && !train.getRouteImageUrl().isEmpty()) {
            fileStorageService.deleteFile(train.getRouteImageUrl());
        }
        
        trainRepository.delete(train);
    }
}
