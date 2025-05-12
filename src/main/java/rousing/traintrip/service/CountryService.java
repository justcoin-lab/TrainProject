package rousing.traintrip.service;

import rousing.traintrip.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rousing.traintrip.domain.Country;
import rousing.traintrip.dto.CountryDto;
import rousing.traintrip.repository.CountryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    // 모든 국가 정보를 조회합니다.
    @Transactional(readOnly = true)
    public List<CountryDto> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(CountryDto::fromEntity)
                .collect(Collectors.toList());
    }

    // ID로 특정 국가의 정보를 조회합니다.
    @Transactional(readOnly = true)
    public CountryDto getCountryById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "id", id));
        return CountryDto.fromEntity(country);
    }
}