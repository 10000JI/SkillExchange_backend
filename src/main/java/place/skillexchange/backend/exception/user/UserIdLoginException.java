package place.skillexchange.backend.exception.user;

import place.skillexchange.backend.exception.AllCodeException;

public class UserIdLoginException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new UserIdLoginException();

    private UserIdLoginException() {
        super(UserErrorCode.USER_LOGIN_INVALID);
    }
}
