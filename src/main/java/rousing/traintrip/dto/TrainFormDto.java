package rousing.traintrip.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 기차여행 폼 데이터를 담는 DTO
 * 프레젠테이션 계층에서 파일 업로드와 함께 사용됩니다.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainFormDto {
    private TrainDto trainDto;
    private MultipartFile imageFile;
    private MultipartFile routeImageFile;
    
    /**
     * TrainDto를 생성하여 반환합니다.
     * @return 기차여행 DTO
     */
    public TrainDto toTrainDto() {
        return trainDto;
    }
    
    /**
     * TrainDto로부터 TrainFormDto를 생성합니다.
     * @param trainDto 기차여행 DTO
     * @return 기차여행 폼 DTO
     */
    public static TrainFormDto from(TrainDto trainDto) {
        return TrainFormDto.builder()
                .trainDto(trainDto)
                .build();
    }
}