package place.skillexchange.backend.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import place.skillexchange.backend.chat.dto.ChatDto;
import place.skillexchange.backend.chat.entity.ChatMessage;
import place.skillexchange.backend.chat.entity.ChatRoom;
import place.skillexchange.backend.chat.repository.ChatMessageRepository;
import place.skillexchange.backend.chat.repository.ChatRoomRepository;
import place.skillexchange.backend.common.util.SecurityUtil;
import place.skillexchange.backend.exception.chat.ChatRoomAccessDeniedException;
import place.skillexchange.backend.exception.chat.ChatRoomNotFoundException;
import place.skillexchange.backend.exception.user.UserNotFoundException;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SecurityUtil securityUtil;

    @Override // 개인 DM방 생성
    public ChatDto.CreateChatRoomResponse createChatRoomForPersonal(ChatDto.CreateChatRoomRequest chatRoomRequest) {
        String id = securityUtil.getCurrentMemberUsername(); //id=roomMakerId 같아야 함
        if (!id.equals(chatRoomRequest.getRoomMakerId())) {
            throw UserNotFoundException.EXCEPTION;
        }
        User roomMaker = userRepository.findById(id).orElseThrow(() -> UserNotFoundException.EXCEPTION);
        User guest = userRepository.findById(chatRoomRequest.getGuestId()).orElseThrow(() -> UserNotFoundException.EXCEPTION);

        ChatRoom newRoom  = ChatRoom.create();
        newRoom.setCreatedAt(LocalDateTime.now());

        newRoom.addMembers(roomMaker, guest);

        chatRoomRepository.save(newRoom);

        return new ChatDto.CreateChatRoomResponse(roomMaker.getId(),guest.getId(), newRoom.getId());
    }

    @Override // 15개씩 채팅내역 보내주기
    public ChatDto.ChatRoomInfoResponse chatRoomInfo(String roomId, int page, int size) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> ChatRoomNotFoundException.EXCEPTION);
        ChatDto.ChatRoomInfoResponse chatRoomInfoResponse = new ChatDto.ChatRoomInfoResponse(chatRoom);

        //액세스할 수 있는 사용자인지 확인
        Set<ChatDto.ChatUserInfoDto> chatRoomMembers = chatRoomInfoResponse.getChatRoomMembers();
        if (!chatRoomMembers.contains(new ChatDto.ChatUserInfoDto(userRepository.findById(securityUtil.getCurrentMemberUsername()).get()))) {
            throw ChatRoomAccessDeniedException.EXCEPTION;
        }

        List<ChatMessage> lastestChatMessages = findChatMessagesWithPaging(page, size, roomId);
        List<ChatDto.ChatMessageInfo> chatMessageInfos = new ArrayList<>();

        for (ChatMessage chatMessage : lastestChatMessages) {
            chatMessageInfos.add(new ChatDto.ChatMessageInfo(chatMessage));
        }

        chatRoomInfoResponse.setLatestChatMessages(chatMessageInfos);
        return chatRoomInfoResponse;
    }

    @Override
    public ChatRoom findById(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId).orElseThrow();
    }

    public List<ChatMessage> findChatMessagesWithPaging(int page, int size, String roomId) {

        Sort sort = Sort.by("createdAt").descending();
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);

        List<ChatMessage> result = chatMessageRepository.findListsByRoomId(roomId, pageRequest).getContent();

        return result;
    }
}
