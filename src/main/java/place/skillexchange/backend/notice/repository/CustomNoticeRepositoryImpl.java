package place.skillexchange.backend.notice.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import place.skillexchange.backend.notice.dto.NoticeDto;
import place.skillexchange.backend.notice.entity.QNotice;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomNoticeRepositoryImpl implements CustomNoticeRepository {

    private final JPAQueryFactory queryFactory;

    //공지사항 페이징 처리 및 검색어 처리
    @Override
    public Page<NoticeDto.NoticeListResponse> findNoticesWithPagingAndKeyword(String keyword, Pageable pageable) {
        QNotice qNotice = QNotice.notice;

        // QueryDSL 라이브러리에서 사용되는 조건 표현식, 데이터베이스 쿼리의 WHERE 절에 사용
        BooleanExpression predicate = null;
        if (keyword != null && !keyword.isEmpty()) {
            //제목 또는 내용에 특정 키워드가 포함되는지 확인
            //containsIgnoreCase: 문자열에 특정 문자열이 포함되어 있는지를 대소문자를 구분하지 않고 확인하는 메서드
            predicate = qNotice.title.containsIgnoreCase(keyword)
                    .or(qNotice.content.containsIgnoreCase(keyword));
        }

        //Projections: QueryDSL에서 쿼리 결과를 변환하거나 특정 필드만 선택할 때 사용되는 기능
        //NoticeDto.ListResponse 클래스의 생성자를 사용하여 조회된 공지사항을 DTO로 변환
        /**
         * SELECT
         *     n.id, n.title, n.writer, n.regdate, n.hit,
         *     (SELECT COUNT(*) FROM comment c WHERE c.notice_id = n.id) AS comment_count
         * FROM
         *     notice n
         * WHERE
         *     (LOWER(n.title) LIKE CONCAT('%', #{keyword}, '%') OR LOWER(n.content) LIKE CONCAT('%', #{keyword}, '%'))
         * ORDER BY
         *     n.id DESC
         * LIMIT #{pageSize} OFFSET #{offset}
         */
        List<NoticeDto.NoticeListResponse> notices = queryFactory
                .select(Projections.constructor(NoticeDto.NoticeListResponse.class,
                        qNotice,
                        qNotice.comments.size().longValue() // 댓글 개수 조회
                ))
                .from(qNotice)
                .where(predicate)
                .orderBy(qNotice.id.desc())
                //getOffset(): 쿼리 결과의 시작점 (=skip)
                .offset(pageable.getOffset())
                //getPageSize(): 한 페이지에 몇 개의 결과 (=limit)
                .limit(pageable.getPageSize())
                //fetch(): 구성한 쿼리를 실행하고, 그 결과를 반환하는 역할
                //데이터베이스에서 데이터를 조회하려면 최종적으로 fetch() 메소드를 호출
                .fetch();

        //QNotice 엔티티에서 조건(predicate)에 맞는 데이터의 총 개수
        /**
         SELECT COUNT(*)
         FROM notice
         WHERE (LOWER(title) LIKE '%검색어%' OR LOWER(content) LIKE '%검색어%')
         */
        long totalCount = queryFactory.selectFrom(qNotice)
                .where(predicate)
                .fetchCount();

        return new PageImpl<>(notices, pageable, totalCount);
    }
}