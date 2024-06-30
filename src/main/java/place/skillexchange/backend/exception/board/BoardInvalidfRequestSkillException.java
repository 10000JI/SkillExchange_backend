package place.skillexchange.backend.exception.board;

import place.skillexchange.backend.exception.AllCodeException;

public class BoardInvalidfRequestSkillException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new BoardInvalidfRequestSkillException();

    private BoardInvalidfRequestSkillException() {
        super(BoardErrorCode.INVALID_REQUEST_SKILL);
    }

}
