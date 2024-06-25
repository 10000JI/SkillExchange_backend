package place.skillexchange.backend.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import place.skillexchange.backend.auth.services.JwtService;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompEventListener {

    private final JwtService jwtService;
    //private final StompHandler stompHandler;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("[Connected] websocket session id : {}", sessionId);
    }

//    @EventListener(SessionConnectEvent.class)
//    public void onApplicationEvent(SessionConnectEvent event) {
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//        String accessToken = accessor.getFirstNativeHeader("Authorization");
//
//        if (accessToken != null && accessToken.startsWith("Bearer ")) {
//            // "Bearer " 접두사 제거 및 공백 제거
//            accessToken = accessToken.substring(7).trim();
//
//            try {
//                if (!jwtService.isTokenExpired(accessToken)) {
//                    String id = stompHandler.getUserId(accessToken);
//                    accessor.getSessionAttributes().put("senderUserId", id);
//                } else {
//                    // 토큰이 만료된 경우 로그
//                    log.warn("Expired token received");
//                }
//            } catch (Exception e) {
//                // JWT 처리 중 발생한 예외 로그
//                log.error("Error processing JWT token", e);
//            }
//        } else {
//            // 유효한 Authorization 헤더가 없는 경우 로그
//            log.warn("No valid Authorization header found");
//        }
//    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("[Disconnected] websocket session id : {}", sessionId);
    }
}
