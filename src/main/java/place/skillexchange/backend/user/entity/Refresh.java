package place.skillexchange.backend.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@RedisHash(value = "refresh", timeToLive = 1209600) //2주
public class Refresh {
    @Id
    private String userId;
    @Indexed
    private String refreshToken;
}
