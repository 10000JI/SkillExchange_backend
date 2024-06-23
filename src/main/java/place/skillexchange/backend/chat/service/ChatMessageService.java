package place.skillexchange.backend.chat.service;

import place.skillexchange.backend.chat.dto.ChatDto;
import place.skillexchange.backend.chat.entity.ChatMessage;

public interface ChatMessageService {
    public ChatMessage createChatMessage(ChatDto.ChatMessageDto chatMessageDto, String userId);
}
