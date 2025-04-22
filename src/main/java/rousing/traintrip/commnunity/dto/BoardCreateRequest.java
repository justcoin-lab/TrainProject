package rousing.traintrip.commnunity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rousing.traintrip.commnunity.domain.Board;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreateRequest {

    @NotBlank(message ="제목을 입력해 주세요")
    private String title;

    @NotBlank(message = "내용은 입력해 주세요")
    private String content;

    @NotBlank(message = "작성자를 입력해 주세요")
    private String writer;

    //DTO -> Entity
    public Board toEntity() {
        return Board.builder()
                .title(title)
                .content(content)
                .writer(writer)
                .build();
    }
}
