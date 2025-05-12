package rousing.traintrip.community.dto;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    @NotNull(message = "게시물 ID는 필수입니다")
    private Long boardId;

    @NotBlank(message = "내용을 입력해 주세요")
    private String content;

    // @NotBlank 제거 - 사용자 인증으로 로그인한 사용자의 닉네임으로 자동 설정
    private String writer;

    //부모댓글 ID (null이면 일반 댓글, 값이 있으면 대댓글)
    private Long parentId;

    // DTO to Entity(일반 댓글)
    public Comment toEntity(Board board) {
        return Comment.builder()
                .content(content)
                .writer(writer)
                .board(board)
                .depth(0) // 일반 댓글은 깊이가 0
                .order(0) // 초기 순서는 0
                .build();
    }

    // DTO to Entity(대댓글)
    public Comment toEntity(Board board, Comment parent) {
        // 깊이는 부모의 깊이 + 1
        int newDepth = parent.getDepth() +1;

        Comment child = Comment.builder()
                .content(content)
                .writer(writer)
                .board(board)
                .parent(parent)
                .depth(newDepth)
                .build();
        // 부모와의 관계 설정은 service 에서 parent.addChild(child) 호출로 처리
        return child;
    }
}
