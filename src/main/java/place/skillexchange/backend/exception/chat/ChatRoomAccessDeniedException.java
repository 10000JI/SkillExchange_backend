package place.skillexchange.backend.exception.chat;

import place.skillexchange.backend.exception.AllCodeException;

public class ChatRoomAccessDeniedException extends AllCodeException {

    public static final AllCodeException EXCEPTION = new ChatRoomAccessDeniedException();

    private ChatRoomAccessDeniedException() {
        super(ChatErrorCode.CHATROOM_ACCESS_DENIED);
    }

}
