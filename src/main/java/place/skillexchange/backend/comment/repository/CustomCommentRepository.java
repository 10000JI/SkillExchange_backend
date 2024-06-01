package place.skillexchange.backend.comment.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import place.skillexchange.backend.comment.entity.Comment;

import java.util.List;

//별도의 메소드를 추가하여 특정 쿼리를 정의 (유연한 쿼리를 작성)
public interface CustomCommentRepository {

    /**
     * 공지사항 게시물 번호의 댓글 목록
     */
    //findCommentsByNoticeIdWithParentOrderByParentIdAscNullsFirstCreatedAtAsc /**Impl에서 Querydsl로 구현**/
    List<Comment> findCommentByNoticeId(Long noticeId);

    /**
     * 재능교환 게시물 번호의 댓글 목록
     */
    //findCommentsByTalentIdWithParentOrderByParentIdAscNullsFirstCreatedAtAsc /**Impl에서 Querydsl로 구현**/
    List<Comment> findCommentByTalentId(Long talentId);
}
