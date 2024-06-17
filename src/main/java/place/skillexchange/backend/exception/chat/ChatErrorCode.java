package place.skillexchange.backend.exception.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import place.skillexchange.backend.common.annotation.ExplainError;
import place.skillexchange.backend.common.dto.ErrorReason;
import place.skillexchange.backend.exception.BaseErrorCode;

import java.lang.reflect.Field;
import java.util.Objects;

import static place.skillexchange.backend.common.consts.ConstFields.FORBIDDEN;
import static place.skillexchange.backend.common.consts.ConstFields.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements BaseErrorCode {

    CHATROOM_NOT_FOUND(NOT_FOUND, "CHATROOM_404_1", "존재하지 않는 채팅방 입니다."),
    CHATROOM_ACCESS_DENIED(FORBIDDEN, "CHATROOM_403_1", "이 대화방에 액세스할 수 있는 권한이 없습니다.");

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
