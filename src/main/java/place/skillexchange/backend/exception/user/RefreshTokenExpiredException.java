package place.skillexchange.backend.exception.user;

import place.skillexchange.backend.exception.AllCodeException;

public class RefreshTokenExpiredException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new RefreshTokenExpiredException();

    private RefreshTokenExpiredException() {
        super(UserErrorCode.REFRESHTOKEN_EXPIRED);
    }
}
