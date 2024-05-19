package place.skillexchange.backend.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import place.skillexchange.backend.comment.entity.Comment;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>,  CustomCommentRepository {

    //FetchType.LAZY로 설정된 연관 관계에서도 left join fetch를 사용하여 특정 엔티티의 일부를 즉시 로드
    @Query("select c from Comment c left join fetch c.parent where c.id = :id")
    Optional<Comment> findCommentByIdWithParent(@Param("id") Long id);

}
