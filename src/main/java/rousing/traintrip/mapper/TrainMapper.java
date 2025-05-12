package rousing.traintrip.mapper;

import org.springframework.stereotype.Component;
import rousing.traintrip.domain.Region;
import rousing.traintrip.domain.Train;
import rousing.traintrip.dto.TrainDetailDto;
import rousing.traintrip.dto.TrainDto;
import rousing.traintrip.dto.TrainFormDto;
import rousing.traintrip.dto.TrainSummaryDto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Train 엔티티와 DTO 간의 변환을 담당하는 매퍼 클래스입니다.
 */
@Component
public class TrainMapper {

    /**
     * Train 엔티티를 TrainSummaryDto로 변환합니다.
     *
     * @param train 기차여행 엔티티
     * @return 기차여행 요약 DTO
     */
    public TrainSummaryDto toSummaryDto(Train train) {
        return TrainSummaryDto.builder()
                .id(train.getId())
                .name(train.getName())
                .description(train.getDescription())
                .imageUrl(train.getImageUrl())
                .regionId(train.getRegion().getId())
                .regionName(train.getRegion().getName())
                .description(train.getDescription())
                .build();
    }

    /**
     * Train 엔티티 리스트를 TrainSummaryDto 리스트로 변환합니다.
     *
     * @param trains 기차여행 엔티티 리스트
     * @return 기차여행 요약 DTO 리스트
     */
    public List<TrainSummaryDto> toSummaryDtoList(List<Train> trains) {
        return trains.stream()
                .map(this::toSummaryDto)
                .collect(Collectors.toList());
    }

    /**
     * Train 엔티티를 TrainDetailDto로 변환합니다.
     *
     * @param train      기차여행 엔티티
     * @param bookmarked 북마크 여부
     * @return 기차여행 상세 DTO
     */
    public TrainDetailDto toDetailDto(Train train, boolean bookmarked) {
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

    /**
     * TrainDto와 Region을 기반으로 Train 엔티티를 생성합니다.
     *
     * @param dto    기차여행 DTO
     * @param region 지역 엔티티
     * @return 기차여행 엔티티
     */
    public Train toEntity(TrainDto dto, Region region) {
        return new Train(
                dto.getName(),
                dto.getDescription(),
                dto.getImageUrl(),
                dto.getOperatingDays(),
                dto.getFare(),
                dto.getRouteImageUrl(),
                dto.getBookingUrl(),
                dto.getSiteUrl(),
                region
        );
    }

    /**
     * TrainFormDto에서 TrainDto를 추출합니다.
     *
     * @param formDto 기차여행 폼 DTO
     * @return 기차여행 DTO
     */
    public TrainDto formToDto(TrainFormDto formDto) {
        return formDto.getTrainDto();
    }
}
