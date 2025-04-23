package rousing.traintrip.commnunity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import rousing.traintrip.commnunity.dto.BoardCreateRequest;
import rousing.traintrip.commnunity.dto.BoardDTO;
import rousing.traintrip.commnunity.dto.BoardUpdateRequest;
import rousing.traintrip.commnunity.dto.CommentDTO;
import rousing.traintrip.commnunity.service.BoardService;
import rousing.traintrip.commnunity.service.CommentService;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;

    // 게시글 리스트 페이지
    @GetMapping("/list")
    public String list(Model model,
                       @PageableDefault(size = 10, sort = "id",
                               direction = Sort.Direction.DESC) Pageable pageable,
                       @RequestParam(required = false) String searchType,
                       @RequestParam(required = false) String keyword) {

        Page<BoardDTO> boardList;

        if (keyword == null || keyword.isEmpty()) {
            boardList = boardService.findAll(pageable);
        } else {
            switch (searchType) {
                case "title":
                    boardList = boardService.findByTitle(keyword, pageable);
                    break;
                case "content":
                    boardList = boardService.findByContent(keyword, pageable);
                    break;
                case "writer":
                    boardList = boardService.findByWriter(keyword, pageable);
                    break;
                default:
                    boardList = boardService.findAll(pageable);
            }
        }
        int startPage = Math.max(1, boardList.getPageable().getPageNumber() - 4);
        int endPage = Math.min(boardList.getTotalPages(), boardList.getPageable().getPageNumber() + 4);

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);


        return "board/list";
    }

    //게시물 작성 폼 페이지
    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("boardCreateRequest", new BoardCreateRequest());
        return "board/write";
    }

    /*@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
        // 필드에 직접 접근 설정
    }*/   // 해당 방식은 유효성에 걸려서 저장이 안돼는 경우 사용. 필드 직접 접근, 하지만 유효성 검사에서 오류가능함

    //게시물 작성 처리
    @PostMapping("/write")
    public String write(@Valid BoardCreateRequest request,
                        BindingResult result) {

        if (result.hasErrors()) {
            return "board/write";
        }

        boardService.save(request);
        return "redirect:/board/list";
    }

    //@Setter를 안쓰기 위한 간편하고 안전한 방법중 하나
    @InitBinder("boardCreateRequest")
    public void initBinderForCreate(WebDataBinder binder) {
        binder.initDirectFieldAccess();
        // 필드에 직접 접근 설정
    }   // 해당 방식은 유효성에 걸려서 저장이 안돼는 경우 사용. 필드 직접 접근, 하지만 유효성 검사에서 오류가능함


    //게시물 상세 페이지
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        List<CommentDTO> comments = commentService.findByBoardId(id);
        
        model.addAttribute("board", boardDTO);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCount", comments.size());
        
        return "board/detail";
    }

    //게시글 수정 폼
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);

        BoardUpdateRequest updateRequest = BoardUpdateRequest.builder()
                .id(boardDTO.getId())
                .title(boardDTO.getTitle())
                .content(boardDTO.getContent())
                .build();
        model.addAttribute("boardUpdateRequest", updateRequest);
        model.addAttribute("writer", boardDTO.getWriter());
        return "board/edit";
    }

    //게시글 수정 처리
    @PostMapping("/edit/{id}")
    public String edit(@Valid BoardUpdateRequest request,
                       BindingResult result) {
        if (result.hasErrors()) {
            return "board/edit";
        }
        boardService.update(request);
        return "redirect:/board/" + request.getId();
    }

    //@Setter를 안쓰기 위한 간편하고 안전한 방법중 하나
    @InitBinder("boardUpdateRequest")
    public void initBinderForUpdate(WebDataBinder binder) {
        binder.initDirectFieldAccess();
        // 필드에 직접 접근 설정
    }   // 해당 방식은 유효성에 걸려서 저장이 안돼는 경우 사용. 필드 직접 접근, 하지만 유효성 검사에서 오류가능함

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        boardService.delete(id);
        return "redirect:/board/list";
    }
}
