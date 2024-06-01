package place.skillexchange.backend.talent.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import place.skillexchange.backend.file.entity.QFile;
import place.skillexchange.backend.talent.dto.TalentDto;
import place.skillexchange.backend.talent.entity.QPlace;
import place.skillexchange.backend.talent.entity.QSubjectCategory;
import place.skillexchange.backend.talent.entity.QTalent;
import place.skillexchange.backend.user.entity.QAuthority;
import place.skillexchange.backend.user.entity.QUser;

import java.util.List;

@RequiredArgsConstructor
public class CustomTalentRepositoryImpl implements CustomTalentRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TalentDto.TalentListResponse> findAllWithPagingAndSearch(String keyword, Pageable pageable, Long subjectCategoryId) {
        QTalent qTalent = QTalent.talent;
        QUser qUser = QUser.user;
        QAuthority qAuthority = QAuthority.authority;
        QPlace qPlace = QPlace.place;
        QSubjectCategory qTeachedSubject = new QSubjectCategory("teachedSubject");
        QSubjectCategory qTeachingSubject = new QSubjectCategory("teachingSubject");
        QFile qFile = QFile.file;

        //subjectCategoryId가 있다면 카테고리 별 게시물 목록
        BooleanExpression predicate = qTalent.isNotNull();
        if (subjectCategoryId != null) {
            predicate = predicate.and(qTalent.teachedSubject.id.eq(subjectCategoryId));
        }

        //content, teachedSubject, teachingSubject, place를 검색조건으로 함
        if (keyword != null && !keyword.isEmpty()) {
            predicate = predicate.and(qTalent.content.containsIgnoreCase(keyword)
                    .or(qTalent.teachedSubject.subjectName.containsIgnoreCase(keyword))
                    .or(qTalent.teachingSubject.subjectName.containsIgnoreCase(keyword))
                    .or(qTalent.place.placeName.containsIgnoreCase(keyword)));
        }

        List<TalentDto.TalentListResponse> talents = queryFactory
                .select(Projections.constructor(TalentDto.TalentListResponse.class, qTalent))
                .from(qTalent)
                .leftJoin(qTalent.writer, qUser).fetchJoin()
                .leftJoin(qTalent.writer.authorities, qAuthority).fetchJoin()
                .leftJoin(qTalent.writer.file, qFile).fetchJoin()
                .leftJoin(qTalent.place, qPlace).fetchJoin()
                .leftJoin(qTalent.teachedSubject, qTeachedSubject).fetchJoin()
                .leftJoin(qTalent.teachingSubject, qTeachingSubject).fetchJoin()
                .where(predicate)
                .orderBy(qTalent.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory
                .selectFrom(qTalent)
                .where(predicate)
                .fetchCount();

        return new PageImpl<>(talents, pageable, totalCount);
    }
}