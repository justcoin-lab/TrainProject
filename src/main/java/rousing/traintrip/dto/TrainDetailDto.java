package rousing.traintrip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rousing.traintrip.domain.Train;

// 기차여행 상세 정보 DTO
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainDetailDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String operatingDays;
    private String fare;
    private String routeImageUrl;
    private String bookingUrl;
    private Long regionId;
    private String regionName;
    private boolean bookmarked;

    public static TrainDetailDto fromEntity(Train train, boolean bookmarked) {
        TrainDetailDto dto = new TrainDetailDto();
        dto.id = train.getId();
        dto.name = train.getName();
        dto.description = train.getDescription();
        dto.imageUrl = train.getImageUrl();
        dto.operatingDays = train.getOperatingDays();
        dto.fare = train.getFare();
        dto.routeImageUrl = train.getRouteImageUrl();
        dto.bookingUrl = train.getBookingUrl();
        dto.regionId = train.getRegion().getId();
        dto.regionName = train.getRegion().getName();
        dto.bookmarked = bookmarked;
        return dto;
    }
}
