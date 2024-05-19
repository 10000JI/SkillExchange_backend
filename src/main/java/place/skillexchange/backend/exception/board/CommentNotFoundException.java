package place.skillexchange.backend.exception.board;

import place.skillexchange.backend.exception.AllCodeException;

public class CommentNotFoundException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new CommentNotFoundException();

    private CommentNotFoundException() {
        super(BoardErrorCode.COMMENT_NOT_FOUND);
    }

}
