package place.skillexchange.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import place.skillexchange.backend.common.annotation.ExplainError;
import place.skillexchange.backend.common.dto.ErrorReason;

import java.lang.reflect.Field;
import java.util.Objects;

import static place.skillexchange.backend.common.consts.ConstFields.*;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements BaseErrorCode {
    @ExplainError("500번대 알수없는 오류입니다. 서버 관리자에게 문의 주세요")
    INTERNAL_SERVER_ERROR(INTERNAL_SERVER, "GLOBAL_500_1", "서버 오류. 관리자에게 문의 부탁드립니다.");
    

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
