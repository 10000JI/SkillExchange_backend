package place.skillexchange.backend.exception.user;

import place.skillexchange.backend.exception.AllCodeException;

public class RefreshTokenNotFoundException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new RefreshTokenNotFoundException();

    private RefreshTokenNotFoundException() {
        super(UserErrorCode.REFRESHTOKEN_NOT_FOUND);
    }
}
