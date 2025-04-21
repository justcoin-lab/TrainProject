package rousing.traintrip.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rousing.traintrip.domain.Country;
import rousing.traintrip.dto.CountryDto;
import rousing.traintrip.repository.CountryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    // 모든 국가 정보를 조회합니다.
    public List<CountryDto> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(CountryDto::fromEntity)
                .collect(Collectors.toList());
    }

    // ID로 특정 국가의 정보를 조회합니다.
    public CountryDto getCountryById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("국가를 찾을 수 없습니다: " + id));
        return CountryDto.fromEntity(country);
    }
}