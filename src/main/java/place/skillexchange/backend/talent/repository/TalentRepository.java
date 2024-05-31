package place.skillexchange.backend.talent.repository;

import com.querydsl.core.annotations.QueryEmbedded;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import place.skillexchange.backend.notice.entity.Notice;
import place.skillexchange.backend.talent.entity.Talent;
import place.skillexchange.backend.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface TalentRepository extends JpaRepository<Talent, Long>, CustomTalentRepository {

    @Query("SELECT t FROM Talent t LEFT JOIN TalentScrap ts ON t.id = ts.talent.id WHERE ts.user.id = :userId")
    List<Talent> findTalentsByUserIdWithScrap(@Param("userId") String userId);

    @EntityGraph(attributePaths = {"writer","writer.file","files","place","teachedSubject","teachedSubject.parent","teachingSubject","teachingSubject.parent","dayOfWeek"})
    Optional<Talent> findWithWriterAndFilesById(Long noticeId);

}
