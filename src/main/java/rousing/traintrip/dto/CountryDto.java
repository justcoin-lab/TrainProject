package rousing.traintrip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rousing.traintrip.domain.Country;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CountryDto {
    private Long id;
    private String name;
    private List<RegionDto> regions;

    public static CountryDto fromEntity(Country country) {
        CountryDto dto = new CountryDto();
        dto.id = country.getId();
        dto.name = country.getName();
        dto.regions = country.getRegions().stream()
                .map(RegionDto::fromEntity)
                .collect(Collectors.toList());
        return dto;
    }
}