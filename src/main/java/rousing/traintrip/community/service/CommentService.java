package rousing.traintrip.community.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rousing.traintrip.community.domain.Board;
import rousing.traintrip.community.domain.Comment;
import rousing.traintrip.community.dto.*;
import rousing.traintrip.community.repository.BoardRepository;
import rousing.traintrip.community.repository.CommentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    // 댓글 저장
    @Transactional
    public Long save(CommentCreateRequest request) {
        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "게시글을 찾을 수 없습니다. ID: " + request.getBoardId()));
        Comment comment;

        // 부모 댓글 ID가 있는 경우 (대댓글)- 댓글을 달기 위한 준비 단계로 댓글이 있는지 찾아야함
        if(request.getParentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException
                            ("대댓글을 찾을 수 없습니다. ID: " + request.getParentId()));

            comment = request.toEntity(board, parentComment);
            parentComment.addChild(comment); // 부모와 자식 관계 설정
        }else {
            //일반댓글
            comment = request.toEntity(board);
        }

        Comment saveComment = commentRepository.save(comment);

        //가장 상위 댓글 경우 자신의 id로 그룹 ID 설정
        if (request.getParentId() == null && saveComment.getGroupId() == null) {
            saveComment.onPostPersist(); //그룹 아이디 설정
            commentRepository.save(saveComment);
        }
        return saveComment.getId();
    }

    // 게시글에 달린 댓글 목록 조회
    public List<CommentDTO> findByBoardId(Long boardId) {
        return commentRepository.findByBoardIdOrderByCreatedDateAsc(boardId)
                .stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    //대댓글 저장(전용 메서드)
    @Transactional
    public Long saveReply(ReplyCreateRequest request) {
        Board board = boardRepository.findById(request.getBoardId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("게시글을 찾을 수 없습니다. ID: " + request.getBoardId()));

        Comment parentComment = commentRepository.findById(request.getParentId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("댓글을 찾을 수 없습니다. ID: " + request.getParentId()));
        Comment reply = request.toEntity(board, parentComment);
        parentComment.addChild(reply); //부모, 자식 관계 설정(groupID, depth, order 설정됨)

        return commentRepository.save(reply).getId();
    }

    //모든 댓글을 계층 구조로 조회(기본 메서드)
    public CommentListResponse findCommentsByBoardId(Long boardId) {
        List<CommentDTO> comments = commentRepository.findByBoardIdOrderByHierarchy(boardId)
                .stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());

        return CommentListResponse.fromCommentDTOList(comments);
    }

    // 특정 댓글과 그 대댓글 조회
    public CommentHierarchyDTO findCommentWithReplies(Long commentId) {
        Comment rootComment = commentRepository.findCommentWithChildren(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found ID: " + commentId));

        return CommentHierarchyDTO.fromCommentDTO(CommentDTO.fromEntity(rootComment));
    }

    // 댓글 수정 - 일관성 있는 DTO 패턴 사용
    @Transactional
    public Long update(Long commentId, CommentUpdateRequest request) {
        // 댓글 조회 - 없으면 예외 발생
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "댓글을 찾을 수 없습니다. ID: " + commentId));

        // 댓글 내용 업데이트
        comment.update(request.getContent());
        return comment.getId();
    }

    /**
     * 댓글 작성자를 조회합니다.
     * @param commentId 댓글 ID
     * @return 댓글 작성자
     * @throws EntityNotFoundException 댓글을 찾을 수 없는 경우
     */
    public String getCommentWriter(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "댓글을 찾을 수 없습니다. ID: " + commentId));
        return comment.getWriter();
    }
    
    // 댓글 삭제
    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "댓글을 찾을 수 없습니다. ID: " + commentId));

        commentRepository.delete(comment);
    }
}
