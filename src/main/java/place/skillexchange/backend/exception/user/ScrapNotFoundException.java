package place.skillexchange.backend.exception.user;

import place.skillexchange.backend.exception.AllCodeException;

public class ScrapNotFoundException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new ScrapNotFoundException();

    private ScrapNotFoundException() {
        super(UserErrorCode.SCRAP_NOT_FOUND);
    }
}
