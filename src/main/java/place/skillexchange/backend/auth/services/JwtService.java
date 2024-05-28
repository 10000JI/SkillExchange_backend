package place.skillexchange.backend.auth.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import place.skillexchange.backend.user.entity.Authority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
public class JwtService {
    /**
     * 클레임(Claim): JWT(토큰 기반의 웹 인증 시스템) 내에서 사용자에 대한 정보를 나타내는 JSON 객체
     */

    @Value("${custom.jwt.secretKey}")
    private String secretKeyPlain;


    /**
     * 비밀 키 : JWT에서 사용되는 비밀 키
     */
    private SecretKey getSignInKey() {
        // decode SECRET_KEY
        return Keys.hmacShaKeyFor(secretKeyPlain.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 토큰의 사용자 이름 추출
     */
    public String extractUsername(String token) {
        return String.valueOf(extractAllClaims(token).get("id"));
    }

    /**
     * 토큰의 사용자 권한 추출
     */
    public String extractAuthority(String token) {
        return (String) extractAllClaims(token).get("authorities");
    }

    /**
     * activeToken에서 모든 클레임을 추출하는 작업
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                //verifyWith(): key 같은 값을 보냄
                .verifyWith(getSignInKey())
                .build()
                //parseSignedClaims(): 받은 JWT 토큰 보냄
                .parseSignedClaims(token)
                //JWT 바디 값을 읽어보자, 특정 값을 나타내는 토큰 값이라면 헤더에서 서명 부분을 읽고 싶지 않은 것이다
                //getPayload() 메소드에서 claims를 가져옴
                .getPayload();
    }

    /**
     * 계정 활성화 토큰 (activeToken) 생성
     */
    public String generateActiveToken(UserDetails userDetails) {
        return Jwts
                .builder().issuer("Skill Exchange").subject("JWT Active Token")
                //claim(): 로그인된 유저의 ID를 채워줌
                .claim("id", userDetails.getUsername())
                //issuedAt(): 클라이언트에게 JWT 토큰이 발행시간 설정
                .issuedAt(new Date())
                //expiration(): 클라이언트에게 JWT 토큰이 만료시간 설정 (5분)
                .expiration(new Date((new Date()).getTime() + 5 * 60 * 1000))
                //signWith(): JWT 토큰 속 모든 요청에 디지털 서명을 하는 것, 여기서 위에서 설정한 비밀키를 대입
                .signWith(getSignInKey()).compact();
    }

    /**
     * 엑세스 토큰 (accessToken) 생성
     */
    public String generateAccessToken(UserDetails userDetails) {
        return Jwts
                .builder().issuer("Skill Exchange").subject("JWT Access Token")
                //claim(): 로그인된 유저의 ID, 권한을 채워줌
                .claim("id", userDetails.getUsername())
                .claim("authorities",populateAuthorities(userDetails.getAuthorities()))
                //issuedAt(): 클라이언트에게 JWT 토큰이 발행시간 설정
                .issuedAt(new Date())
                //expiration(): 클라이언트에게 JWT 토큰이 만료시간 설정 (5분)
                .expiration(new Date((new Date()).getTime() + 1 * 60 * 1000 /*24 * 60 * 60 * 1000*/))
                //signWith(): JWT 토큰 속 모든 요청에 디지털 서명을 하는 것, 여기서 위에서 설정한 비밀키를 대입
                .signWith(getSignInKey()).compact();
    }


    /**
     * 사용자 이름과 사용자 세부 정보를 기반으로 토큰이 유효한지 여부
     */
    public boolean isActiveTokenValid(String token, UserDetails userDetails) {
        final String id = extractUsername(token);
        return (id.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

//    /**
//     * 사용자 이름과 사용자 권한, 사용자 세부 정보를 기반으로 토큰이 유효한지 여부
//     */
//    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
//        final String id = extractUsername(token);
//        final String authorities = extractAuthority(token);
//        return (id.equals(userDetails.getUsername()) && authorities.equals(populateAuthorities(userDetails.getAuthorities())) && !isTokenExpired(token));
//    } // 만료 여부 확인했는데도 불구하고 굳이 사용자 이름 권한 등을 조회하고 있어 불필요하다고 판단

    /**
     * 토큰 만료 여부
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = extractExpiration(token);
            return expirationDate != null && expirationDate.before(new Date());
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었음을 처리
            return true;
        }
    }

    /**
     * 토큰에서 만료 일자 클레임을 추출하여 반환
     */
    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * user의 authority 여러 개일 수도 있음을 고려한 String 변경
     */
    private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> authoritiesSet = new HashSet<>();
        //내 모든 권한을 읽어옴
        for (GrantedAuthority authority : collection) {
            authoritiesSet.add(authority.getAuthority());
        }
        //String value로 "," 를 구분자로 권한들을 구분
        return String.join(",", authoritiesSet);
    }

    /**
     * 반대로 권한이 String으로 들어올 때 List<GrantedAuthority>로 반환
     */
    public Collection<? extends GrantedAuthority> getAuthorities(String token) {
        String strAuthorities = extractAuthority(token);

        // 하나의 권한만 있는 경우
        if (!strAuthorities.contains(",")) {
            return Collections.singletonList(new SimpleGrantedAuthority(strAuthorities));
        }

        StringTokenizer st = new StringTokenizer(strAuthorities, ",");
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        while (st.hasMoreTokens()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(st.nextToken()));
        }
        return grantedAuthorities;
    }
}
