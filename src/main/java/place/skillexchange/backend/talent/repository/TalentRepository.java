package place.skillexchange.backend.talent.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import place.skillexchange.backend.talent.entity.Talent;

import java.util.List;
import java.util.Optional;

public interface TalentRepository extends JpaRepository<Talent, Long>, CustomTalentRepository {

    @Query("SELECT t FROM Talent t LEFT JOIN TalentScrap ts ON t.id = ts.talent.id WHERE ts.user.id = :userId")
    List<Talent> findTalentsByUserIdWithScrap(@Param("userId") String userId);

    //@EntityGraph(attributePaths = {"writer","writer.file","place","files","teachedSubject","teachedSubject.parent","teachingSubject","teachingSubject.parent"})
    //@Query("SELECT DISTINCT t FROM Talent t WHERE t.id = :talentId")
    @Query("SELECT DISTINCT t FROM Talent t " +
            "LEFT JOIN FETCH t.writer w " +
            "LEFT JOIN FETCH w.file " +
            "LEFT JOIN FETCH t.files " +
            "LEFT JOIN FETCH t.place " +
            "LEFT JOIN FETCH t.teachedSubject ts " +
            "LEFT JOIN FETCH ts.parent " +
            "LEFT JOIN FETCH t.teachingSubject tgs " +
            "LEFT JOIN FETCH tgs.parent " +
            "WHERE t.id = :talentId")
    Optional<Talent> findWithAllAssociationsById(Long talentId);
    @EntityGraph(attributePaths = {"writer","writer.file","place","teachedSubject","teachingSubject","dayOfWeek"})
    Optional<Talent> findWithPartAssociationsById(Long talentId);

    @Query("SELECT COUNT(t) FROM Talent t WHERE t.id = :talentId")
    Long countByTalentId(@Param("talentId") Long talentId);

    //관련 게시물 5개 뽑아내기
    @Query("SELECT t FROM Talent t LEFT JOIN FETCH t.teachedSubject sc WHERE sc.subjectName = :subjectName ORDER BY t.id DESC")
    List<Talent> findRelatedPostsById(Pageable pageable, String subjectName);

    @Query("SELECT t FROM Talent t LEFT JOIN FETCH t.writer w WHERE w.id = :userId")
    List<Talent> findByWriterId(String userId);
}
