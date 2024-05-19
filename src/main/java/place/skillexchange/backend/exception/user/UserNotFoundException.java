package place.skillexchange.backend.exception.user;

import place.skillexchange.backend.common.annotation.ApiErrorCodeExample;
import place.skillexchange.backend.common.dto.ErrorReason;
import place.skillexchange.backend.exception.AllCodeException;

public class UserNotFoundException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new UserNotFoundException();

    private UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }

    @ApiErrorCodeExample(UserErrorCode.class)
    public ErrorReason getErrorReason() {
        return super.getErrorReason();
    }
}
