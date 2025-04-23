package rousing.traintrip.commnunity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import rousing.traintrip.commnunity.dto.BoardDTO;
import rousing.traintrip.commnunity.service.BoardService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // 게시글 목록 페이지
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
}
