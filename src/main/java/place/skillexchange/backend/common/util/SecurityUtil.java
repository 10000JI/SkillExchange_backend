package place.skillexchange.backend.common.util;

import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import place.skillexchange.backend.exception.user.AccountLoginRequriedException;

@Component
@NoArgsConstructor
public class SecurityUtil {

    // SecurityContext 에 유저 정보가 저장되는 시점
    // Request 가 들어올 때 JwtFilter 의 doFilter 에서 저장
    public static String getCurrentMemberUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context 에 인증 정보가 없습니다.");
        } else if (authentication.getName().equals("anonymousUser")) {
            throw AccountLoginRequriedException.EXCEPTION;
        }

        //authenticaion은 principal을 extends 받은 객체. getName() 메서드는 사용자의 이름을 넘겨주었다.
        //String type의 username (유저의 id) -> UserDetails의 username와 동일
        return authentication.getName();
    }
    // SecurityContext 에 유저 정보가 저장되는 시점
    // Request 가 들어올 때 JwtFilter 의 doFilter 에서 저장
    public static String getCurrentMemberUsernameOrNonMember() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null || authentication.getName().equals("anonymousUser")) {
            return "non-Member";
        }

        //authenticaion은 principal을 extends 받은 객체. getName() 메서드는 사용자의 이름을 넘겨주었다.
        //String type의 username (유저의 id) -> UserDetails의 username와 동일
        return authentication.getName();
    }
}
