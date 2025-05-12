package rousing.traintrip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 레거시 클래스 - 후에는 TrainDto와 TrainFormDto로 대체
 * @deprecated 파일 업로드 로직과 업무 로직이 혼재되어 있으므로 디프리케이션 방지를 위해 새로운 클래스로 분리함
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class TrainUpsertDto {
    private Long id;

    @NotBlank(message = "기차 이름은 필수입니다")
    private String name;

    @NotBlank(message = "간단한 소개를 입력해주세요")
    private String description;

    // 이미지 URL
    private String imageUrl;
    
    // 대표 이미지 파일
    private MultipartFile imageFile;

    @NotBlank(message = "운행일을 입력해주세요")
    private String operatingDays;

    @NotBlank(message = "요금을 입력해주세요")
    private String fare;

    // 노선 이미지 URL
    private String routeImageUrl;
    
    // 노선 이미지 파일
    private MultipartFile routeImageFile;

    @NotBlank(message = "예약 URL을 입력해주세요")
    private String bookingUrl;

    @NotBlank(message = "사이트 URL을 입력해주세요")
    private String siteUrl;

    @NotNull(message = "지역 ID는 필수입니다")
    private Long regionId;
    
    /**
     * TrainDto로 변환합니다.
     * @return TrainDto 객체
     */
    public TrainDto toTrainDto() {
        return TrainDto.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .imageUrl(this.imageUrl)
                .operatingDays(this.operatingDays)
                .fare(this.fare)
                .routeImageUrl(this.routeImageUrl)
                .bookingUrl(this.bookingUrl)
                .siteUrl(this.siteUrl)
                .regionId(this.regionId)
                .build();
    }
    
    /**
     * TrainDto와 파일들을 포함한 TrainFormDto로 변환합니다.
     * @return TrainFormDto 객체
     */
    public TrainFormDto toTrainFormDto() {
        return TrainFormDto.builder()
                .trainDto(this.toTrainDto())
                .imageFile(this.imageFile)
                .routeImageFile(this.routeImageFile)
                .build();
    }
    
    /**
     * TrainDto로부터 TrainUpsertDto를 생성합니다.
     * @param trainDto 기차여행 DTO
     * @return TrainUpsertDto 객체
     */
    public static TrainUpsertDto from(TrainDto trainDto) {
        return TrainUpsertDto.builder()
                .id(trainDto.getId())
                .name(trainDto.getName())
                .description(trainDto.getDescription())
                .imageUrl(trainDto.getImageUrl())
                .operatingDays(trainDto.getOperatingDays())
                .fare(trainDto.getFare())
                .routeImageUrl(trainDto.getRouteImageUrl())
                .bookingUrl(trainDto.getBookingUrl())
                .siteUrl(trainDto.getSiteUrl())
                .regionId(trainDto.getRegionId())
                .build();
    }
}
