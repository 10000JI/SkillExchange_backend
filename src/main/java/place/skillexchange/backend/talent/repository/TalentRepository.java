package place.skillexchange.backend.talent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import place.skillexchange.backend.talent.entity.Talent;

import java.util.List;

public interface TalentRepository extends JpaRepository<Talent, Long>, CustomTalentRepository {

    @Query("SELECT t FROM Talent t LEFT JOIN TalentScrap ts ON t.id = ts.talent.id WHERE ts.user.id = :userId")
    List<Talent> findTalentsByUserIdWithScrap(@Param("userId") String userId);
}
