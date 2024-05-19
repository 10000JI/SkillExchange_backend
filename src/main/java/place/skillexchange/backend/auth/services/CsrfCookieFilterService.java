package place.skillexchange.backend.auth.services;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Service
public class CsrfCookieFilterService extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //HttpServletRequest에서 가능한 CsrfToken을 읽음
        //언제든 백엔드에 토큰 값이 생성되면 요청 속성으로써 사용 가능해질 것이며 읽어내서 CSRF 토큰의 객체에게 변환을 해줌
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        //csrfToken 안에 헤더 이름이 있는지 확인, null이 아닐 경우 CSRF 토큰을 생성함
        if(null != csrfToken.getHeaderName()){
            //응답 헤더에 csrfToken의 hearderName과 token 값을 주입
            response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
        }
        //해당 응답이 FilterChain 안에 있는 다음 필터에게 전달
        filterChain.doFilter(request, response);
    }
}
