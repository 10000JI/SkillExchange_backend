package place.skillexchange.backend.exception.board;

import place.skillexchange.backend.exception.AllCodeException;

public class SubjectCategoryBadRequestException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new SubjectCategoryBadRequestException();

    private SubjectCategoryBadRequestException() {
        super(BoardErrorCode.SUBJECT_CATEGORY_BAD_REQUEST);
    }

}
