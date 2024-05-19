package place.skillexchange.backend.exception.board;

import place.skillexchange.backend.exception.AllCodeException;

public class SubjectCategoryNotFoundException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new SubjectCategoryNotFoundException();

    private SubjectCategoryNotFoundException() {
        super(BoardErrorCode.SUBJECT_CATEGORY_NOT_FOUND);
    }

}
