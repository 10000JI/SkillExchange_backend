package place.skillexchange.backend.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import place.skillexchange.backend.chat.dto.ChatDto;
import place.skillexchange.backend.chat.entity.ChatMessage;
import place.skillexchange.backend.chat.entity.ChatRoom;
import place.skillexchange.backend.chat.service.ChatMessageService;
import place.skillexchange.backend.chat.service.ChatRoomService;
import place.skillexchange.backend.user.entity.User;

import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/message")
    public void sendMessage(ChatDto.ChatMessageDto message) {
        // 실시간으로 방에서 채팅하기
        ChatMessage newChat = chatMessageService.createChatMessage(message);
        log.info("received message: {}", message);

//        ChatRoom room = chatRoomService.findById(message.getRoomId());
//        User friend = room.getChatRoomMembers().stream()
//                .filter(m -> m.getId() != message.getAuthorId())
//                .collect(Collectors.toList())
//                .get(0);
//
//        // 메시지를 친 사람에게 전송
//        messagingTemplate.convertAndSendToUser(
//                message.getAuthorId().toString(), "/sub/reply", newChat);
//
//        // 상대방에게 전송
//        messagingTemplate.convertAndSendToUser(
//                friend.getId().toString(), "/sub/reply", newChat);

        // 방에 있는 모든 사용자에게 메시지 전송
        messagingTemplate.convertAndSend("/sub/channel/"+message.getRoomId(), newChat);
    }
}