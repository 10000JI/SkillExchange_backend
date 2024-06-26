package place.skillexchange.backend.exception.board;

import place.skillexchange.backend.exception.AllCodeException;

public class BoardAleadyRequestSkillException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new BoardAleadyRequestSkillException();

    private BoardAleadyRequestSkillException() {
        super(BoardErrorCode.ALREADY_REQUESTSKILL);
    }

}
