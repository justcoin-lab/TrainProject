package rousing.traintrip.community.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import rousing.traintrip.community.dto.*;
import rousing.traintrip.community.service.CommentService;
import rousing.traintrip.domain.User;
import rousing.traintrip.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    // 바인딩 설정 제거 - DTO에 @Setter를 추가하여 사용하는 방식으로 변경

    /**
     * 현재 인증된 사용자 정보를 가져옵니다.
     * @return 인증된 사용자 또는 빈 Optional
     */
    private Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            return Optional.of(authentication.getName());
        }
        return Optional.empty();
    }
    
    /**
     * 인증 여부를 확인합니다.
     * @throws AccessDeniedException 인증되지 않은 경우 예외 발생
     */
    private void checkAuthentication() {
        if (getCurrentUsername().isEmpty()) {
            throw new AccessDeniedException("댓글 작업을 위해서는 로그인이 필요합니다.");
        }
    }
    
    /**
     * 요청한 사용자가 댓글 작성자인지 확인합니다.
     * @param commentWriter 댓글 작성자
     * @throws AccessDeniedException 작성자가 아닌 경우 예외 발생
     */
    private void checkCommentOwnership(String commentWriter) {
        String currentUsername = getCurrentUsername()
                .orElseThrow(() -> new AccessDeniedException("댓글 작업을 위해서는 로그인이 필요합니다."));
        
        User currentUser = userService.getCurrentUser(currentUsername);
        
        // 댓글 작성자가 현재 사용자의 username 또는 nickname과 일치하는지 확인
        if (!commentWriter.equals(currentUser.getUsername()) && 
            !commentWriter.equals(currentUser.getNickname())) {
            throw new AccessDeniedException("자신이 작성한 댓글만 수정/삭제할 수 있습니다.");
        }
    }

    // 댓글 생성
    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentCreateRequest request,
                                           BindingResult result) {
        // 인증 확인
        checkAuthentication();
        
        System.out.println("Received comment request: " + request.getBoardId() + ", " + request.getWriter() + ", " + request.getContent());

        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                System.out.println("유효성 검증 오류: " + error.getField() + " - " + error.getDefaultMessage());
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // 현재 사용자 정보 설정
            String username = getCurrentUsername().orElseThrow(() ->
                new AccessDeniedException("댓글 작성을 위해서는 로그인이 필요합니다."));
            User currentUser = userService.getCurrentUser(username);
            request.setWriter(currentUser.getNickname()); // 닉네임을 작성자로 설정
            
            Long commentId = commentService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            System.out.println("유효성 검증 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 대댓글 생성
    @PostMapping("/reply")
    public ResponseEntity<?> createReply(@Valid @RequestBody ReplyCreateRequest request,
                                         BindingResult result) {
        // 인증 확인
        checkAuthentication();
        
        if (result.hasErrors()) {
            Map<String,String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                System.out.println("유효성 검증 오류: " +error.getField() + " - " + error.getDefaultMessage());
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            // 현재 사용자 정보 설정
            String username = getCurrentUsername().orElseThrow(() ->
                new AccessDeniedException("대댓글 작성을 위해서는 로그인이 필요합니다."));
            User currentUser = userService.getCurrentUser(username);
            request.setWriter(currentUser.getNickname()); // 닉네임을 작성자로 설정
            
            Long replyId = commentService.saveReply(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(replyId);

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            System.out.println("대댓글 저장 오류: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    // 댓글 수정 - 일관성 있는 DTO 패턴 사용
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id,
                                           @Valid @RequestBody CommentUpdateRequest request,
                                           BindingResult result) {
        // 인증 확인
        checkAuthentication();
        
        // 유효성 검사 오류가 있는 경우 오류 응답
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // 댓글 소유권 검증
            String commentWriter = commentService.getCommentWriter(id);
            checkCommentOwnership(commentWriter);
            
            // 수정 요청 처리
            Long commentId = commentService.update(id, request);
            return ResponseEntity.ok(commentId);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("댓글 수정중 오류가 발생했습니다: " + e.getMessage());
        }
    }
/*

    // 게시글의 댓글 목록 조회 (대댓글 기능 추가 전 코드)
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByBoardId(@PathVariable Long boardId) {
        List<CommentDTO> comments = commentService.findByBoardId(boardId);
        return ResponseEntity.ok(comments);
    }

*/

    //게시글의 댓글 계층구조 목록 조회(대댓글)
    @GetMapping("/board/{boardId}")
    public ResponseEntity <CommentListResponse> getComments(@PathVariable("boardId") Long boardId) {
        try {
            CommentListResponse commentListResponse = commentService.findCommentsByBoardId(boardId);
            return ResponseEntity.ok(commentListResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 특정 댓글과 그 대댓글 조회
    @GetMapping("/{commentId}/tree")
    public ResponseEntity<?> getCommentTree(@PathVariable("commentId") Long commentId) {
        try {
            CommentHierarchyDTO commentTree = commentService.findCommentWithReplies(commentId);
            return ResponseEntity.ok(commentTree);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("조회 실패");
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        // 인증 확인
        checkAuthentication();
        
        try {
            // 댓글 소유권 검증
            String commentWriter = commentService.getCommentWriter(id);
            checkCommentOwnership(commentWriter);
            
            commentService.delete(id);
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("댓글 삭제중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
