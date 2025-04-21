package rousing.traintrip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rousing.traintrip.domain.Region;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegionDto {
    private Long id;
    private String name;
    private Long countryId;

    public static RegionDto fromEntity(Region region) {
        RegionDto dto = new RegionDto();
        dto.id = region.getId();
        dto.name = region.getName();
        dto.countryId = region.getCountry().getId();
        return dto;
    }
}
