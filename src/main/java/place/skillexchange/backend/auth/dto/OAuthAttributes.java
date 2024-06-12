package place.skillexchange.backend.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import place.skillexchange.backend.user.entity.AuthProvider;
import place.skillexchange.backend.user.entity.Authority;
import place.skillexchange.backend.user.entity.User;

import java.util.Collections;
import java.util.Map;

@Getter
@ToString
public class OAuthAttributes {
    private Map<String, Object> attributes;     // OAuth2 반환하는 유저 정보
    private String nameAttributesKey;
    private String email;
    private AuthProvider provider;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributesKey, String email,
                           AuthProvider provider) {
        this.attributes = attributes;
        this.nameAttributesKey = nameAttributesKey;
        this.email = email;
        this.provider = provider;
    }

    public static OAuthAttributes of(String socialName, Map<String, Object> attributes) {
        if ("kakao".equals(socialName)) {
            return ofKakao("id", attributes);
        } else if ("google".equals(socialName)) {
            return ofGoogle("sub", attributes);
        }

        return null;
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .email("google_" + String.valueOf(attributes.get("email")))
                .attributes(attributes)
                .nameAttributesKey(userNameAttributeName)
                .provider(AuthProvider.google)
                .build();
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        return OAuthAttributes.builder()
                .email("kakao_"+ String.valueOf(kakaoAccount.get("email")))
                .nameAttributesKey(userNameAttributeName)
                .attributes(attributes)
                .provider(AuthProvider.kakao)
                .build();
    }

    public User toEntity() {
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        return User.builder()
                .id(email)
                .authorities(Collections.singleton(authority))
                .provider(provider)
                .active(true)
                .build();
    }
}
