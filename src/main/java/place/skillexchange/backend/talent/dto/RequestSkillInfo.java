package place.skillexchange.backend.talent.dto;

import lombok.Data;

@Data
public class RequestSkillInfo {
    private Long talentId;
    private String requesterId;
    private String title;
    private String teachingSubject;
    private String teachedSubject;

    public RequestSkillInfo(Long talentId, String requesterId, String teachedSubject, String teachingSubject, String title) {
        this.talentId = talentId;
        this.requesterId = requesterId;
        this.teachedSubject = teachedSubject;
        this.teachingSubject = teachingSubject;
        this.title = title;
    }

}
