package place.skillexchange.backend.comment.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import place.skillexchange.backend.comment.entity.Comment;

import java.util.List;

//별도의 메소드를 추가하여 특정 쿼리를 정의 (유연한 쿼리를 작성)
public interface CustomCommentRepository {

    // 꼬리에 꼬리를 물어 계속해서 users와 file이 출력되는 것을 해결 (lazy 로딩 n+1 해결)
    @Query("select c from Comment c left join fetch c.writer w left join fetch w.authorities a left join fetch w.file where c.notice.id = :noticeId")
    List<Comment> findCommentByNoticeId(Long noticeId);
}
