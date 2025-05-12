package rousing.traintrip.community.dto;


//댓글 계층 구조를 표현하기 위한 DTO 클래스, 트리 구조로 댓글과 대댓글을 표현

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentHierarchyDTO {

    private Long id;
    private String content;
    private String writer;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Long parentId; // 부모 댓글 ID
    private Long groupId; // 그룹 ID
    private Integer depth;
    private Integer order; // 정렬 순서
    private boolean isReply; // 대댓글 여부
    private int replyCount; // 대댓글 개수

    @Builder.Default
    private List<CommentHierarchyDTO> childComments = new ArrayList<>();

    // CommentDTO에서 변환
    public static CommentHierarchyDTO fromCommentDTO(CommentDTO commentDTO) {
        CommentHierarchyDTO dto = CommentHierarchyDTO.builder()
                .id(commentDTO.getId())
                .content(commentDTO.getContent())
                .writer(commentDTO.getWriter())
                .createdDate(commentDTO.getCreatedDate())
                .modifiedDate(commentDTO.getModifiedDate())
                .depth(commentDTO.getDepth())
                .parentId(commentDTO.getParentId())
                .groupId(commentDTO.getGroupId())
                .order(commentDTO.getOrder())
                .isReply(commentDTO.isReply())
                .replyCount(commentDTO.getReplyCount())
                .build();

        // 하위 댓글이 있는 경우 재귀적으로 변환
        if (commentDTO.getChildComments() != null && !commentDTO.getChildComments().isEmpty()) {
            for (CommentDTO childDTO : commentDTO.getChildComments() ) {
                dto.getChildComments().add(fromCommentDTO(childDTO));
            }
        }
        return dto;
    }

    //들여쓰기가 포함된 내용 반환 메서드
    public String getFormattedContent() {
        if (depth > 0) {
            return "\u21AA ".repeat(depth) + content;
        }
        return content;
    }
}
