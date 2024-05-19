package place.skillexchange.backend.exception.board;

import place.skillexchange.backend.exception.AllCodeException;

public class BoardAleadyScrappedException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new BoardAleadyScrappedException();

    private BoardAleadyScrappedException() {
        super(BoardErrorCode.ALREADY_SCRAPPED);
    }

}
