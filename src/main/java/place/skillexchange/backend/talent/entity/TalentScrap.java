package place.skillexchange.backend.talent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import place.skillexchange.backend.user.entity.User;

@Entity
@Table(name = "talent_scrap")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class TalentScrap {

    @EmbeddedId
    private TalentScrapId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("talentId")
    @JoinColumn(name = "talent_id")
    private Talent talent;

    //of 정적 메서드: User와 Talent 객체를 인자로 받아 TalentScrap 객체를 생성
    public static TalentScrap of(User user, Talent talent) {
        TalentScrapId id = TalentScrapId.builder()
                .userId(user.getId())
                .talentId(talent.getId())
                .build();

        return TalentScrap.builder()
                .id(id)
                .user(user)
                .talent(talent)
                .build();
    }
}