package place.skillexchange.backend.exception.board;

import place.skillexchange.backend.exception.AllCodeException;

public class BoardNotFoundException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new BoardNotFoundException();

    private BoardNotFoundException() {
        super(BoardErrorCode.BOARD_NOT_FOUND);
    }

}
