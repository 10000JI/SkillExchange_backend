package place.skillexchange.backend.user.repository;

import org.springframework.data.repository.CrudRepository;
import place.skillexchange.backend.user.entity.Refresh;

public interface RefreshRepository extends CrudRepository<Refresh, String> {

    Refresh findByRefreshToken(String refreshToken);
}
