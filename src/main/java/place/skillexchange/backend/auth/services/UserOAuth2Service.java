package place.skillexchange.backend.auth.services;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import place.skillexchange.backend.user.entity.AuthProvider;
import place.skillexchange.backend.user.entity.Authority;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.user.repository.UserRepository;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserOAuth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.error("OAuth2User attributes: {}", oAuth2User.getAttributes());

        Map<String, Object> attributes = oAuth2User.getAttributes();

        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakao_account.get("email");

//        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
//        String nickname = (String) properties.get("nickname");

        if (!userRepository.findById("KAKAO_" + email).isPresent()) { //db에 해당 회원정보 없다면 저장
            userRepository.save(emailToSave(email));
        }

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), attributes, "id");
    }

    private User emailToSave(String email) {
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();
        return User.builder().id("KAKAO_" + email).authorities(Collections.singleton(authority)).provider(AuthProvider.KAKAO).active(true).build();
    }

}
