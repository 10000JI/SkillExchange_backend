package place.skillexchange.backend.chat.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import place.skillexchange.backend.auth.services.JwtService;
import place.skillexchange.backend.exception.user.AccessTokenRequiredException;
import place.skillexchange.backend.exception.user.UserTokenExpriedException;

import java.security.SignatureException;

@RequiredArgsConstructor
@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            String accessToken = accessor.getFirstNativeHeader("Authorization");
            if (!this.validateAccessToken(accessToken)) {
                throw AccessTokenRequiredException.EXCEPTION;
            }
            String id = getUserId(accessToken);
            accessor.addNativeHeader("senderUserId", id);
        }


        return message;
    }

    public String getUserId(String accessToken) {
        try {
            String token = accessToken.trim();
            if (token.startsWith("Bearer ")) {
                token = token.substring(7).trim();
            }
            String id = jwtService.extractUsername(token);
            if (id == null || id.isEmpty()) {
                throw UserTokenExpriedException.EXCEPTION;
            }
            return id;
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰: {}", e.getMessage());
            throw UserTokenExpriedException.EXCEPTION;
        } catch (MalformedJwtException e) {
            log.error("잘못된 형식의 JWT 토큰: {}", e.getMessage());
            throw UserTokenExpriedException.EXCEPTION;
        } catch (Exception e) {
            log.error("사용자 ID 추출 중 예기치 않은 오류 발생: {}", e.getMessage());
            throw UserTokenExpriedException.EXCEPTION;
        }
    }

    private boolean validateAccessToken(String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            return false;
        }

        String token = accessToken.trim();
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();
        }

        try {
            Claims claims = jwtService.extractAllClaims(token);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException e) {
            log.error("토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }
}
