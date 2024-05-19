package place.skillexchange.backend.exception.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import place.skillexchange.backend.common.annotation.ExplainError;
import place.skillexchange.backend.common.dto.ErrorReason;
import place.skillexchange.backend.exception.BaseErrorCode;

import java.lang.reflect.Field;
import java.util.Objects;

import static place.skillexchange.backend.common.consts.ConstFields.*;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

//    @ExplainError("회원가입시에 유효성 검사 실시할 때 발생하는 오류")
//    PASSWORD_NOT_EQUAL(BAD_REQUEST, "USER_400_1", "비밀번호와 비밀번호 확인이 일치하지 않습니다."),
//    ID_ALREADY_EXISTS(BAD_REQUEST, "USER_400_2", "이미 존재하는 아이디입니다."),
//    EMAIL_ALREADY_EXISTS(BAD_REQUEST, "USER_400_3", "이미 존재하는 이메일입니다."),
//    ID_CANNOT_BE_MODIFIED(BAD_REQUEST, "USER_400_4", "아이디는 임의로 조정할 수 없습니다."),
//    NOW_PASSWORD_NOT_EQUAL(BAD_REQUEST, "USER_400_5", "현재 비밀번호를 정확히 입력해주세요."),
//    NEW_PASSWORD_NOT_EQUAL(BAD_REQUEST, "USER_400_6", "새 비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    @ExplainError("인증 및 권한이 없는 경우")
    USER_TOKEN_EXPIRED(UNAUTHORIZED, "USER_401_1", "토큰이 만료 되었습니다."),
    REFRESHTOKEN_NOT_FOUND(UNAUTHORIZED, "USER_401_2", "Refresh Token을 찾을 수 없습니다."),
    REFRESHTOKEN_EXPIRED(UNAUTHORIZED, "USER_401_3", "Refresh Token이 만료되었습니다."),
    USER_ACCESS_DENIED(FORBIDDEN, "USER_403_1", "접근이 거부되었습니다."),
    @ExplainError("사용자 정보를 찾을 수 없는 경우")
    USER_NOT_FOUND(BAD_REQUEST, "USER_400_1", "사용자 정보를 찾을 수 없습니다."),
    USER_LOGIN_INVALID(BAD_REQUEST, "USER_400_2", "일치하는 로그인 정보가 없습니다."),
    EMAIL_SEND_FAILURE(BAD_REQUEST, "USER_400_3", "이메일 전송 중 문제가 생겼습니다."),
    WRITER_LOGGEDINUSER_INVALID(INTERNAL_SERVER, "USER_500_1", "로그인한 회원 정보와 글쓴이가 다릅니다."),
    ACCOUNT_LOGIN_REQUIRED(NOT_FOUND,"USER_404_1", "계정에 다시 로그인 해야 합니다."),
    USER_EMAIL_NOT_FOUND(NOT_FOUND, "USER_404_2", "등록된 계정 중 없는 이메일 주소 입니다.");
    

    private Integer status;
    private String code;
    private String reason;

    public ErrorReason getErrorReason() {
        return ErrorReason.builder().message(reason).code(code).status(status).build();
    }

    @Override
    public String getExplainError() throws NoSuchFieldException {
        Field field = this.getClass().getField(this.name());
        ExplainError annotation = field.getAnnotation(ExplainError.class);
        return Objects.nonNull(annotation) ? annotation.value() : this.getReason();
    }
}
