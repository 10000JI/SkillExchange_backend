package place.skillexchange.backend.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import place.skillexchange.backend.chat.dto.ChatDto;
import place.skillexchange.backend.chat.service.ChatRoomService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chatRoom/")
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping("/personal") //개인 DM 채팅방 생성
    public ChatDto.CreateChatRoomResponse createPersonalChatRoom(@RequestBody ChatDto.CreateChatRoomRequest request) {
        return chatRoomService.createChatRoomForPersonal(request);
    }

    @GetMapping("/message") //채팅방 메세지 목록 요청 (최신 15개씩, 무한스크롤 구현)
    public ChatDto.ChatRoomInfoResponse chatRoomInfo(/*@RequestBody ChatDto.ChatRoomInfoRequest request,*/
                                                     @RequestParam int page, @RequestParam int size, @RequestParam String roomId) {
        return chatRoomService.chatRoomInfo(roomId, page, size);
    }

    @GetMapping("/list") //채팅방 목록 (최신 7개씩, 무한스크롤 구현)
    public ChatDto.ChatRoomListReponse getChatRoomList(@RequestParam int page, @RequestParam int size) {
        return chatRoomService.getChatRoomList(page,size);
    }
}
