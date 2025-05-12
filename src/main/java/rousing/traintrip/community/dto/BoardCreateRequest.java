package rousing.traintrip.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import rousing.traintrip.community.domain.Board;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCreateRequest {

    @NotBlank(message ="제목을 입력해 주세요")
    private String title;

    @NotBlank(message = "내용은 입력해 주세요")
    private String content;

    @NotBlank(message = "작성자를 입력해 주세요")
    private String writer;
    
    // 로그인한 사용자 정보로 작성자 설정 가능하도록 setter 추가
    public void setWriter(String writer) {
        this.writer = writer;
    }


    //DTO -> Entity
    public Board toEntity() {
        return Board.builder()
                .title(title)
                .content(content)
                .writer(writer)
                .build();
    }
}
