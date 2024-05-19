package place.skillexchange.backend.exception.board;

import place.skillexchange.backend.exception.AllCodeException;

public class CannotConvertNestedStructureException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new CannotConvertNestedStructureException();

    private CannotConvertNestedStructureException() {
        super(BoardErrorCode.NESTED_STRUCTURE_CONVERSION_FAILURE);
    }
}
