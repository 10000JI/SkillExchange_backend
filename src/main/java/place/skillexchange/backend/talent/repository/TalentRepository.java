package place.skillexchange.backend.talent.repository;

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

    @EntityGraph(attributePaths = {"writer","writer.file","files","place","teachedSubject","teachedSubject.parent","teachingSubject","teachingSubject.parent","dayOfWeek"})
    Optional<Talent> findWithAllAssociationsById(Long noticeId);
    @EntityGraph(attributePaths = {"writer","writer.file","place","teachedSubject","teachingSubject","dayOfWeek"})
    Optional<Talent> findWithPartAssociationsById(Long noticeId);

    @Query("SELECT COUNT(t) FROM Talent t WHERE t.id = :talentId")
    Long countByTalentId(@Param("talentId") Long talentId);

}
