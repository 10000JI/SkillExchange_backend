package place.skillexchange.backend.talent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import place.skillexchange.backend.talent.entity.Place;

import java.util.Optional;

public interface PlaceRepository  extends JpaRepository<Place, Long> {

    Optional<Place> findByPlaceName(String placeName);
}
