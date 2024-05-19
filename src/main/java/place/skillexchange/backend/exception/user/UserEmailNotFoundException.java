package place.skillexchange.backend.exception.user;

import place.skillexchange.backend.common.annotation.ApiErrorCodeExample;
import place.skillexchange.backend.common.dto.ErrorReason;
import place.skillexchange.backend.exception.AllCodeException;

public class UserEmailNotFoundException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new UserEmailNotFoundException();

    private UserEmailNotFoundException() {
        super(UserErrorCode.USER_EMAIL_NOT_FOUND);
    }

}
