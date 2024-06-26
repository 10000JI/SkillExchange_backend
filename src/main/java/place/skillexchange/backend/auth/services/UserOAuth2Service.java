package place.skillexchange.backend.auth.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import place.skillexchange.backend.auth.dto.OAuth2CustomUser;
import place.skillexchange.backend.auth.dto.OAuthAttributes;
import place.skillexchange.backend.common.util.RedisUtil;
import place.skillexchange.backend.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserOAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final RedisUtil redisUtil;
    private final long ACCESS_TOKEN_EXPIRATION = 3600 * 1000;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = service.loadUser(userRequest);  // OAuth2 정보를 가져옵니다.
        //log.error("OAuth2User attributes: {}", oAuth2User.getAttributes());

        // 카카오(혹은 구글) 서버에서 발급해주는 AccessToken 추출
        String oauth2AccessToken = userRequest.getAccessToken().getTokenValue();

        Map<String, Object> originAttributes = oAuth2User.getAttributes(); // OAuth2User의 attribute

        // OAuth2 서비스 id (google, kakao)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();    // 소셜 정보를 가져옵니다.

        // OAuthAttributes: OAuth2User의 attribute를 서비스 유형에 맞게 담아줄 클래스
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, originAttributes);

        if (!userRepository.findById(attributes.getEmail()).isPresent()) { //db에 해당 회원정보 없다면 저장
            userRepository.save(attributes.toEntity());
        }

        /* 레디스 토큰 정보 */
        redisUtil.setValuesWithTimeout("AT(oauth2):" + attributes.getEmail() ,oauth2AccessToken, ACCESS_TOKEN_EXPIRATION);

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new OAuth2CustomUser(registrationId, originAttributes, authorities, attributes.getEmail());
    }

}
