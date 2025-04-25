package rousing.traintrip.commnunity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 댓글 수정을 위한 DTO 클래스
 * - CreateRequest와 동일한 패턴으로 일관성 있게 설계
 * - 수정 시에는 댓글 내용(content)만 변경 가능
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest {

    @NotBlank(message = "내용을 입력해 주세요")
    private String content;
    
    // 필요에 따라 나중에 수정 사유 등 추가 필드를 확장할 수 있음
    // private String updateReason;
}
