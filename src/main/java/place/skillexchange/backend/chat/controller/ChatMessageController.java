package place.skillexchange.backend.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import place.skillexchange.backend.chat.dto.ChatDto;
import place.skillexchange.backend.chat.entity.ChatMessage;
import place.skillexchange.backend.chat.service.ChatMessageService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";

    private final ChatMessageService chatMessageService;
    private final RabbitTemplate template;

    @MessageMapping("chat.message")
    public void sendMessage(ChatDto.ChatMessageDto message) {
        log.info("Message content: {}", message);

        ChatMessage newChat = chatMessageService.createChatMessage(message);

        template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + message.getRoomId(), newChat);

        log.info("Message sent to RabbitMQ: {}", newChat);
    }
}