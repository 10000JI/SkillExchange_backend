package place.skillexchange.backend.chat.service;

import place.skillexchange.backend.chat.dto.ChatDto;
import place.skillexchange.backend.chat.entity.ChatRoom;

public interface ChatRoomService {

    public ChatDto.CreateChatRoomResponse createChatRoomForPersonal(ChatDto.CreateChatRoomRequest chatRoomRequest);

    public ChatDto.ChatRoomInfoResponse chatRoomInfo(String roomId);

    public ChatRoom findById(String chatRoomId);
}
