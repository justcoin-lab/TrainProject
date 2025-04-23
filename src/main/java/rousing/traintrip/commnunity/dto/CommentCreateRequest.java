package rousing.traintrip.commnunity.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rousing.traintrip.commnunity.domain.Board;
import rousing.traintrip.commnunity.domain.Comment;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    @NotNull(message = "게시물 ID는 필수입니다")
    private Long boardId;

    @NotBlank(message = "내용을 입력해 주세요")
    private String content;

    @NotBlank(message = "작성자를 입력해 주세요")
    private String writer;

    // DTO to Entity
    public Comment toEntity(Board board) {
        return Comment.builder()
                .content(content)
                .writer(writer)
                .board(board)
                .build();
    }
}
