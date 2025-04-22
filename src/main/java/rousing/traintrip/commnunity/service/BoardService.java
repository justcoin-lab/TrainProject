package rousing.traintrip.commnunity.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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

    //게시글 수정
    @Transactional
    public Long update(BoardUpdateRequest request) {
        Board board = boardRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException
                        ("게시글을 찾을 수 없습니다. ID: " + request.getId()));

        board.update(request.getTitle(), request.getContent());

        return board.getId();
    }

    //게시글 상세 조회
    public BoardDTO findById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException
                        ("해당글 존재하지않음 ID : " + id));

        return BoardDTO.fromEntity(board);
    }

}
