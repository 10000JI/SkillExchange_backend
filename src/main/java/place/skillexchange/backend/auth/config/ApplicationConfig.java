package place.skillexchange.backend.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import place.skillexchange.backend.exception.user.UserNotFoundException;
import place.skillexchange.backend.user.repository.UserRepository;

@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 사용자 정보 반환 : loadUserByUsername() 와 동일 역할
     * */
    @Bean
    public UserDetailsService userDetailsService() {
        return id -> userRepository.findById(id)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);
    }

    /**
     * 인증 공급자인 DaoAuthenticationProvider에 세부내역 설정
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        //UserDetailsService는 user DAO를 사용되므로 DaoAuthenticationProvider에 내가 정의한 userDetailsService를 주입
        authenticationProvider.setUserDetailsService(userDetailsService());
        //BCryptPasswordEncoder로 설정하겠다고 PasswordEncoder 빈을 만든 매서드 passwordEncoder()를 주입
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * 자동으로 구성되지 않을 때를 대비하기 위해 AuthenticationManager를 명시적으로 정의
     * */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
