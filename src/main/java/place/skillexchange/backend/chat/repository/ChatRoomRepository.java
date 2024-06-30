package place.skillexchange.backend.chat.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import place.skillexchange.backend.chat.entity.ChatRoom;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    @Transactional(readOnly = true)
    Page<ChatRoom> findByChatRoomMembersId(String userId, Pageable pageable);

    @Query("SELECT DISTINCT cr.id FROM ChatRoom cr " +
            "JOIN cr.chatRoomMembers m1 " +
            "JOIN cr.chatRoomMembers m2 " +
            "WHERE m1.id = :userId1 AND m2.id = :userId2")
    String findSharedChatRoom(@Param("userId1") String userId1, @Param("userId2") String userId2);

    @Modifying
    @Query("UPDATE ChatRoom c SET c.lastChatMesg = null WHERE c.id IN :roomIds")
    void setLastChatMessageToNullByRoomIds(@Param("roomIds") List<String> roomIds); //roomIds 리스트에 포함된 모든 ChatRoom의 lastChatMesg를 null로 설정

    @Query("SELECT c.id FROM ChatRoom c JOIN c.chatRoomMembers m WHERE m.id = :userId")
    List<String> findChatRoomIdsByUserId(@Param("userId") String userId);

    @Modifying
    @Query(value = "DELETE FROM chat_room_members WHERE chat_room_id IN (:roomIds)", nativeQuery = true)
    void removeAllMembersFromChatRooms(@Param("roomIds") List<String> roomIds);

    void deleteByIdIn(List<String> roomIds); //리스트에 포함된 모든 ChatRoom 삭제
    //ChatRoom이 삭제될 때 ChatRoom_Members 테이블의 관련 엔트리들이 자동으로 삭제
}
