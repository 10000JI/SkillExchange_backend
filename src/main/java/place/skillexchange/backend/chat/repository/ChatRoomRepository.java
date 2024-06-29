package place.skillexchange.backend.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import place.skillexchange.backend.chat.entity.ChatRoom;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    @Transactional(readOnly = true)
    Page<ChatRoom> findByChatRoomMembersId(String userId, Pageable pageable);

    @Query("SELECT DISTINCT cr.id FROM ChatRoom cr " +
            "JOIN cr.chatRoomMembers m1 " +
            "JOIN cr.chatRoomMembers m2 " +
            "WHERE m1.id = :userId1 AND m2.id = :userId2")
    String findSharedChatRoom(@Param("userId1") String userId1, @Param("userId2") String userId2);

}
