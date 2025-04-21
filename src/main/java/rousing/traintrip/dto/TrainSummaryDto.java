package rousing.traintrip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rousing.traintrip.domain.Train;

// 기차여행 간략 정보 DTO (목록 표시용)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainSummaryDto {
    private Long id;
    private String name;
    private String imageUrl;

    public static TrainSummaryDto fromEntity(Train train) {
        TrainSummaryDto dto = new TrainSummaryDto();
        dto.id = train.getId();
        dto.name = train.getName();
        dto.imageUrl = train.getImageUrl();
        return dto;
    }
}
