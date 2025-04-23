package rousing.traintrip.commnunity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rousing.traintrip.commnunity.domain.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 게시글 ID로 댓글 목록 조회
    List<Comment> findByBoardIdOrderByCreatedDateAsc(Long boardId);
    
    // 작성자로 댓글 목록 조회
    List<Comment> findByWriter(String writer);
}
