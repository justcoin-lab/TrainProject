package rousing.traintrip.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rousing.traintrip.domain.Bookmark;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkDto {
    private Long id;
    private Long userId;
    private Long trainId;
    private String trainName;
    private String trainImageUrl;

    public static BookmarkDto fromEntity(Bookmark bookmark) {
        BookmarkDto dto = new BookmarkDto();
        dto.id = bookmark.getId();
        dto.userId = bookmark.getUser().getId();
        dto.trainId = bookmark.getTrain().getId();
        dto.trainName = bookmark.getTrain().getName();
        dto.trainImageUrl = bookmark.getTrain().getImageUrl();
        return dto;
    }
}
