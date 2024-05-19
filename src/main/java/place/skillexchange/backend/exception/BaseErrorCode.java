package place.skillexchange.backend.exception;

import place.skillexchange.backend.common.dto.ErrorReason;

public interface BaseErrorCode {
    public ErrorReason getErrorReason();
    String getExplainError() throws NoSuchFieldException;
}
