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
    private final RedisTemplate<String, Boolean> redisTemplate;

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

        // 사실 set 할때 value가 필요없음. 그나마 가장 작은 불린으로 넣긴 했는데 아직 레디스를 잘 몰라서 이렇게 쓰고 있음
        redisTemplate.opsForValue().set(key, true);
        redisTemplate.expire(key, clientAddressPostRequestWriteExpireDurationSec, TimeUnit.SECONDS);
    }

    // key 형식 : 'client Address + postId' ->  '\xac\xed\x00\x05t\x00\x0f127.0.0.1 + 500'
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
