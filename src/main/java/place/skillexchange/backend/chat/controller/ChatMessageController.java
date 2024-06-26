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
        try {
            ChatMessage newChat = chatMessageService.createChatMessage(message);
            if (newChat != null) {
                template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + message.getRoomId(), newChat);
                log.info("Message sent to RabbitMQ: {}", newChat);
            } else {
                log.error("Failed to create chat message. User might not be in the chat room. User: {}, Room: {}",
                        message.getAuthorId(), message.getRoomId());
            }
        } catch (Exception e) {
            log.error("Error processing message: ", e);
        }
    }
}