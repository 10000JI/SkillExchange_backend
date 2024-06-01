package place.skillexchange.backend.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import place.skillexchange.backend.comment.entity.Comment;

import java.util.List;

import static place.skillexchange.backend.comment.entity.QComment.comment;


@RequiredArgsConstructor
public class CommentRepositoryImpl implements CustomCommentRepository {
    private final JPAQueryFactory queryFactory;

    //findCommentByNoticeId 구현
    /**
     * 공지사항의 대댓글 구현
     */
    @Override
    public List<Comment> findCommentByNoticeId(Long noticeId) {
        //querydsl을 이용한 조회 코드
        //부모 댓글 컬럼이 NULL이라면, 최상위 댓글이므로 nullsFirst로 조회, 작성일이 오래 전꺼부터 출력 (comment.regDate.asc())
        return queryFactory.selectFrom(comment)
                .leftJoin(comment.parent)
                .fetchJoin()
                .leftJoin(comment.writer)
                .fetchJoin()
                .leftJoin(comment.writer.authorities)
                .fetchJoin()
                .leftJoin(comment.writer.file)
                .fetchJoin()
                .where(comment.notice.id.eq(noticeId))
                .orderBy(
                        comment.parent.id.asc().nullsFirst(),
                        comment.regDate.asc()
                ).fetch();
        //ex> 부모 댓글 컬럼이 NULL이라면 최상위 댓글
        //1의 자식 댓글은 (2, 3), 2의 자식 댓글은(4, 5), 4의 자식 댓글은 (6)
        //1 NULL
        //8 NULL
        //2 1
        //3 1
        //4 2
        //5 2
        //7 3
        //6 4
    }

    /**
     * 재능교환의 대댓글 구현
     */
    @Override
    public List<Comment> findCommentByTalentId(Long talentId) {
        return queryFactory.selectFrom(comment)
                .leftJoin(comment.parent)
                .fetchJoin()
                .leftJoin(comment.writer)
                .fetchJoin()
                .leftJoin(comment.writer.authorities)
                .fetchJoin()
                .leftJoin(comment.writer.file)
                .fetchJoin()
                .where(comment.talent.id.eq(talentId))
                .orderBy(
                        comment.parent.id.asc().nullsFirst(),
                        comment.regDate.asc()
                ).fetch();
    }
}
