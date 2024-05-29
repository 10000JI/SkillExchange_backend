package place.skillexchange.backend.auth.services;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import place.skillexchange.backend.exception.user.UserNotFoundException;
import place.skillexchange.backend.user.entity.RefreshToken;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.user.repository.UserRepository;

import java.io.IOException;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthFilterService extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private final RefreshTokenService refreshTokenService;

    private final UserRepository userRepository;


    /**
     * 헤더에 토큰을 저장하는데, 저장된 토큰을 헤더에서 꺼내서 유효한지 검증하는 작업, 실패시 filer 작용으로 (security) exceptionHandler 작동 안됨 해결해야 할 과제 어떻게 프론트에게 에러임을 알려줄 수 있는가
     */
/*    1. 엑세스 토큰 만료되었을 때 리프레시 토큰 db에 있으면 엑세스 토큰 재발급, 재발급한 엑세스 토큰은 헤더에 넣어서 보낸다.
      2, 엑세스 토큰 만료되었을 때 리프레시 토큰 db에 있는거 확인 후 만료일자가 지났다면 리프레시 토큰 삭제
       -> 엑세스 토큰 만료되고, 리프레시 토큰도 없기 때문에 "새로 로그인 하시오"라는 문구가 띄어져야 한다.
      3.  디폴트는 헤더에 저장된 토큰을 꺼내 유효한지 확인 */

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        //Authorization 이름을 가진 헤더의 값을 꺼내옴
        final String authHeader = request.getHeader("Authorization");
        String jwt;

        //authHeader가 null이고, Bearer로 시작하지 않다면 체인 내의 다음 필터를 호출
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            //체인 내의 다음 필터를 호출
            filterChain.doFilter(request, response);
            return;
        }

        // authHeader의 `Bearer `를 제외한 문자열 jwt에 담은
        jwt = authHeader.substring(7);


        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //accessToken이 만료되었다면
            if (jwtService.isTokenExpired(jwt)) {
                //쿠키의 refreshToken과 db에 저장된 refreshToken의 만료일을 확인하고 accessToken 재발급 / 만료되면 재로그인 exception
                handleExpiredToken(request, response);
            } else {
                //accessToken이 만료되지 않았다면 유효한지 검증
                authenticateUser(jwt, request, response);
            }
        }
        //체인 내의 다음 필터를 호출
        filterChain.doFilter(request, response);
    }

    private void handleExpiredToken(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String refreshTokenValue = extractRefreshTokenFromCookie(request);
        if (refreshTokenValue != null) {
            RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenValue);
            if (refreshToken != null) {
                //User user = refreshToken.getUser();
                User user = userRepository.findById(refreshToken.getUser().getId()).orElseThrow(() -> UserNotFoundException.EXCEPTION);
                String accessToken = jwtService.generateAccessToken(user);
                response.setHeader("Authorization", "Bearer " + accessToken);

//                //UserDetailsService에서 loadUserByUsername 메서드로 사용자 세부 정보 검색
//                UserDetails userDetails = userDetailsService.loadUserByUsername(user.getId());
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                        user.getId(),
                        "",
                        true,
                        true,
                        true,
                        true,
                        user.getAuthorities()
                );
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                //authenticationToken의 세부정보 설정
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //해당 인증 객체를 SecurityContextHolder에 authenticationToken 설정
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        // 쿠키에서 refreshToken 가져오기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void authenticateUser(String jwt, HttpServletRequest request, HttpServletResponse response) {
//        // jwt의 사용자 이름 추출
//        String id = jwtService.extractUsername(jwt);
//        //UserDetailsService에서 loadUserByUsername 메서드로 사용자 세부 정보 검색
//        UserDetails userDetails = userDetailsService.loadUserByUsername(id);  //db를 방문할 필요가 없음 , jwt 서명만 하면 된다
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                jwtService.extractUsername(jwt),
                "",
                true,
                true,
                true,
                true,
                jwtService.getAuthorities(jwt)
        );
        //UsernamePasswordAuthenticationToken 대상을 생성 (사용자이름,암호(=null로 설정),권한)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        //authenticationToken의 세부정보 설정
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        //해당 인증 객체를 SecurityContextHolder에 authenticationToken 설정
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //헤더에 accessToken 유효하므로 동일하게 설정
        response.setHeader("Authorization", "Bearer " + jwt);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        return servletPath.equals("/v1/user/withdraw");
    }
}