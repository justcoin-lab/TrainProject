package rousing.traintrip.commnunity.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import rousing.traintrip.commnunity.domain.Board;



public interface BoardRepository extends JpaRepository<Board, Long> {

    //제목에 특정 문자열이 포함된 게시글 검색
    Page<Board> findByTitleContaining(String title, Pageable pageable);

    //내용에 특정 문자열이 포함된 게시글 검색
    Page<Board> findByContentContaining(String content, Pageable pageable);

    //작성자에 특정 문자열이 폼함된 게시글 검색
    Page<Board> findByWriter(String writer, Pageable pageable);


}
