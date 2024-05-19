package place.skillexchange.backend.exception.board;

import place.skillexchange.backend.exception.AllCodeException;

public class BoardNumNotFoundException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new BoardNumNotFoundException();

    private BoardNumNotFoundException() {
        super(BoardErrorCode.BOARD_NUM_NOT_FOUND);
    }

}
