package place.skillexchange.backend.chat.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import place.skillexchange.backend.chat.entity.ChatMessage;
import place.skillexchange.backend.chat.entity.ChatRoom;
import place.skillexchange.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ChatDto {

    /**
     * 채팅방 개설 요청 dto
     */
    @Getter
    public static class CreateChatRoomRequest {
        private String roomMakerId;
        private String guestId;
    }

    /**
     * 채팅방 개설 성공시 응답 dto
     */
    @Getter
    public static class CreateChatRoomResponse {
        private String roomMakerId;
        private String guestId;
        private String chatRoomId;

        /* Entity -> Dto */
        public CreateChatRoomResponse(String roomMakerId, String guestId, String chatRoomId) {
            this.roomMakerId = roomMakerId;
            this.guestId = guestId;
            this.chatRoomId = chatRoomId;
        }
    }

    /**
     * 채팅방 메세지 요청 dto
     */
    @Getter
    public static class ChatRoomInfoRequest {
        private String roomId;
    }

    /**
     * 채팅방 메세지 요청 성공시 응답 dto
     */
    @Data
    public static class ChatRoomInfoResponse {
        private String chatRoomId;
        private ChatMessage lastChatMesg;
        private Set<ChatUserInfoDto> chatRoomMembers;
        private List<ChatMessageInfo> latestChatMessages;
        private LocalDateTime createdAt;

        public ChatRoomInfoResponse(ChatRoom chatRoom) {
            this.chatRoomId = chatRoom.getId();
            this.lastChatMesg = chatRoom.getLastChatMesg();
            this.chatRoomMembers = new HashSet<>(); // Initialize the Set
            for (User user : chatRoom.getChatRoomMembers()) {
                //log.error("userInfo:::{}", user);
                chatRoomMembers.add(new ChatUserInfoDto(user));
            }
            this.createdAt = chatRoom.getCreatedAt();
        }
    }

    @Data
    public static class ChatMessageInfo {
        private long chatMessageId;
        private String authorId;
        private String message;
        private LocalDateTime createdAt;

        public ChatMessageInfo(ChatMessage chatMessage) {
            this.chatMessageId = chatMessage.getId();
            this.authorId = chatMessage.getAuthorId();
            this.message = chatMessage.getMessage();
            this.createdAt = chatMessage.getCreatedAt();
        }
    }

    @Data
    public static class ChatUserInfoDto {
        private String userId;
        private String profileImage;

        public ChatUserInfoDto(User user) {
            this.userId = user.getId();
            if (user.getFile() == null) {
                this.profileImage = null;
            } else {
                this.profileImage = user.getFile().getFileUrl();
            }
        }
    }

    /**
     * 웹소켓 접속시 요청 Dto
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageDto {
        private String roomId;
        private String authorId;
        private String message;

        /* Dto -> Entity */
        public ChatMessage toEntity() {
            ChatMessage chatMessage = ChatMessage.builder()
                    .roomId(roomId)
                    .authorId(authorId)
                    .message(message)
                    .createdAt(LocalDateTime.now())
                    .build();
            return chatMessage;
        }
    }

    /**
     * 채팅방 목록 요청 성공시 응답 dto
     */
    @Data
    @Builder
    public static class ChatRoomListReponse {
        private int page;
        private int count;
        private String reqUserId;
        private List<ChatRoomList> chatRooms;
    }

    @Data
    public static class ChatRoomList {
        private String chatRoomId;
        private ChatMessage lastChatMesg;
        private String guestId;

        public ChatRoomList(ChatRoom chatRoom, String userId) {
            this.chatRoomId = chatRoom.getId();
            this.lastChatMesg = chatRoom.getLastChatMesg();
            for (User user : chatRoom.getChatRoomMembers()) {
                if(!user.getId().equals(userId)){
                    //log.error("userInfo:::{}", user);
                    this.guestId = userId;
                }
            }
        }
    }
}
