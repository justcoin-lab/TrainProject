package rousing.traintrip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 기차여행 정보를 담는 DTO
 * 서비스 계층과 컨트롤러 계층 간의 기차여행 데이터 전송에 사용됩니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainDto {
    private Long id;

    @NotBlank(message = "기차 이름은 필수입니다")
    private String name;

    @NotBlank(message = "간단한 소개를 입력해주세요")
    private String description;

    private String imageUrl;

    @NotBlank(message = "운행일을 입력해주세요")
    private String operatingDays;

    @NotBlank(message = "요금을 입력해주세요")
    private String fare;

    private String routeImageUrl;

    @NotBlank(message = "예약 URL을 입력해주세요")
    private String bookingUrl;

    @NotBlank(message = "사이트 URL을 입력해주세요")
    private String siteUrl;

    @NotNull(message = "지역 ID는 필수입니다")
    private Long regionId;
}