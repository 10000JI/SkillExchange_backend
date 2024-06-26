package place.skillexchange.backend.talent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import place.skillexchange.backend.comment.entity.Comment;
import place.skillexchange.backend.common.entity.BaseEntity;
import place.skillexchange.backend.common.util.DayOfWeekUtil;
import place.skillexchange.backend.talent.dto.TalentDto;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.user.entity.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Talent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "talent_id")
    private Long id;

    /**
     * 단방향 매핑 (단뱡향일 때는 Cascade 작동 X, User 삭제 시 Talent 삭제 후 User 삭제 해야 함)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="writer")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    /**
     * 단방향 매핑
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teached_subject_id", nullable = false)
    private SubjectCategory teachedSubject;

    /**
     * 단방향 매핑
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teaching_subject_id", nullable = false)
    private SubjectCategory teachingSubject;

    @Column(name = "talent_title", length = 50, nullable = false)
    private String title;

    @Column(name = "talent_content", length = 4000, nullable = false)
    private String content;

    @Column(name = "talent_hit")
    @ColumnDefault("0")
    private Long hit;

    @Column(name = "max_age", nullable = false)
    private Long maxAge;

    @Column(name = "min_age", nullable = false)
    private Long minAge;

    @Enumerated(EnumType.STRING)
    @Column(length = 6)
    private GenderForTalent gender;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private Set<DayOfWeek> dayOfWeek = new HashSet<>();
    /**
     * 이미지와 양방향 매핑
     */
    @OneToMany(mappedBy = "talent", cascade = CascadeType.PERSIST)
    private List<File> files = new ArrayList<>();

    /**
     * Talent와 TalentScrap 양방향 매핑
     */
    //Talent 엔티티를 삭제하기 전에 해당 Talent 엔티티와 관련된 모든 TalentScrap 엔티티를 삭제
    @OneToMany(mappedBy = "talent", cascade = CascadeType.REMOVE)
    private Set<TalentScrap> talentScraps = new HashSet<>();

    /**
     * 양방향
     */
    @OneToMany(mappedBy = "talent", cascade = CascadeType.PERSIST)
    private List<Comment> comments = new ArrayList<>();

    // 재능교환 요청 필드
    @Enumerated(EnumType.STRING)
    @Column(name = "exchange_status")
    private ExchangeStatus exchangeStatus = ExchangeStatus.PENDING;

    // 재능교환 요청은 다수가 될 수 있음
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "talent_exchange_requests",
            joinColumns = @JoinColumn(name = "talent_id"),
            inverseJoinColumns = @JoinColumn(name = "requester_id")
    )
    private Set<User> exchangeRequesters = new HashSet<>();

    /**
     * 게시물 내용, 장소, 가르쳐줄 분야, 가르침 받을 분야, 요일, 연령대 수정
     * : null 이 아니라면 변경내용이 존재하는 것, null이라면 변경내용이 존재하지 않으므로 그대로 유지
     */
    public void changeTalent(TalentDto.TalentUpdateRequest dto, Place place, SubjectCategory teachedSubject, SubjectCategory teachingSubject) {
        this.content = dto.getContent();
        this.title = dto.getTitle();
        if (place != null) {
            this.place = place;
        }
        if (teachedSubject != null) {
            this.teachedSubject = teachedSubject;
        }
        if (teachingSubject != null) {
            this.teachingSubject = teachingSubject;
        }

        this.gender = GenderForTalent.valueOf(dto.getGender());
        this.minAge = dto.getMinAge();
        this.maxAge = dto.getMaxAge();
        this.dayOfWeek = DayOfWeekUtil.convertSelectedDaysToEnum(dto.getSelectedDays());
    }

    public void updateHit() {
        hit++;
    }
    public User getWriter() {
        return writer != null ? writer : new User(); // 또는 적절한 기본값
    }

    public void addExchangeRequester(User requester) {
        this.exchangeRequesters.add(requester);
        if (this.exchangeStatus == ExchangeStatus.PENDING) {
            this.exchangeStatus = ExchangeStatus.REQUESTED;
        }
    }
    public void completeExchange() {
        this.exchangeStatus = ExchangeStatus.COMPLETED;
    }
}

