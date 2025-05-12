package rousing.traintrip.service;

import rousing.traintrip.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rousing.traintrip.domain.Region;
import rousing.traintrip.dto.RegionDto;
import rousing.traintrip.repository.RegionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RegionService {
    private final RegionRepository regionRepository;

    // 특정 국가에 속한 모든 지역 정보를 조회합니다.
    @Transactional(readOnly = true)
    public List<RegionDto> getRegionsByCountryId(Long countryId) {
        return regionRepository.findByCountryId(countryId).stream()
                .map(RegionDto::fromEntity)
                .collect(Collectors.toList());
    }

    // ID로 특정 지역의 정보를 조회합니다.
    @Transactional(readOnly = true)
    public RegionDto getRegionById(Long id) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region", "id", id));
        return RegionDto.fromEntity(region);
    }
}