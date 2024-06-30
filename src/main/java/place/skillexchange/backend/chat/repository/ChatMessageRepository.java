package place.skillexchange.backend.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import place.skillexchange.backend.chat.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Transactional(readOnly = true)
    Page<ChatMessage> findListsByRoomId(String roomId, Pageable pageable);

    void deleteByRoomIdIn(List<String> roomIds); //리스트에 포함된 모든 ChatMessage 삭제
}
