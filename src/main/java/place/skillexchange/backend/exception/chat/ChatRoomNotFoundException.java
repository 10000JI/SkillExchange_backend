package place.skillexchange.backend.exception.chat;

import place.skillexchange.backend.exception.AllCodeException;
import place.skillexchange.backend.exception.board.BoardErrorCode;

public class ChatRoomNotFoundException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new ChatRoomNotFoundException();

    private ChatRoomNotFoundException() {
        super(ChatErrorCode.CHATROOM_NOT_FOUND);
    }

}
