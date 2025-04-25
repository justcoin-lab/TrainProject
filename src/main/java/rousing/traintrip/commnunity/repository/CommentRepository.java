package rousing.traintrip.commnunity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rousing.traintrip.commnunity.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 게시글 ID로 댓글 목록 조회
    List<Comment> findByBoardIdOrderByCreatedDateAsc(Long boardId);
    
    // 작성자로 댓글 목록 조회
    List<Comment> findByWriter(String writer);

    // 기본 정렬 공통 메서드: 그룹 ID, 깊이, 순서 기준 정렬
    @Query("SELECT c FROM Comment c WHERE c.board.id = :boardId ORDER BY c.groupId ASC, c.depth ASC, c.order ASC")
    List<Comment> findByBoardIdOrderByHierarchy(@Param("boardId") Long boardId);

    // 특정 댓글과 그 하위 대댓글 조회
    @Query("SELECT DISTINCT c FROM Comment c LEFT JOIN FETCH c.children WHERE c.id = :commentId")
    Optional<Comment> findCommentWithChildren(@Param("commentId") Long commentId);



}
