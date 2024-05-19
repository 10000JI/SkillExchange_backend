package place.skillexchange.backend.talent.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalentScrapId implements Serializable {
    //Serializable 하는 이유
    private String userId;
    private Long talentId;
}
