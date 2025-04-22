package rousing.traintrip.commnunity.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import rousing.traintrip.commnunity.domain.Board;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        boardRepository.deleteAll(); // 테스트 시작 전에 모든 게시글 데이터 삭제
    }

    @Test
    @DisplayName("게시글 저장 테스트")
    void saveTest() {
        //given
        Board board = Board.builder()
                .title("테스트제목")
                .content("테스트내용")
                .writer("테스트작성자")
                .build();
        //when
        Board saveBoard = boardRepository.save(board);

        //then
        assertThat(saveBoard).isNotNull();
        assertThat(saveBoard.getId()).isNotNull();
        assertThat(saveBoard.getTitle()).isEqualTo("테스트제목");
        assertThat(saveBoard.getContent()).isEqualTo("테스트내용");
        assertThat(saveBoard.getWriter()).isEqualTo("테스트작성자");

    }

    @Test
    @DisplayName("게시글 조회 테스트")
    void findById() {
        //given
        Board board = Board.builder()
                .title("테스트제목")
                .content("테스트내용")
                .writer("테스트작성자")
                .build();

        Board savedBoard = boardRepository.save(board);
        //when
        Optional<Board> foundBoard = boardRepository.findById(savedBoard.getId());


        //then
        assertThat(foundBoard).isPresent();
        assertThat(foundBoard.get().getTitle()).isEqualTo("테스트제목");
        assertThat(foundBoard.get().getContent()).isEqualTo("테스트내용");
        assertThat(foundBoard.get().getWriter()).isEqualTo("테스트작성자");
    }

    @Test
    @DisplayName("제목으로 게시글 검색 테스트")
    void findByTitleContainingTest() {
        //given

        Board board1 = Board.builder()
                .title("테스트 제목1")
                .content("테스트 내용1")
                .writer("테스트 작성자1")
                .build();

        Board board2 = Board.builder()
                .title("다른 제목")
                .content("다른 내용")
                .writer("다른 작성자")
                .build();

        Board board3 = Board.builder()
                .title("테스트 제목2")
                .content("테스트 내용2")
                .writer("테스트 작성자2")
                .build();

        boardRepository.save(board1);
        boardRepository.save(board2);
        boardRepository.save(board3);

        //when
        Pageable pageable = PageRequest.of(0, 10,
                Sort.by("id").descending());
        Page<Board> boardPage = boardRepository.findByTitleContaining("테스트", pageable);

        //then
        assertThat(boardPage.getTotalElements()).isEqualTo(2);
        assertThat(boardPage.getContent()).extracting("title")
                .containsExactlyInAnyOrder("테스트 제목1", "테스트 제목2");
    }
}