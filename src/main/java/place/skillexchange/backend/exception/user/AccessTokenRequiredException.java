package place.skillexchange.backend.exception.user;

import place.skillexchange.backend.exception.AllCodeException;

//RunTimeException(500번) 예외 클래스 상속받아서 생성
public class AccessTokenRequiredException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new AccessTokenRequiredException();

    private AccessTokenRequiredException() {
        super(UserErrorCode.ACCESSTOKEN_REQUIRED);
    }
}
