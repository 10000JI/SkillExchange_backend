package place.skillexchange.backend.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import place.skillexchange.backend.chat.dto.ChatDto;
import place.skillexchange.backend.chat.entity.ChatMessage;
import place.skillexchange.backend.chat.entity.ChatRoom;
import place.skillexchange.backend.chat.repository.ChatRoomRepository;
import place.skillexchange.backend.exception.user.UserNotFoundException;
import place.skillexchange.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService{

    private final ChatRoomRepository chatRoomRepository;

    @Override
    public ChatMessage createChatMessage(ChatDto.ChatMessageDto chatMessageDto) {

        ChatMessage chatMessage = chatMessageDto.toEntity();
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getRoomId()).orElseThrow();

        boolean exists = chatRoom.getChatRoomMembers().stream()
                .anyMatch(member -> member.getUsername().equals(chatMessageDto.getAuthorId()));

        if (!exists) {
            throw UserNotFoundException.EXCEPTION;
        }

        chatRoom.setLastChatMesg(chatMessage);
        chatRoomRepository.save(chatRoom);

        return chatMessage;
    }
}
