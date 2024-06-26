package place.skillexchange.backend.talent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import place.skillexchange.backend.common.util.DayOfWeekUtil;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.talent.entity.*;
import place.skillexchange.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TalentDto {

    /**
     * 게시물 등록 시 요청된 Dto
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TalentRegisterRequest {

        @NotBlank(message = "작성자: 필수 정보입니다.")
        private String writer;

        @NotBlank(message = "제목: 필수 정보입니다.")
        private String title;

        @NotBlank(message = "내용: 필수 정보입니다.")
        private String content;

        @NotBlank(message = "장소: 필수 정보입니다.")
        private String placeName;

        @NotBlank(message = "가르쳐 줄 분야: 필수 정보입니다.")
        private String teachingSubject;

        @NotBlank(message = "가르침 받을 분야: 필수 정보입니다.")
        private String teachedSubject;

        @NotNull(message = "최소 연령대: 필수 정보입니다.")
        private Long minAge;

        @NotNull(message = "최대 연령대: 필수 정보입니다.")
        private Long maxAge;

        @NotEmpty(message = "요일: 최소 한 개 이상의 요일을 선택해야 합니다.")
        private Set<String> selectedDays = new HashSet<>();

        @NotBlank(message = "성: 필수 정보입니다.")
        private String gender;

        /* Dto -> Entity */
        public Talent toEntity(User user, Place place, SubjectCategory teachingSubject, SubjectCategory teachedSubject) {
            Talent talent = Talent.builder()
                    .writer(user)
                    .content(content)
                    .title(title)
                    .teachingSubject(teachingSubject)
                    .teachedSubject(teachedSubject)
                    .place(place)
                    .hit(0L)
                    .dayOfWeek(DayOfWeekUtil.convertSelectedDaysToEnum(selectedDays))
                    .minAge(minAge)
                    .maxAge(maxAge)
                    .gender(GenderForTalent.valueOf(gender)) // 문자열로 받은 gender 값을 GenderForTalent 열거형(enum)으로 변환하여 빌더 패턴에 설정
                    .exchangeStatus(ExchangeStatus.PENDING)
                    .build();
            return talent;
        }
    }

    /**
     * 게시물 등록 성공시 보낼 Dto
     */
    @Getter
    public static class TalentRegisterResponse {
        private Long id;
        private String writer;
        private String title;
        private String content;
        private String placeName;
        private String teachingSubject;
        private String teachedSubject;
        private Set<String> selectedDays;
        private String gender;
        private Long minAge;
        private Long maxAge;
        private LocalDateTime regDate;
        private LocalDateTime modDate;
        private List<String> oriName = new ArrayList<>();
        private List<String> imgUrl = new ArrayList<>();
        private int returnCode;
        private String returnMessage;

        /* Entity -> Dto */
        public TalentRegisterResponse(User user, Talent talent, List<File> files, int returnCode, String returnMessage) {
            this.writer = user.getId();
            this.id = talent.getId();
            this.content = talent.getContent();
            this.title = talent.getTitle();
            this.placeName = talent.getPlace().getPlaceName();
            this.teachingSubject = talent.getTeachingSubject().getSubjectName();
            this.teachedSubject = talent.getTeachedSubject().getSubjectName();
            this.gender = talent.getGender().toString();
            this.minAge = talent.getMinAge();
            this.maxAge = talent.getMaxAge();
            this.selectedDays = DayOfWeekUtil.convertSelectedDaysToString(talent.getDayOfWeek());
            this.regDate = talent.getRegDate();
            this.modDate = talent.getModDate();
            if (files != null && !files.isEmpty()) {
                for (File file : files) {
                    this.oriName.add(file.getOriName());
                    this.imgUrl.add(file.getFileUrl());
                }
            } else {
                this.oriName = null;
                this.imgUrl = null;
            }
            this.returnCode = returnCode;
            this.returnMessage = returnMessage;
        }

    }

    /**
     * 프로필 조회 시 응답 Dto
     */
    @Getter
    public static class WriterInfoResponse {
        private String id;
        private String gender;
        private String job;
        private String careerSkills;
        private String preferredSubject;
        private String mySubject;

        /* Entity -> Dto */
        public WriterInfoResponse(User user) {
            this.id = user.getId();
            if (user.getGender() != null) {
                this.gender = user.getGender().toString();
            } else {
                this.gender = null;
            }
            this.job = user.getJob();
            this.careerSkills = user.getCareerSkills();
            this.preferredSubject = user.getPreferredSubject();
            this.mySubject = user.getMySubject();
        }
    }

    /**
     * 게시물 조회 성공시 보낼 Dto
     */
    @Getter
    public static class TalentReadResponse {
        private Long id;
        private String writer;
        private String avatar;
        private String title;
        private String content;
        private String placeName;
        private String teachingSubject;
        private String parentTeachingSubject;
        private String teachedSubject;
        private String parentTeachedSubject;
        private Set<String> selectedDays;
        private String gender;
        private Long minAge;
        private Long maxAge;
        private LocalDateTime regDate;
        private LocalDateTime modDate;
        private List<String> imgUrl = new ArrayList<>();
        private Long hit;

        /* Entity -> Dto */
        public TalentReadResponse(Talent talent) {
//            if (talent.getWriter() == null) {
//                this.writer = "(알 수 없음)";
//            } else {
//                this.writer = talent.getWriter().getId();
//            }
            this.writer = talent.getWriter().getId();
            if (talent.getWriter() != null && talent.getWriter().getFile() != null) {
                this.avatar = talent.getWriter().getFile().getFileUrl();
            } else {
                this.avatar = null;
            }
            this.id = talent.getId();
            this.content = talent.getContent();
            this.title = talent.getTitle();
            this.placeName = talent.getPlace().getPlaceName();
            this.parentTeachingSubject = talent.getTeachingSubject().getParent().getSubjectName();
            this.teachingSubject = talent.getTeachingSubject().getSubjectName();
            this.teachedSubject = talent.getTeachedSubject().getSubjectName();
            this.parentTeachedSubject = talent.getTeachedSubject().getParent().getSubjectName();
            this.gender = talent.getGender().toString();
            this.minAge = talent.getMinAge();
            this.maxAge = talent.getMaxAge();
            this.selectedDays = DayOfWeekUtil.convertSelectedDaysToString(talent.getDayOfWeek());
            this.regDate = talent.getRegDate();
            this.modDate = talent.getModDate();
            this.hit = talent.getHit();

            if (!talent.getFiles().isEmpty()) {
                //자바8 람다식 + forEach 메서드
                talent.getFiles().forEach(file -> this.imgUrl.add(file.getFileUrl()));
            } else {
                this.imgUrl = null;
            }
        }
    }

    /**
     * 게시물 수정 시 요청된 Dto
     */
    @Getter
    @Builder
    public static class TalentUpdateRequest {

        @NotBlank(message = "작성자: 필수 정보입니다.")
        private String writer;

        @NotBlank(message = "제목: 필수 정보입니다.")
        private String title;

        @NotBlank(message = "내용: 필수 정보입니다.")
        private String content;

        @NotBlank(message = "장소: 필수 정보입니다.")
        private String placeName;

        @NotBlank(message = "가르쳐 줄 분야: 필수 정보입니다.")
        private String teachingSubject;

        @NotBlank(message = "가르침 받을 분야: 필수 정보입니다.")
        private String teachedSubject;

        @NotNull(message = "최소 연령대: 필수 정보입니다.")
        private Long minAge;

        @NotNull(message = "최대 연령대: 필수 정보입니다.")
        private Long maxAge;

        @NotEmpty(message = "요일: 최소 한 개 이상의 요일을 선택해야 합니다.")
        private Set<String> selectedDays = new HashSet<>();

        @NotBlank(message = "성: 필수 정보입니다.")
        private String gender;

        private List<String> imgUrl = new ArrayList<>();
    }

    /**
     * 게시물 수정 성공시 보낼 Dto
     */
    @Getter
    public static class TalentUpdateResponse {
        private Long id;
        private String writer;
        private String title;
        private String content;
        private String placeName;
        private String teachingSubject;
        private String teachedSubject;
        private Set<String> selectedDays;
        private String gender;
        private Long minAge;
        private Long maxAge;
        private LocalDateTime regDate;
        private LocalDateTime modDate;
        private List<String> oriName = new ArrayList<>();
        private List<String> imgUrl = new ArrayList<>();
        private int returnCode;
        private String returnMessage;

        /* Entity -> Dto */
        public TalentUpdateResponse(Talent talent, List<File> files, int returnCode, String returnMessage) {
            this.writer = talent.getWriter().getId();
            this.id = talent.getId();
            this.title = talent.getTitle();
            this.content = talent.getContent();
            this.placeName = talent.getPlace().getPlaceName();
            this.teachingSubject = talent.getTeachingSubject().getSubjectName();
            this.teachedSubject = talent.getTeachedSubject().getSubjectName();
            this.gender = talent.getGender().toString();
            this.minAge = talent.getMinAge();
            this.maxAge = talent.getMaxAge();
            this.selectedDays = DayOfWeekUtil.convertSelectedDaysToString(talent.getDayOfWeek());
            this.regDate = talent.getRegDate();
            this.modDate = talent.getModDate();
            if (files != null && !files.isEmpty()) {
                for (File file : files) {
                    this.oriName.add(file.getOriName());
                    this.imgUrl.add(file.getFileUrl());
                }
            } else {
                this.oriName = null;
                this.imgUrl = null;
            }
            this.returnCode = returnCode;
            this.returnMessage = returnMessage;
        }
    }


    /**
     * 응답코드, 응답메세지
     */
    @Getter
    @AllArgsConstructor
    public static class ResponseBasic {
        private int returnCode;
        private String returnMessage;
    }

    /**
     * 게시물 목록 응답 Dto
     */
    @Getter
    @AllArgsConstructor
    public static class TalentListResponse {
        private Long id;
        private String writer;
        private String title;
        private String content;
        private String placeName;
        private String teachingSubject;
        private String teachedSubject;
        private Long minAge;
        private Long maxAge;
        private String avatar;
        private LocalDateTime regDate;
        private Long hit;
        private Long commentCount; //댓글 개수 추가

        public TalentListResponse(Talent talent, Long commentCount) {
            this.id = talent.getId();
            this.writer = talent.getWriter().getId();
            this.content = talent.getContent();
            this.title = talent.getTitle();
            this.placeName = talent.getPlace().getPlaceName();
            this.teachingSubject = talent.getTeachingSubject().getSubjectName();
            this.teachedSubject = talent.getTeachedSubject().getSubjectName();
            this.minAge = talent.getMinAge();
            this.maxAge = talent.getMaxAge();
            this.avatar = talent.getWriter().getFile() != null ? talent.getWriter().getFile().getFileUrl() : null; // null 체크 추가
            this.regDate = talent.getRegDate();
            this.hit = talent.getHit();
            this.commentCount = commentCount;
        }
    }

    /**
     * 관련 게시물 목록 요청 Dto
     */
    @Getter
    public static class RelatedPostsRequest {
        private String subjectName;
    }

    /**
     * 관련 게시물 목록 응답 Dto
     */
    @Getter
    @AllArgsConstructor
    public static class RelatedPostsResponse {
        private Long id;
        private String title;
        private String content;
        private String placeName;
        private String teachingSubject;
        private String teachedSubject;
        private LocalDateTime regDate;

        public RelatedPostsResponse(Talent talent) {
            this.id = talent.getId();
            this.content = talent.getContent();
            this.title = talent.getTitle();
            this.placeName = talent.getPlace().getPlaceName();
            this.teachingSubject = talent.getTeachingSubject().getSubjectName();
            this.teachedSubject = talent.getTeachedSubject().getSubjectName();
            this.regDate = talent.getRegDate();
        }
    }

}
