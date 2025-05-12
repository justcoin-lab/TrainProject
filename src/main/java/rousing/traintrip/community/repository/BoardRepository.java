package rousing.traintrip.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rousing.traintrip.community.domain.Board;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    //제목에 특정 문자열이 포함된 게시글 검색
    Page<Board> findByTitleContaining(String title, Pageable pageable);

    //내용에 특정 문자열이 포함된 게시글 검색
    Page<Board> findByContentContaining(String content, Pageable pageable);

    //작성자에 특정 문자열이 폼함된 게시글 검색
    Page<Board> findByWriter(String writer, Pageable pageable);
    
    // 게시글별 댓글 수를 조회하는 쿼리
    @Query("SELECT b.id, COUNT(c) FROM Board b LEFT JOIN b.comments c WHERE b.id IN :ids GROUP BY b.id")
    List<Object[]> findCommentCountsByBoardIds(@Param("ids") List<Long> boardIds);
    
    // 게시글 조회 시 댓글 수를 함께 조회하는 쿼리
    @Query("SELECT b, COUNT(c) FROM Board b LEFT JOIN b.comments c WHERE b.id = :id GROUP BY b.id")
    Object[] findBoardWithCommentCount(@Param("id") Long id);
    
    // 게시글 ID 리스트로 댓글 수 카운트 조회
    @Query(value = """
           SELECT b.id, COUNT(c.id) as comment_count 
           FROM board b
           LEFT JOIN comment c ON b.id = c.board_id 
           GROUP BY b.id
           """, nativeQuery = true)
    List<Object[]> countCommentsForAllBoards();
}
