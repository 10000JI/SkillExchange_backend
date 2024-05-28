package place.skillexchange.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import place.skillexchange.backend.user.entity.RefreshToken;
import place.skillexchange.backend.user.entity.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findRefreshTokenByUser(User user);

    @Query("SELECT rt.user FROM RefreshToken rt WHERE rt.user.id = :userId")
    User findUserByRefreshTokenUserId(@Param("userId") String userId);

}
