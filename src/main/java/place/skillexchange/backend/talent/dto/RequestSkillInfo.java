package place.skillexchange.backend.talent.dto;

import lombok.Data;

@Data
public class RequestSkillInfo {
    private Long talentId;
    private String requesterId;

    public RequestSkillInfo(Long talentId, String requesterId) {
        this.talentId = talentId;
        this.requesterId = requesterId;
    }

}
