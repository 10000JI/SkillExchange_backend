package place.skillexchange.backend.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.bind.annotation.BindParam;
import place.skillexchange.backend.talent.entity.Talent;
import place.skillexchange.backend.user.entity.Authority;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.user.entity.User;

import java.util.Collections;

public class UserDto {

    /**
     * 회원가입 시 요청된 Dto
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "회원가입 요청을 위한 도메인 객체")
    public static class SignUpRequest {

        @NotBlank(message = "아이디: 필수 정보입니다.")
        @Size(min = 5 , message="id는 5글자 이상 입력해 주세요.")
        @Schema(title = "사용자 ID",description = "사용자 ID를 입력합니다.")
        private String id;

        @NotBlank(message = "이메일: 필수 정보입니다.")
        @Email(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
        @Schema(title = "사용자 이메일",description = "사용자 이메일을 입력합니다.")
        private String email;

        @NotBlank(message = "비밀번호: 필수 정보입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        @Schema(title = "사용자 비밀번호",description = "사용자 비밀번호를 입력합니다.")
        private String password;

        @NotBlank(message = "비밀번호 확인: 필수 정보입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        @Schema(title = "사용자 비밀번호 확인",description = "사용자 비밀번호를 재입력합니다.")
        private String passwordCheck;

        //Authority 객체를 생성하고, 권한 이름을 "ROLE_USER"로 설정
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        /* Dto -> Entity */
        //toEntity는 `패스`워드 확인 일치하면 사용
        public User toEntity() {
            User user = User.builder()
                    .id(id)
                    .email(email)
                    .password(password)
                    .authorities(Collections.singleton(authority))
                    .build();
            return user;
        }
    }

    /**
     * 로그인시 요청된 Dto
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "로그인 요청을 위한 도메인 객체")
    @Builder
    public static class SignInRequest {
        @Schema(title = "사용자 ID",description = "사용자 ID를 입력합니다.")
        private String id;
        @Schema(title = "사용자 비밀번호",description = "사용자 비밀번호를 입력합니다.")
        private String password;
    }

    /**
     * 회원가입, 로그인 성공시 보낼 Dto
     */
    @Getter
    @Schema(description ="회원가입 응답을 위한 도메인 객체")
    public static class SignUpInResponse {

        @Schema(title = "사용자 ID",description = "사용자 ID를 반환합니다.")
        private String id;
        @Schema(title = "사용자 이메일",description = "사용자 이메일를 반환합니다.")
        private String email;
        @Schema(title = "HTTP 상태 코드")
        private int returnCode;
        @Schema(title = "응답 메시지")
        private String returnMessage;

        /* Entity -> Dto */
        public SignUpInResponse(User user, int returnCode, String returnMessage) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.returnCode = returnCode;
            this.returnMessage = returnMessage;
        }
    }
    /**
     * 응답코드, 응답메세지
     */
    @Getter
    @AllArgsConstructor
    @Schema(description = "상태 코드 및 메세지")
    public static class ResponseBasic {
        @Schema(title = "HTTP 상태 코드")
        private int returnCode;
        @Schema(title = "응답 메시지")
        private String returnMessage;
    }

    /**
     * 로그인 성공시 요청된 Dto
     */
    @Getter
    @Schema(description = "이메일로 아이디 혹은 비밀번호 찾기")
    public static class EmailRequest {
        private String email;
    }

    /**
     * 프로필 수정 시 요청된 Dto
     */
    @Getter
    @Schema(description = "프로필 수정 요청을 위한 도메인 객체")
    @Builder
    public static class ProfileRequest {
        @Schema(title = "사용자 성별",description = "사용자 성별을 입력합니다.")
        private String gender;
        @Schema(title = "사용자 직업",description = "사용자 직업을 입력합니다.")
        private String job;
        @Schema(title = "사용자 경력기술",description = "사용자 경력기술을 입력합니다.")
        private String careerSkills;
        @Schema(title = "사용자 관심분야",description = "사용자 관심분야을 입력합니다.")
        private String preferredSubject;
        @Schema(title = "사용자 전공분야",description = "사용자 전공분야를 입력합니다.")
        private String mySubject;
    }

    /**
     * 프로필 수정 시 응답 Dto
     */
    @Getter
    @Schema(description = "프로필 수정 응답을 위한 도메인 객체")
    public static class ProfileResponse {
        @Schema(title = "사용자 아이디",description = "사용자 아이디을 반환합니다.")
        private String id;
        @Schema(title = "사용자 이메일",description = "사용자 성별을 반환합니다.")
        private String email;
        @Schema(title = "사용자 성별",description = "사용자 성별을 반환합니다.")
        private String gender;
        @Schema(title = "사용자 직업",description = "사용자 직업을 반환합니다.")
        private String job;
        @Schema(title = "사용자 경력기술",description = "사용자 경력기술을 반환합니다.")
        private String careerSkills;
        @Schema(title = "사용자 관심분야",description = "사용자 관심분야을 반환합니다.")
        private String preferredSubject;
        @Schema(title = "사용자 전공분야",description = "사용자 전공분야를 반환합니다.")
        private String mySubject;
        @Schema(title = "이미지명",description = "이미지명을 반환합니다.")
        private String oriName;
        @Schema(title = "이미지주소",description = "이미지주소를 반환합니다.")
        private String imgUrl;
        @Schema(title = "HTTP 상태 코드")
        private int returnCode;
        @Schema(title = "응답 메시지")
        private String returnMessage;

        /* Entity -> Dto */
        public ProfileResponse(User user, File file, int returnCode, String returnMessage) {
            this.id = user.getId();
            this.email = user.getEmail();
            if (user.getGender() != null) {
                this.gender = user.getGender().toString();
            } else {
                this.gender = null;
            }
            this.job = user.getJob();
            this.careerSkills = user.getCareerSkills();
            this.preferredSubject = user.getPreferredSubject();
            this.mySubject = user.getMySubject();
            if (file != null) {
                this.oriName = file.getOriName();
                this.imgUrl = file.getFileUrl();
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
    @Schema(description = "프로필 조회 응답을 위한 도메인 객체")
    public static class MyProfileResponse{
        @Schema(title = "사용자 아이디",description = "사용자 아이디을 반환합니다.")
        private String id;
        @Schema(title = "사용자 이메일",description = "사용자 이메일을 반환합니다.")
        private String email;
        @Schema(title = "사용자 성별",description = "사용자 성별을 반환합니다.")
        private String gender;
        @Schema(title = "사용자 직업",description = "사용자 직업을 반환합니다.")
        private String job;
        @Schema(title = "사용자 경력기술",description = "사용자 경력기술을 반환합니다.")
        private String careerSkills;
        @Schema(title = "사용자 관심분야",description = "사용자 관심분야을 반환합니다.")
        private String preferredSubject;
        @Schema(title = "사용자 전공분야",description = "사용자 전공분야를 반환합니다.")
        private String mySubject;
        @Schema(title = "이미지주소",description = "이미지주소를 반환합니다.")
        private String imgUrl;
        @Schema(title = "HTTP 상태 코드")
        private int returnCode;
        @Schema(title = "응답 메시지")
        private String returnMessage;

        /* Entity -> Dto */
        public MyProfileResponse(User user, int returnCode, String returnMessage) {
            this.id = user.getId();
            this.email = user.getEmail();
            if (user.getGender() != null) {
                this.gender = user.getGender().toString();
            } else {
                this.gender = null;
            }
            this.job = user.getJob();
            this.careerSkills = user.getCareerSkills();
            this.preferredSubject = user.getPreferredSubject();
            this.mySubject = user.getMySubject();
            if (user.getFile() != null) {
                this.imgUrl = user.getFile().getFileUrl();
            }else{
                this.imgUrl = null;
            }
            this.returnCode = returnCode;
            this.returnMessage = returnMessage;
        }
    }

    /**
     * 비밀번호 변경 시 요청된 Dto
     */
    @Getter
    @Schema(description = "비밀번호 변경 요청을 위한 도메인 객체")
    @AllArgsConstructor
    public static class UpdatePwRequest {
        @NotBlank(message = "현재 비밀번호: 필수 정보입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        @Schema(title = "사용자 현재 비밀번호",description = "사용자 현재 비밀번호를 반환합니다.")
        private String password;

        @NotBlank(message = "새 비밀번호: 필수 정보입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        @Schema(title = "사용자 새 비밀번호",description = "사용자 새 비밀번호를 반환합니다.")
        private String newPassword;

        @NotBlank(message = "새 비밀번호 확인: 필수 정보입니다.")
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        @Schema(title = "사용자 새 비밀번호 확인",description = "사용자 새 비밀번호를 반환합니다.")
        private String newPasswordCheck;
    }

    /**
     * 내가 스크랩한 게시물 목록 확인 시 응답 Dto
     */
    @Getter
    public static class MyScrapResponse{
        private Long id;
        private String writer;
        private String title;
        private String content;

        /* Entity -> Dto */
        public MyScrapResponse(Talent talent) {
            this.writer = talent.getWriter().getId();
            this.id = talent.getId();
            this.content = talent.getContent();
            this.title = talent.getTitle();
        }
    }
}
