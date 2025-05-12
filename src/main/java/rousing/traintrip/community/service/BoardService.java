package rousing.traintrip.community.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rousing.traintrip.community.domain.Board;
import rousing.traintrip.community.dto.BoardCreateRequest;
import rousing.traintrip.community.dto.BoardDTO;
import rousing.traintrip.community.dto.BoardUpdateRequest;
import rousing.traintrip.community.repository.BoardRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    
    // 댓글 수 카운트 맵을 조회하는 헬퍼 메서드
    private Map<Long, Long> getCommentCountMap() {
        List<Object[]> commentCounts = boardRepository.countCommentsForAllBoards();
        Map<Long, Long> commentCountMap = new HashMap<>();
        
        for (Object[] result : commentCounts) {
            Long boardId = ((Number) result[0]).longValue();
            Long count = ((Number) result[1]).longValue();
            commentCountMap.put(boardId, count);
        }
        
        return commentCountMap;
    }
    
    // Board 엔티티를 BoardDTO로 변환하며 댓글 수 정보 추가
    private BoardDTO convertToBoardDTO(Board board, Map<Long, Long> commentCountMap) {
        BoardDTO dto = BoardDTO.fromEntity(board);
        Long count = commentCountMap.getOrDefault(board.getId(), 0L);
        
        return BoardDTO.builder()
            .id(dto.getId())
            .title(dto.getTitle())
            .content(dto.getContent())
            .writer(dto.getWriter())
            .createdDate(dto.getCreatedDate())
            .modifiedDate(dto.getModifiedDate())
            .commentCount(count)
            .build();
    }

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
    @Transactional(readOnly = true)
    public BoardDTO findById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException
                        ("해당글 존재하지않음 ID : " + id));

        // 댓글 수는 업데이트된 값을 가져오기 위해 각각 조회
        Object[] result = boardRepository.findBoardWithCommentCount(id);
        long commentCount = 0;
        if (result != null && result.length > 1) {
            commentCount = (Long) result[1];
        }
        
        BoardDTO dto = BoardDTO.fromEntity(board);
        return BoardDTO.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .createdDate(dto.getCreatedDate())
                .modifiedDate(dto.getModifiedDate())
                .commentCount(commentCount)
                .build();
    }

    //게시글 페이징
    @Transactional(readOnly = true)
    public Page<BoardDTO> findAll(Pageable pageable) {
        Map<Long, Long> commentCountMap = getCommentCountMap();
        Page<Board> boardPage = boardRepository.findAll(pageable);
        return boardPage.map(board -> convertToBoardDTO(board, commentCountMap));
    }

    //제목으로 게시글 찾기 (페이징)
    @Transactional(readOnly = true)
    public Page<BoardDTO> findByTitleContaining(String title, Pageable pageable) {
        Map<Long, Long> commentCountMap = getCommentCountMap();
        Page<Board> boardPage = boardRepository.findByTitleContaining(title, pageable);
        return boardPage.map(board -> convertToBoardDTO(board, commentCountMap));
    }

    //내용으로 게시글 찾기 (페이징)
    @Transactional(readOnly = true)
    public Page<BoardDTO> findByContentContaining(String content, Pageable pageable) {
        Map<Long, Long> commentCountMap = getCommentCountMap();
        Page<Board> boardPage = boardRepository.findByContentContaining(content, pageable);
        return boardPage.map(board -> convertToBoardDTO(board, commentCountMap));
    }
    
    //작성자로 게시글 검색 (페이징)
    @Transactional(readOnly = true)
    public Page<BoardDTO> findByWriterPaging(String writer, Pageable pageable) {
        Map<Long, Long> commentCountMap = getCommentCountMap();
        Page<Board> boardPage = boardRepository.findByWriter(writer, pageable);
        return boardPage.map(board -> convertToBoardDTO(board, commentCountMap));
    }

    //작성자로 게시글 검색
    @Transactional(readOnly = true)
    public List<BoardDTO> findByWriter(String writer) {
        Map<Long, Long> commentCountMap = getCommentCountMap();
        List<Board> boards = boardRepository.findByWriter(writer, Pageable.unpaged()).getContent();
        
        return boards.stream()
                .map(board -> convertToBoardDTO(board, commentCountMap))
                .collect(Collectors.toList());
    }
    
    // 게시글 삭제
    @Transactional
    public void delete(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException
                        ("게시글을 찾을 수 없습니다. ID: " + id));

        boardRepository.delete(board);
    }
}
