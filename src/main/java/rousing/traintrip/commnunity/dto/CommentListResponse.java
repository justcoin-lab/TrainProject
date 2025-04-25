package rousing.traintrip.commnunity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//댓글 목록 응답 DTO
//- 계층 구조를 표현해서 클라이언트에 전달//- 댓글 목록을 표현하기 위한 DTO


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentListResponse {

    @Builder.Default
    private List<CommentHierarchyDTO> comments = new ArrayList<>();
    private int totalCount;
    private int rootCount; // 최상위 댓글 수
    private int replyCount; // 대댓글 수

    // 댓글 목록을 계층 구조로 구성
    public static CommentListResponse fromCommentDTOList(List<CommentDTO> commentsDTOList) {
        // 최상의 댓글만 필터링(parentId가 null 인 댓글)
        List<CommentDTO> rootComments = commentsDTOList.stream()
                .filter(dto -> dto.getParentId() == null)
                .collect(Collectors.toList());

        // 그룹별로 정리된 계층 구조 목록 구성
        List<CommentHierarchyDTO> hierarchyDTOs = rootComments.stream()
                .map(CommentHierarchyDTO::fromCommentDTO)
                .collect(Collectors.toList());
        // 댓글 수 계산
        int total = commentsDTOList.size();
        int rootCount = rootComments.size();
        int replyCount = total - rootCount;

        return CommentListResponse.builder()
                .comments(hierarchyDTOs)
                .totalCount(total)
                .rootCount(rootCount)
                .replyCount(replyCount)
                .build();
    }

    // 2단계 구조로 댓글과 대댓글을 그룹핑
    public static CommentListResponse fromFlatCommentList(List<CommentDTO> commentDTOList) {
        // 댓글 ID를 기반으로 맵 생성
        Map<Long, CommentHierarchyDTO> commentMap = commentDTOList.stream()
                .collect(Collectors.toMap(
                        CommentDTO::getId,
                        CommentHierarchyDTO::fromCommentDTO
                ));
        // 루트 댓글만 따로 보관할 리스트
        List<CommentHierarchyDTO> rootComments = new ArrayList<>();

        // 부모-자식 관계 설정
        commentDTOList.forEach(dto ->{
            CommentHierarchyDTO current = commentMap.get(dto.getId());

            if(dto.getParentId() == null) {
            // 부모가 없는 경우 루트 댓글 목록에 추가
                rootComments.add(current);

            } else {
                //부모가 있는 경우 부모의 자식 목록에 추가
                CommentHierarchyDTO parent = commentMap.get(dto.getParentId());
                if (parent != null) {
                    parent.getChildComments().add(current);
                }
                }
            });

        //결과 생성
        int total = commentDTOList.size();
        int root = rootComments.size();

        return CommentListResponse.builder()
                .comments(rootComments)
                .totalCount(total)
                .rootCount(root)
                .replyCount(total - root)
                .build();


    }

}
