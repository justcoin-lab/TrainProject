package rousing.traintrip.community.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rousing.traintrip.community.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long id;
    private String content;
    private String writer;
    private Long boardId;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    // 대댓글 관련 필드
    private Long parentId;
    private Integer depth;
    private List<CommentDTO> childComments;
    private Long groupId; // 그룹 ID
    private Integer order; // 정렬 순서
    private boolean isReply; // 대댓글 여부
    private int replyCount; // 대댓글 개수


    // Entity -> DTO
    public static CommentDTO fromEntity(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId() != null ? comment.getId() : null)
                .content(comment.getContent())
                .writer(comment.getWriter())
                .boardId(comment.getBoard().getId() != null ? comment.getBoard().getId() : null)
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .depth(comment.getDepth() != null ? comment.getDepth() : 0)
                .groupId(comment.getGroupId() != null ? comment.getGroupId() : comment.getId())
                .order(comment.getOrder() != null ? comment.getOrder() : 0)
                .isReply(comment.getParent() != null)
                .replyCount(comment.getChildren() != null ? comment.getReplyCount() : 0)
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .childComments(comment.getChildren() !=null && !comment.getChildren().isEmpty() ? comment.getChildren()
                        .stream().map(CommentDTO::fromEntity).collect(Collectors.toList()) : null)
                .build();
    }

    // 댓글 트리구조 문자열로 보여주기 (depth가 깊어질수록 들여쓰기)
    public String getFormattedContent() {
        if(depth>0) {
            return "\u21AA".repeat(depth) + content;
        }
        return content;
    }
}
