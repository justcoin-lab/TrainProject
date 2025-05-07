package rousing.traintrip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

// 기차여행 등록/수정 DTO
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    
    // 생성자 - 파일 업로드 필드 제외
    public TrainUpsertDto(Long id, String name, String description, String imageUrl, 
                           String operatingDays, String fare, String routeImageUrl, 
                           String bookingUrl, String siteUrl, Long regionId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.operatingDays = operatingDays;
        this.fare = fare;
        this.routeImageUrl = routeImageUrl;
        this.bookingUrl = bookingUrl;
        this.siteUrl = siteUrl;
        this.regionId = regionId;
    }
}
