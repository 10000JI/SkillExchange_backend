package place.skillexchange.backend.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import place.skillexchange.backend.comment.entity.Comment;
import place.skillexchange.backend.talent.entity.Talent;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {

    //FetchType.LAZY로 설정된 연관 관계에서도 left join fetch를 사용하여 특정 엔티티의 일부를 즉시 로드 **기존 작성 코드**
    @Query("select c from Comment c left join fetch c.parent where c.id = :id")
    Optional<Comment> findCommentByIdWithParent(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Comment c SET c.parent = NULL WHERE c.parent.id IN (SELECT c2.id FROM Comment c2 WHERE c2.notice.id = :noticeId)")
    void removeParentRelationForChildCommentsByNoticeId(@Param("noticeId") Long noticeId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.notice.id = :noticeId")
    void deleteParentCommentsByNoticeId(@Param("noticeId") Long noticeId);

    @Modifying
    @Query("UPDATE Comment c SET c.parent = NULL WHERE c.parent.id IN (SELECT c2.id FROM Comment c2 WHERE c2.talent.id = :talentId)")
    void removeParentRelationForChildCommentsByTalentId(@Param("talentId") Long talentId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.talent.id = :talentId")
    void deleteParentCommentsByTalentId(@Param("talentId") Long talentId);

    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.writer w WHERE w.id = :userId")
    List<Comment> findByWriterId(String userId);
}
