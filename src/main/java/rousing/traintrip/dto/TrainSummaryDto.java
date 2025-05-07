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
    private String thumbnailUrl; // 썸네일 이미지 URL
    private String regionName; // 지역 이름
    private Long regionId; // 지역 ID
    private String description; // 기차여행 설명

    public static TrainSummaryDto fromEntity(Train train) {
        TrainSummaryDto dto = TrainSummaryDto.builder()
                .id(train.getId())
                .name(train.getName())
                .imageUrl(train.getImageUrl())
                .regionName(train.getRegion().getName())
                .regionId(train.getRegion().getId())
                .description(train.getDescription())
                .build();
        
        // 썸네일 URL 생성
        String imageUrl = train.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.contains("_detail")) {
                String thumbnailUrl = imageUrl.replace("_detail", "_thumb");
                dto.setThumbnailUrl(thumbnailUrl);
            } else {
                dto.setThumbnailUrl(imageUrl); // 기본값 설정
            }
        }
        
        return dto;
    }
}
