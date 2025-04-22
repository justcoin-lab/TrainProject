package rousing.traintrip.commnunity.service;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import rousing.traintrip.commnunity.domain.Board;
import rousing.traintrip.commnunity.dto.BoardCreateRequest;
import rousing.traintrip.commnunity.dto.BoardDTO;
import rousing.traintrip.commnunity.dto.BoardUpdateRequest;
import rousing.traintrip.commnunity.repository.BoardRepository;



import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName("게시글 저장 통합 테스트")
    void saveBoardTest() {
        //given
        BoardCreateRequest request = BoardCreateRequest.builder()
                .title("통합 테스트 제목")
                .content("통합 테스트 내용")
                .writer("통합 테스트 작성자")
                .build();

        //when
        Long boardId = boardService.save(request);


        //then
        assertThat(boardId).isNotNull();

        Board savedBoard = boardRepository.findById(boardId).orElseThrow();

        assertThat(savedBoard.getTitle()).isEqualTo("통합 테스트 제목");
        assertThat(savedBoard.getContent()).isEqualTo("통합 테스트 내용");
        assertThat(savedBoard.getWriter()).isEqualTo("통합 테스트 작성자");
        assertThat(savedBoard.getCreatedDate()).isNotNull();


    }

    @Test
    @DisplayName("게시글 수정")
    void updateBoardTest() {
        //given

        BoardCreateRequest request = BoardCreateRequest.builder()
                .title("통합 테스트 제목")
                .content("통합 테스트 내용")
                .writer("통합 테스트 작성자")
                .build();

        Long boardId = boardService.save(request);

        //when

        BoardUpdateRequest updateRequest = BoardUpdateRequest.builder()
                .id(boardId)
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        boardService.update(updateRequest);
    }

    @Test
    @DisplayName("게시글 검색")
    void searchBoardTest() {
        //given

        BoardCreateRequest request = BoardCreateRequest.builder()
                .title("통합 테스트 제목")
                .content("통합 테스트 내용")
                .writer("통합 테스트 작성자")
                .build();

        Long boardId = boardService.save(request);

        //when
        BoardDTO boardDTO = boardService.findById(boardId);

        //then
        assertThat(boardDTO.getTitle()).isEqualTo("통합 테스트 제목");
        assertThat(boardDTO.getContent()).isEqualTo("통합 테스트 내용");


    }


}