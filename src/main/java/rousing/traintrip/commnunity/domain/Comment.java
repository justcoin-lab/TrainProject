package rousing.traintrip.commnunity.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;



    //대댓글 관련 필드
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    // 댓글 깊이 (0: 일반 댓글, 1 이상: 대댓글 깊이)
    @Column(nullable = false)
    private Integer depth = 0;

    //댓글의 순서번호
    @Column(name="comment_order")
    private Integer order = 0;

    //그룹 ID(상위 댓글 ID)
    @Column(name="group_id")
    private Long groupId;

    // 댓글 내용 업데이트 메서드
    public void update(String content) {
        this.content = content;
    }

    //대댓글 추가 메서드
    public void addChild(Comment child) {
        this.children.add(child);
        child.parent = this;
        child.depth = depth + 1;

        if(this.parent == null) {
         //부모가 없으면 자신의 id가 그룹 id
            child.groupId = this.id;
        }else {
            child.groupId = this.groupId;
        }
        //순서 설정(자식 댓글의 순서는 현재 자식 목록 크기)
        child.order = children.size();
    }

    //새 댓글이 생성될때 그룹 id 설정(자신의 id로)
    @PostPersist
    public void onPostPersist() {
        if(this.parent == null && this.groupId == null) {
            this.groupId = this.id;
        }
    }

    //댓글인지 확인하는 메서드
    public boolean isReply() {
        return this.parent != null;
    }

    //루트 댓글(최상위 댓글)을 가져오는 메서드
    public Comment getRootComment() {
        if (this.parent == null) {
            return this;
        }
        return this.parent.getRootComment();
        }

    //대댓글 개수 조회 메서드
    public int getReplyCount() {
        return this.children.size();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}







