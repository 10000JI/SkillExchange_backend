package place.skillexchange.backend.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setBlackList(String key, Object o, Duration minutes) {
        redisTemplate.opsForValue().set(key, o, minutes);
    }

    public Object getBlackList(String key) {
        return redisTemplate.opsForValue().get(key);
    }

}
