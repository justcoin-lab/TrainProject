package rousing.traintrip.commnunity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import rousing.traintrip.commnunity.dto.*;
import rousing.traintrip.commnunity.service.CommentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성 요청에 대한 데이터 바인딩 설정
     * - @Setter 대신 필드에 직접 액세스하는 방식 사용
     * - 보안상 이점: DTO에 setter가 없어 객체 생성 후 변경 방지
     */
    @InitBinder("commentCreateRequest")
    public void initBinderForCreate(WebDataBinder binder) {
        binder.initDirectFieldAccess();  // 필드에 직접 액세스
    }
    
    /**
     * 댓글 수정 요청에 대한 데이터 바인딩 설정
     * - @Setter 대신 필드에 직접 액세스하는 방식 사용
     * - 보안상 이점: DTO에 setter가 없어 객체 생성 후 변경 방지
     */
    @InitBinder("commentUpdateRequest")
    public void initBinderForUpdate(WebDataBinder binder) {
        binder.initDirectFieldAccess();  // 필드에 직접 액세스
    }

    @InitBinder("replyCreateRequest")
    public void initBinderForReply(WebDataBinder binder) {
        binder.initDirectFieldAccess();  // 필드에 직접 액세스
    }

    // 댓글 생성
    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentCreateRequest request,
                                           BindingResult result) {
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
            Long commentId = commentService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
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
        if (result.hasErrors()) {
            Map<String,String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                System.out.println("유효성 검증 오류: " +error.getField() + " - " + error.getDefaultMessage());
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            Long replyId = commentService.saveReply(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(replyId);

        }catch (Exception e) {
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
        // 유효성 검사 오류가 있는 경우 오류 응답
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // 수정 요청 처리
            Long commentId = commentService.update(id, request);
            return ResponseEntity.ok(commentId);
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
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.ok().build();
    }


}
