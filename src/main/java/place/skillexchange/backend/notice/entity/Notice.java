package place.skillexchange.backend.notice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import place.skillexchange.backend.common.entity.BaseEntity;
import place.skillexchange.backend.comment.entity.Comment;
import place.skillexchange.backend.notice.dto.NoticeDto;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Notice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    /**
     * 단방향 매핑 (단뱡향일 때는 Cascade 작동 X, User 삭제 시 Notice 삭제 후 User 삭제 해야 함)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="writer")
    private User writer;

    @Column(name = "notice_title", length = 50, nullable = false)
    private String title;

    @Column(name = "notice_content", length = 4000, nullable = false)
    private String content;

    @Column(name = "notice_hit")
    @ColumnDefault("0")
    private Long hit;

    /**
     * 이미지와 양방향 매핑
     */
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private List<File> files = new ArrayList<>();

    /**
     * 양방향
     */
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    /**
     * 공지사항 제목,내용 수정
     */
    public void changeNotice(NoticeDto.NoticeUpdateRequest dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
    }

    /**
     * 조회수 +1
     */
    public void updateHit() {
        this.hit++; // 조회수 증가
    }

}
