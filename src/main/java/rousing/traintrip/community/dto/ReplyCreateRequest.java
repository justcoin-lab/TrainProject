package rousing.traintrip.community.dto;

// 대댓글 생성 요청 DTO - 필수적으로 부모 댓글 ID가 포함되어야 함.

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rousing.traintrip.community.domain.Board;
import rousing.traintrip.community.domain.Comment;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyCreateRequest {

    @NotNull(message = "게시글 ID는 필수 값입니다.")
    private Long boardId;

    @NotNull(message = "부모 댓글 ID는 필수 값입니다.")
    private Long parentId;

    @NotBlank(message = "내용을 입력해 주세요")
    private String content;

    // @NotBlank 제거 - 사용자 인증으로 로그인한 사용자의 닉네임으로 자동 설정
    private String writer;

    // DTO -> Entity 변환
    public Comment toEntity(Board board, Comment parent) {
        Comment reply = Comment.builder()
                .content(content)
                .writer(writer)
                .board(board)
                .parent(parent)
                .depth(parent.getDepth() +1)
                .build();
        return reply;
    }
}
