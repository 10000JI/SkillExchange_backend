package place.skillexchange.backend.common.util;

import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;
import place.skillexchange.backend.notice.entity.Notice;
import place.skillexchange.backend.talent.entity.Talent;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class CookieUtil {

    private final static String VIEWCOOKIENAME = "AlreadyView";

    public Cookie createCookieForForNotOverlap(Long postId, Object reference) {
        Cookie cookie = new Cookie(getCookieName(postId,reference), String.valueOf(postId));
        cookie.setMaxAge(getExpirationInSeconds(24 * 60 * 60)); // 24시간 = 24 * 60 * 60 초
        cookie.setHttpOnly(true); // 서버에서만 조작 가능
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None"); //쿠키에 samesite 속성 추가

        return cookie;
    }

    public int getExpirationInSeconds(int expirationInSeconds) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusSeconds(expirationInSeconds);
        return (int) now.until(expirationTime, ChronoUnit.SECONDS);
    }

    public String getCookieName(Long postId, Object reference) {
        return VIEWCOOKIENAME + getObjectName(reference) + "-No." + postId;
    }

    public String getObjectName(Object reference) {
        String objectType;
        if (reference instanceof Notice) {
            objectType = "NoticeNum";
        } else if (reference instanceof Talent) {
            objectType = "TalentNum";
        } else {
            objectType = "UnknownNum";
        }
        return objectType;
    }
}
