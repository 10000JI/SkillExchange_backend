package place.skillexchange.backend.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import place.skillexchange.backend.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
}
