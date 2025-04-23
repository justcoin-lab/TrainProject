package rousing.traintrip.commnunity.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rousing.traintrip.commnunity.domain.Board;
import rousing.traintrip.commnunity.domain.Comment;
import rousing.traintrip.commnunity.dto.CommentCreateRequest;
import rousing.traintrip.commnunity.dto.CommentDTO;
import rousing.traintrip.commnunity.repository.BoardRepository;
import rousing.traintrip.commnunity.repository.CommentRepository;

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
        System.out.println("CommentService - save: boardId=" + request.getBoardId());
        try {
            Board board = boardRepository.findById(request.getBoardId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "게시글을 찾을 수 없습니다. ID: " + request.getBoardId()));
            
            Comment comment = request.toEntity(board);
            return commentRepository.save(comment).getId();
        } catch (Exception e) {
            System.out.println("Error in CommentService.save: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // 댓글 수정
    @Transactional
    public Long update(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "댓글을 찾을 수 없습니다. ID: " + commentId));

        comment.update(content);
        return comment.getId();
    }

    // 댓글 삭제
    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "댓글을 찾을 수 없습니다. ID: " + commentId));

        commentRepository.delete(comment);
    }

    // 게시글에 달린 댓글 목록 조회
    public List<CommentDTO> findByBoardId(Long boardId) {
        return commentRepository.findByBoardIdOrderByCreatedDateAsc(boardId)
                .stream()
                .map(CommentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 댓글 상세 조회
    public CommentDTO findById(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "댓글을 찾을 수 없습니다. ID: " + commentId));

        return CommentDTO.fromEntity(comment);
    }
}
