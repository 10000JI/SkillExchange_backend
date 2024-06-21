package place.skillexchange.backend.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, Object> redisTemplate;

    public void setBlackList(String key, Object o, Duration minutes) {
        redisTemplate.opsForValue().set(key, o, minutes);
    }

    /**
     * 키를 이용한 값 확인
     */
    public Object getValues(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 소셜 로그인 탈퇴를 위해 저장, 만료일, 삭제 등
//    /**
//     * 만료 시간 없는 키 값 지정
//     */
//    @Transactional
//    public void setValues(String key, String value){
//        redisTemplate.opsForValue().set(key, value);
//    }

    /**
     * 만료시간 설정 -> 자동삭제
     */
    @Transactional
    public void setValuesWithTimeout(String key, String value, long timeout){
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 키 삭제
     */
    @Transactional
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }
}
