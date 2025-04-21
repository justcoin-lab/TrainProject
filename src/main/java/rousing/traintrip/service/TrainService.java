package rousing.traintrip.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rousing.traintrip.domain.Region;
import rousing.traintrip.domain.Train;
import rousing.traintrip.dto.TrainDetailDto;
import rousing.traintrip.dto.TrainSummaryDto;
import rousing.traintrip.dto.TrainUpsertDto;
import rousing.traintrip.repository.BookmarkRepository;
import rousing.traintrip.repository.RegionRepository;
import rousing.traintrip.repository.TrainRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainService {
    private final TrainRepository trainRepository;
    private final RegionRepository regionRepository;
    private final BookmarkRepository bookmarkRepository;

    // 모든 기차여행 정보를 요약 DTO 형태로 조회합니다.
    public List<TrainSummaryDto> getAllTrains() {
        return trainRepository.findAll().stream()
                .map(TrainSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 특정 지역에 속한 기차여행 정보를 요약 DTO 형태로 조회합니다.
    public List<TrainSummaryDto> getTrainsByRegionId(Long regionId) {
        return trainRepository.findByRegionId(regionId).stream()
                .map(TrainSummaryDto::fromEntity)
                .collect(Collectors.toList());
    }

    // ID로 특정 기차여행의 상세 정보를 조회합니다.
    // 사용자 ID가 제공되면 해당 사용자의 북마크 여부도 함께 확인합니다.
    public TrainDetailDto getTrainById(Long id, Long userId) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("기차여행을 찾을 수 없습니다: " + id));

        boolean bookmarked = false;
        if (userId != null) {
            bookmarked = bookmarkRepository.existsByUserIdAndTrainId(userId, id);
        }

        return TrainDetailDto.fromEntity(train, bookmarked);
    }

    // 새로운 기차여행 정보를 생성합니다.
    @Transactional
    public TrainDetailDto createTrain(TrainUpsertDto dto) {
        Region region = regionRepository.findById(dto.getRegionId())
                .orElseThrow(() -> new EntityNotFoundException("지역을 찾을 수 없습니다: " + dto.getRegionId()));

        Train train = new Train(
                dto.getName(),
                dto.getDescription(),
                dto.getImageUrl(),
                dto.getOperatingDays(),
                dto.getFare(),
                dto.getRouteImageUrl(),
                dto.getBookingUrl(),
                region
        );

        Train savedTrain = trainRepository.save(train);
        return TrainDetailDto.fromEntity(savedTrain, false);
    }

    // 기존 기차여행 정보를 수정합니다.
    @Transactional
    public TrainDetailDto updateTrain(TrainUpsertDto dto) {
        Train train = trainRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("기차여행을 찾을 수 없습니다: " + dto.getId()));

        Region region = regionRepository.findById(dto.getRegionId())
                .orElseThrow(() -> new EntityNotFoundException("지역을 찾을 수 없습니다: " + dto.getRegionId()));
        // 새 객체로 교체
        Train updatedTrain = new Train(
                dto.getName(),
                dto.getDescription(),
                dto.getImageUrl(),
                dto.getOperatingDays(),
                dto.getFare(),
                dto.getRouteImageUrl(),
                dto.getBookingUrl(),
                region
        );
        // ID 유지
        updatedTrain = trainRepository.save(updatedTrain);

        return TrainDetailDto.fromEntity(updatedTrain, false);
    }

    // 기차여행 정보를 삭제합니다.
    @Transactional
    public void deleteTrain(Long id) {
        if (!trainRepository.existsById(id)) {
            throw new EntityNotFoundException("기차여행을 찾을 수 없습니다: " + id);
        }
        trainRepository.deleteById(id);
    }
}
