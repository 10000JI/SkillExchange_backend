package place.skillexchange.backend.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import place.skillexchange.backend.comment.entity.Comment;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {

    //FetchType.LAZY로 설정된 연관 관계에서도 left join fetch를 사용하여 특정 엔티티의 일부를 즉시 로드
    @Query("select c from Comment c left join fetch c.parent where c.id = :id")
    Optional<Comment> findCommentByIdWithParent(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Comment c SET c.parent = NULL WHERE c.parent.id IN (SELECT c2.id FROM Comment c2 WHERE c2.notice.id = :noticeId)")
    void removeParentRelationForChildComments(@Param("noticeId") Long noticeId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.notice.id = :noticeId")
    void deleteParentComments(@Param("noticeId") Long noticeId);
}
