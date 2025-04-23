package rousing.traintrip.commnunity.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rousing.traintrip.commnunity.domain.Board;
import rousing.traintrip.commnunity.dto.BoardCreateRequest;
import rousing.traintrip.commnunity.dto.BoardDTO;
import rousing.traintrip.commnunity.dto.BoardUpdateRequest;
import rousing.traintrip.commnunity.repository.BoardRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    //게시글 저장
    @Transactional
    public Long save(BoardCreateRequest request) {
        return boardRepository.save(request.toEntity()).getId();
    }


    //게시글 상세 조회
    public BoardDTO findById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException
                        ("해당글 존재하지않음 ID : " + id));

        return BoardDTO.fromEntity(board);
    }

    //게시글 페이징
    @Transactional(readOnly = true)
    public Page<BoardDTO> findAll(Pageable pageable) {
        return boardRepository.findAll(pageable).map(BoardDTO::fromEntity);
    }

    //제목으로 게시글 찾기
    @Transactional(readOnly = true)
    public Page<BoardDTO> findByTitle(String title, Pageable pageable) {
        return boardRepository.findByTitleContaining(title,pageable)
                .map(BoardDTO::fromEntity);
    }

    //내용으로 게시글 찾기
    public Page<BoardDTO> findByContent(String content, Pageable pageable) {
        return boardRepository.findByContentContaining(content, pageable)
                .map(BoardDTO::fromEntity);
    }

    //작성자로 게시글 검색
    public Page<BoardDTO> findByWriter(String writer, Pageable pageable) {
        return boardRepository.findByWriter(writer,pageable)
                .map(BoardDTO::fromEntity);
    }
}
