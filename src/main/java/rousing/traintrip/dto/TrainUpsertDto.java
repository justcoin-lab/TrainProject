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

//    @NotBlank(message = "설명은 필수입니다")
    private String description;

//    @NotBlank(message = "이미지 URL은 필수입니다")
    private String imageUrl;

//    @NotBlank(message = "운행일은 필수입니다")
    private String operatingDays;

//    @NotBlank(message = "요금은 필수입니다")
    private String fare;

//    @NotBlank(message = "노선 이미지 URL은 필수입니다")
    private String routeImageUrl;

//    @NotBlank(message = "예약 URL은 필수입니다")
    private String bookingUrl;

    @NotNull(message = "지역 ID는 필수입니다")
    private Long regionId;
}
