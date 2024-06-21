package place.skillexchange.backend.exception.user;

import place.skillexchange.backend.exception.AllCodeException;

public class SocialLoginRequriedException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new SocialLoginRequriedException();

    private SocialLoginRequriedException() {
        super(UserErrorCode.SOCIAL_LOGIN_REQUIRED);
    }
}

