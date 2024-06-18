package place.skillexchange.backend.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import place.skillexchange.backend.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    @Transactional(readOnly = true)
    Page<ChatRoom> findByChatRoomMembersId(String userId, Pageable pageable);
}
