package place.skillexchange.backend.comment.repository;

import place.skillexchange.backend.comment.entity.Comment;

import java.util.List;

//별도의 메소드를 추가하여 특정 쿼리를 정의 (유연한 쿼리를 작성)
public interface CustomCommentRepository {

    //findCommentsByTicketIdWithParentOrderByParentIdAscNullsFirstCreatedAtAsc
    List<Comment> findCommentByNoticeId(Long noticeId);
}
