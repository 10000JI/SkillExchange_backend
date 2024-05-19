package place.skillexchange.backend.exception.board;

import place.skillexchange.backend.exception.AllCodeException;

public class PlaceNotFoundException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new PlaceNotFoundException();

    private PlaceNotFoundException() {
        super(BoardErrorCode.PLACE_NOT_FOUND);
    }

}
