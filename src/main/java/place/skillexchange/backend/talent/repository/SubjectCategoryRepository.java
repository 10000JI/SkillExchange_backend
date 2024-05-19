package place.skillexchange.backend.talent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import place.skillexchange.backend.talent.entity.SubjectCategory;

import java.util.List;
import java.util.Optional;


public interface SubjectCategoryRepository extends JpaRepository<SubjectCategory,Long> {

    //SELECT c.*
    //FROM subject_category c
    //LEFT JOIN subject_category p ON c.parent_id = p.subject_id
    //ORDER BY
    //    CASE WHEN p.subject_id IS NULL THEN 0 ELSE 1 END,
    //    p.subject_id ASC,
    //    c.subject_id ASC;
    //부모 카테고리의 ID가 NULL인 경우를 처리하기 위해 CASE 문을 사용하여 NULL 값을 0으로 대체. 부모 카테고리의 ID를 오름차순으로 정렬하고, 자식 카테고리의 ID를 오름차순으로 정렬
    @Query("SELECT c FROM SubjectCategory c LEFT JOIN c.parent p ORDER BY p.id ASC NULLS FIRST, c.id ASC")
    List<SubjectCategory> findAllOrderByParentIdAscNullsFirstCategoryIdAsc();

    Optional<SubjectCategory> findBySubjectName(String subjectName);

    Optional<SubjectCategory> findByIdAndParentIsNotNull(Long id);
}
