package place.skillexchange.backend.talent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import place.skillexchange.backend.talent.entity.Talent;

public interface TalentRepository extends JpaRepository<Talent, Long>, CustomTalentRepository {

}
