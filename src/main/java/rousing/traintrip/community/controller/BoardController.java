package rousing.traintrip.community.controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rousing.traintrip.community.dto.*;
import rousing.traintrip.community.service.BoardService;
import rousing.traintrip.community.service.CommentService;

import java.security.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import rousing.traintrip.domain.User;
import rousing.traintrip.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final CommentService commentService;
    private final UserService userService;

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
                // 제목으로 검색하는 경우
                case "title":
                    boardList = boardService.findByTitleContaining(keyword, pageable);
                    break;
                // 내용으로 검색하는 경우
                case "content":
                    boardList = boardService.findByContentContaining(keyword, pageable);
                    break;
                // 작성자로 검색하는 경우
                case "writer":
                    boardList = boardService.findByWriterPaging(keyword, pageable);
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
    public String writeForm(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        
        // 현재 로그인한 사용자 정보 가져오기
        User user = userService.getCurrentUser(principal.getName());
        
        // 사용자의 닉네임을 자동으로 설정한 BoardCreateRequest 생성
        BoardCreateRequest request = BoardCreateRequest.builder()
                .writer(user.getNickname() != null ? user.getNickname() : user.getUsername())
                .build();
        
        model.addAttribute("boardCreateRequest", request);
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
                        BindingResult result,
                        Principal principal) {

        if (principal == null) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            return "board/write";
        }
        
        // 현재 로그인한 사용자 정보로 작성자 강제 설정
        User user = userService.getCurrentUser(principal.getName());
        request.setWriter(user.getNickname() != null ? user.getNickname() : user.getUsername());

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
    public String detail(@PathVariable("id") Long id, Model model, Principal principal) {
        BoardDTO boardDTO = boardService.findById(id);
        // 계층 구조로 댓글 조회
        CommentListResponse commentResponse = commentService.findCommentsByBoardId(id);

        model.addAttribute("board", boardDTO);
        model.addAttribute("comments", commentResponse.getComments());
        model.addAttribute("commentCount", commentResponse.getTotalCount());
        
        // 로그인한 사용자인 경우, 현재 사용자가 게시글 작성자인지 혹은 관리자인지 확인
        if (principal != null) {
            User currentUser = userService.getCurrentUser(principal.getName());
            String currentUsername = currentUser.getNickname() != null ? currentUser.getNickname() : currentUser.getUsername();
            boolean isWriter = boardDTO.getWriter().equals(currentUsername);
            boolean isAdmin = currentUser.getRole().equals(User.Role.ADMIN);
            model.addAttribute("isWriter", isWriter);
            model.addAttribute("isAdmin", isAdmin);
        }

        return "board/detail";
    }

    //게시글 수정 폼
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        
        BoardDTO boardDTO = boardService.findById(id);
        User currentUser = userService.getCurrentUser(principal.getName());
        String currentUsername = currentUser.getNickname() != null ? currentUser.getNickname() : currentUser.getUsername();
        
        // 글 작성자와 현재 사용자가 다르고, 관리자가 아니면 접근 거부
        if (!boardDTO.getWriter().equals(currentUsername) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            redirectAttributes.addFlashAttribute("errorMessage", "다른 사용자의 글은 수정할 수 없습니다.");
            return "redirect:/board/" + id;
        }

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
                       BindingResult result,
                       Principal principal,
                       RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        
        if (result.hasErrors()) {
            return "board/edit";
        }
        
        // 글 작성자 확인 및 권한 검사
        BoardDTO boardDTO = boardService.findById(request.getId());
        User currentUser = userService.getCurrentUser(principal.getName());
        String currentUsername = currentUser.getNickname() != null ? currentUser.getNickname() : currentUser.getUsername();
        
        // 글 작성자와 현재 사용자가 다르고, 관리자가 아니면 접근 거부
        if (!boardDTO.getWriter().equals(currentUsername) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            redirectAttributes.addFlashAttribute("errorMessage", "다른 사용자의 글은 수정할 수 없습니다.");
            return "redirect:/board/" + request.getId();
        }
        
        boardService.update(request);
        redirectAttributes.addFlashAttribute("successMessage", "게시글이 성공적으로 수정되었습니다.");
        return "redirect:/board/" + request.getId();
    }

    //@Setter를 안쓰기 위한 간편하고 안전한 방법중 하나
    @InitBinder("boardUpdateRequest")
    public void initBinderForUpdate(WebDataBinder binder) {
        binder.initDirectFieldAccess();
        // 필드에 직접 접근 설정
    }   // 해당 방식은 유효성에 걸려서 저장이 안돼는 경우 사용. 필드 직접 접근, 하지만 유효성 검사에서 오류가능함

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/auth/login";
        }
        
        // 글 작성자 확인 및 권한 검사
        BoardDTO boardDTO = boardService.findById(id);
        User currentUser = userService.getCurrentUser(principal.getName());
        String currentUsername = currentUser.getNickname() != null ? currentUser.getNickname() : currentUser.getUsername();
        
        // 글 작성자와 현재 사용자가 다르고, 관리자가 아니면 접근 거부
        if (!boardDTO.getWriter().equals(currentUsername) && 
            !currentUser.getRole().equals(User.Role.ADMIN)) {
            redirectAttributes.addFlashAttribute("errorMessage", "다른 사용자의 글은 삭제할 수 없습니다.");
            return "redirect:/board/" + id;
        }
        
        boardService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "게시글이 삭제되었습니다.");
        return "redirect:/board/list";
    }
}
