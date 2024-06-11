package place.skillexchange.backend.auth.config;


import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import place.skillexchange.backend.auth.services.AuthFilterService;
import place.skillexchange.backend.exception.user.CustomAccessDeniedHandler;
import place.skillexchange.backend.exception.user.CustomAuthenticationEntryPoint;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    /**
     * (1) 계정 활성화를 위한 jwt 토큰 : 회원가입(/v1/user/signUp) 후 발급하여 이메일로 보내야 함 (Filtering(X), Controller-Service(O))
     * (2) accessToken : 로그인 시 생성하여 헤더에 저장 (Filtering(X), Controller-Service(O))
     * (3) refreshToken : 로그인 시 생성하여 DB에 저장 & 쿠키에 저장 (Filtering(X), Controller-Service(O))
     * (2)번과 (3)번은 같은 엔드포인트(/v1/user/signIn) 즉, 동일 메서드에 정의
     * (4) 회원 정보를 필요로 하는 엔드포인트들을 위해 accessToken 및 refreshToken 검증은 Filtering(O)
     */

    private final AuthFilterService authFilterService;

    private final AuthenticationProvider authenticationProvider;

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;


    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName("_csrf");

        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //CorsConfigurationSource 인터페이스를 구현하는 익명 클래스 생성하여 getCorsConfiguration() 메소드 재정의
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                    //getCorsConfiguration() 메소드에서 CorsConfiguration 객체를 생성하고 필요한 설정들을 추가
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        //허용할 출처(도메인)를 설정
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        //허용할 HTTP 메소드를 설정
                        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "PATCH"));
                        //인증 정보 허용 여부를 설정
                        config.setAllowCredentials(true);
                        //허용할 헤더를 설정
                        config.setAllowedHeaders(List.of("*"));
                        config.setExposedHeaders(Arrays.asList("Authorization"));
                        //CORS 설정 캐시로 사용할 시간을 설정
                        config.setMaxAge(3600L);
                        return config;
                    }
                }))
                .csrf(AbstractHttpConfigurer::disable)
                //DaoAuthenticationProvider의 세부 내역을 AuthenticationProvider 빈을 만들어 정의했으므로 인증을 구성해줘야 한다.
                .authenticationProvider(authenticationProvider)
                //.addFilterAfter(csrfCookieFilterService, BasicAuthenticationFilter.class)
                .addFilterBefore(authFilterService, UsernamePasswordAuthenticationFilter.class)
                //authFilterService가 인증 전에 실행되어 항상 검증되기 때문에 requestMatchers의 authenticated()과 permitAll()은 영향 X
                //하지만 코드 가독성을 위해 requestMatchers를 사용해 명시해주자
                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.PATCH, "/v1/notices/{noticeId}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/notices/{noticeId}").hasRole("ADMIN")
                        .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/v1/notices/register").hasRole("ADMIN")
                        .requestMatchers("/v1/user/**", "/v1/file/**", "/v1/notices/{noticeId}", "/v1/comment/**", "/v1/subjectCategory/**", "/v1/place/**", "/v1/talent/**","/v1/profile/get","/profile", "/actuator/health","/health").permitAll())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

}
