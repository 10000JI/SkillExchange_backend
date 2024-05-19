package place.skillexchange.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import place.skillexchange.backend.common.dto.ErrorReason;

@Getter
@AllArgsConstructor
public class AllCodeException extends RuntimeException {
    private BaseErrorCode errorCode;

    public ErrorReason getErrorReason() {
        return this.errorCode.getErrorReason();
    }
}
