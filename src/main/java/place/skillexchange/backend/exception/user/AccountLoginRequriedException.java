package place.skillexchange.backend.exception.user;

import place.skillexchange.backend.exception.AllCodeException;

public class AccountLoginRequriedException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new AccountLoginRequriedException();

    private AccountLoginRequriedException() {
        super(UserErrorCode.ACCOUNT_LOGIN_REQUIRED);
    }
}

