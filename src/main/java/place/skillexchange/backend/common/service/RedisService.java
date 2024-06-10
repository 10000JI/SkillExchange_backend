package place.skillexchange.backend.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import place.skillexchange.backend.notice.entity.Notice;
import place.skillexchange.backend.talent.entity.Talent;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {
    private final Long clientAddressPostRequestWriteExpireDurationSec = 86400L;
    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isFirstIpRequest(String clientAddress, Long postId, Object reference) {
        String key = generateKey(clientAddress, postId, reference);
        log.debug("user post request key: {}", key);
        if (redisTemplate.hasKey(key)) {
            return false;
        }
        return true;
    }

    public void writeClientRequest(String userId, Long talentId, Object reference) {
        String key = generateKey(userId, talentId, reference);
        log.debug("user post request key: {}", key);

        redisTemplate.opsForValue().append(key, String.valueOf(talentId));
        redisTemplate.expire(key, clientAddressPostRequestWriteExpireDurationSec, TimeUnit.SECONDS);
    }

    private String generateKey(String userId, Long talentId, Object reference) {
        String objectType;
        if (reference instanceof Notice) {
            objectType = "notice";
        } else if (reference instanceof Talent) {
            objectType = "talent";
        } else {
            objectType = "unknown";
        }
        return userId + "'s " + objectType + "Num - No." + talentId;
    }

}
