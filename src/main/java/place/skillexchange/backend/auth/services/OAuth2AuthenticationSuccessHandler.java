package place.skillexchange.backend.auth.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import place.skillexchange.backend.user.entity.Refresh;
import place.skillexchange.backend.user.repository.RefreshRepository;
import place.skillexchange.backend.user.repository.UserRepository;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${custom.jwt.secretKey}")
    private String secretKeyPlain;
    private final RefreshRepository refreshRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

//        login 성공한 사용자 목록.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> kakao_account = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        String email = (String) kakao_account.get("email");

        //accessToken 생성
        String accessToken = generateAccessToken(email);
        //refreshToken 생성
        String refreshToken = generateRefreshToken(email);

        String url = createURI(accessToken, refreshToken).toString();
        System.out.println("url: " + url);

        if (response.isCommitted()) {
            logger.debug("응답이 이미 커밋된 상태입니다. " + url + "로 리다이렉트하도록 바꿀 수 없습니다.");
            return;
        }
        getRedirectStrategy().sendRedirect(request, response, url);
    }

//    private String makeRedirectUrl(String token) {
//        return UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect/"+token)
//                .build().toUriString();
//    }

    // Redirect URI 생성. JWT를 쿼리 파라미터로 담아 전달한다.
    private URI createURI(String accessToken, String refreshToken) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", accessToken);
        queryParams.add("refresh_token", refreshToken);

        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("localhost")
                .port(3000)
                .path("/oauth2/redirect")
                .queryParams(queryParams)
                .build()
                .toUri();
    }

    //AccessToken 생성
    private String generateAccessToken(String email) {
        return Jwts
                .builder().issuer("Skill Exchange").subject("JWT Access Token")
                //claim(): 로그인된 유저의 ID, 권한을 채워줌
                .claim("id", "KAKAO_" + email)
                .claim("authorities", "ROLE_USER")
                //issuedAt(): 클라이언트에게 JWT 토큰이 발행시간 설정
                .issuedAt(new Date())
                //expiration(): 클라이언트에게 JWT 토큰이 만료시간 설정 (하루)
                .expiration(new Date((new Date()).getTime() + /*1 * 60 * 1000*/ 24 * 60 * 60 * 1000))
                //signWith(): JWT 토큰 속 모든 요청에 디지털 서명을 하는 것, 여기서 위에서 설정한 비밀키를 대입
                .signWith(Keys.hmacShaKeyFor(secretKeyPlain.getBytes(StandardCharsets.UTF_8))).compact();
    }

    //RefreshToken 생성 (이미 있어도 덮어쓰기 가능)
    private String generateRefreshToken(String email) {
        Refresh redis = Refresh.builder()
                .refreshToken(UUID.randomUUID().toString())
                .userId("KAKAO_" + email)
                .build();
        refreshRepository.save(redis);
        return redis.getRefreshToken();
    }
}
