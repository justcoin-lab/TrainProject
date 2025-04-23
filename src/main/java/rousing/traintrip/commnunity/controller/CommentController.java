package rousing.traintrip.commnunity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import rousing.traintrip.commnunity.dto.CommentCreateRequest;
import rousing.traintrip.commnunity.dto.CommentDTO;
import rousing.traintrip.commnunity.service.CommentService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentCreateRequest request,
                                          BindingResult result) {
        System.out.println("Received comment request: " + request.getBoardId() + ", " + request.getWriter() + ", " + request.getContent());
        
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                System.out.println("Validation error: " + error.getField() + " - " + error.getDefaultMessage());
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Long commentId = commentService.save(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
        } catch (Exception e) {
            System.out.println("Error saving comment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 게시글의 댓글 목록 조회
    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByBoardId(@PathVariable Long boardId) {
        List<CommentDTO> comments = commentService.findByBoardId(boardId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.ok().build();
    }

    // 댓글 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, 
                                          @RequestBody Map<String, String> request) {
        String content = request.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("내용은 필수입니다.");
        }
        
        Long commentId = commentService.update(id, content);
        return ResponseEntity.ok(commentId);
    }
}
