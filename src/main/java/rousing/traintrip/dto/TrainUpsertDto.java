package rousing.traintrip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 기차여행 등록/수정 DTO
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainUpsertDto {
    private Long id; // 수정 시 사용

    @NotBlank(message = "기차 이름은 필수입니다")
    private String name;

    @NotBlank(message = "간단한 소개를 입력해주세요")
    private String description;

    @NotBlank(message = "이미지를 넣어주세요")
    private String imageUrl;

    @NotBlank(message = "운행일을 입력해주세요")
    private String operatingDays;

    @NotBlank(message = "요금을 입력해주세요")
    private String fare;

    @NotBlank(message = "노선 이미지를 넣어주세요")
    private String routeImageUrl;

    @NotBlank(message = "예약 URL을 입력해주세요")
    private String bookingUrl;

    @NotBlank(message = "사이트 URL을 입력해주세요")
    private String siteUrl;

    @NotNull(message = "지역 ID는 필수입니다")
    private Long regionId;
}
