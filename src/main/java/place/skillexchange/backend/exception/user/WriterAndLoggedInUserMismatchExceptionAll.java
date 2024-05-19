package place.skillexchange.backend.exception.user;

import place.skillexchange.backend.exception.AllCodeException;

public class WriterAndLoggedInUserMismatchExceptionAll extends AllCodeException {

    public static final AllCodeException EXCEPTION = new WriterAndLoggedInUserMismatchExceptionAll();

    private WriterAndLoggedInUserMismatchExceptionAll() {
        super(UserErrorCode.WRITER_LOGGEDINUSER_INVALID);
    }
}
