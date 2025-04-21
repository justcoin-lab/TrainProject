package rousing.traintrip.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rousing.traintrip.domain.Region;
import rousing.traintrip.dto.RegionDto;
import rousing.traintrip.repository.RegionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    // 특정 국가에 속한 모든 지역 정보를 조회합니다.
    public List<RegionDto> getRegionsByCountryId(Long countryId) {
        return regionRepository.findByCountryId(countryId).stream()
                .map(RegionDto::fromEntity)
                .collect(Collectors.toList());
    }

    // ID로 특정 지역의 정보를 조회합니다.
    public RegionDto getRegionById(Long id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("지역을 찾을 수 없습니다: " + id));
        return RegionDto.fromEntity(region);
    }
}