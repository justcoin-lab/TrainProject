package rousing.traintrip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rousing.traintrip.domain.Train;

@Getter
@Setter
@Builder
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
    private String siteUrl;
    private Long regionId;
    private String regionName;
    private String countryName;
    private boolean bookmarked;


    public static TrainDetailDto fromEntity(Train train, boolean bookmarked) {
        return TrainDetailDto.builder()
                .id(train.getId())
                .name(train.getName())
                .description(train.getDescription())
                .imageUrl(train.getImageUrl())
                .operatingDays(train.getOperatingDays())
                .fare(train.getFare())
                .routeImageUrl(train.getRouteImageUrl())
                .bookingUrl(train.getBookingUrl())
                .siteUrl(train.getSiteUrl())
                .regionId(train.getRegion().getId())
                .regionName(train.getRegion().getName())
                .countryName(train.getRegion().getCountry().getName())
                .bookmarked(bookmarked)
                .build();
    }
}
