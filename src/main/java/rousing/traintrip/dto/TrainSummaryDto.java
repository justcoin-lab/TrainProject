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
public class TrainSummaryDto {
    private Long id; // 기차여행 ID
    private String name; // 기차여행 이름
    private String imageUrl; // 이미지 URL
    private String regionName; // 지역 이름

    /**
     * Train 엔티티로부터 DTO 변환
     */
    public static TrainSummaryDto fromEntity(Train train) {
        return TrainSummaryDto.builder()
                .id(train.getId())
                .name(train.getName())
                .imageUrl(train.getImageUrl())
                .regionName(train.getRegion().getName())
                .build();
    }
}
