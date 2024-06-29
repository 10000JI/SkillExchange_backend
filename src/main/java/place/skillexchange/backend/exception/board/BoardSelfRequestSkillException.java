package place.skillexchange.backend.exception.board;

import place.skillexchange.backend.exception.AllCodeException;

public class BoardSelfRequestSkillException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new BoardSelfRequestSkillException();

    private BoardSelfRequestSkillException() {
        super(BoardErrorCode.SELF_REQUESTSKILL);
    }

}
