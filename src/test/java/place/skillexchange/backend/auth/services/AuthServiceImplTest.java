package place.skillexchange.backend.auth.services;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import place.skillexchange.backend.common.service.MailService;
import place.skillexchange.backend.user.dto.UserDto;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.user.repository.UserRepository;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private MailService mailService;
    @Mock
    private AuthenticationManager authenticationManager;
//    @Mock
//    private RefreshTokenService refreshTokenService;
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("회원가입 테스트")
    public void testProfileUpdate() throws IOException, MessagingException, MethodArgumentNotValidException {
        // Given
        UserDto.SignUpRequest request = UserDto.SignUpRequest.builder()
                .id("testUser")
                .email("test@example.com")
                .password("password")
                .passwordCheck("password")
                .build();
        BindingResult bindingResult = mock(BindingResult.class);
        User user = User.builder().id(request.getId()).email(request.getEmail()).build();

        given(userRepository.findByEmailAndId(request.getEmail(), request.getId())).willReturn(Optional.empty());
        given(userRepository.findById(request.getId())).willReturn(Optional.empty());
        given(userRepository.findByEmail(request.getEmail())).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(user);

        // jwtService.generateActiveToken() 메서드 모킹
        given(jwtService.generateActiveToken(user)).willReturn("dummyActiveToken");
        // When
        UserDto.SignUpInResponse response = authService.register(request, bindingResult);

        // Then
        assertThat(response.getReturnCode()).isEqualTo(201);
        assertThat(response.getReturnMessage()).isEqualTo("이메일(test@example.com)을 확인하여 회원 활성화를 완료해주세요.");

        //메서드가 특정한 인수로 한 번 호출되었는지를 확인//

        // 중복 회원 검증 확인 (active가 0이면서 다음과 같은 id와 email을 가지고 있는 사람이 존재하는지 확인하는 메서드)
        verify(userRepository).findByEmailAndId(eq("test@example.com"), eq("testUser"));

        // 회원 저장 확인
        verify(userRepository).save(any(User.class));

        // 회원 활성화 이메일 전송 확인
        verify(mailService).getEmail(eq("test@example.com"), eq("testUser"), eq("dummyActiveToken"));
    }

//    @Test
//    @DisplayName("로그인 성공 테스트")
//    public void testLogin_Success() {
//        // Given
//        UserDto.SignInRequest request = new UserDto.SignInRequest("testUser", "password");
//        User user = User.builder().id("testUser").active(true).build();
//        String accessToken = "dummyAccessToken";
//        String refreshToken_Jwt = "dummyRefreshToken";
//
//        RefreshToken refreshToken = RefreshToken.builder()
//                //refreshToken은 UUID로 생성
//                .refreshToken(refreshToken_Jwt)
//                //만료일은 2분 (실제로는 2주 정도로 설정)
//                .expirationTime(new Date((new Date()).getTime() + 14 * 24 * 60 * 60 * 1000))
//                .user(user)
//                .build();
//
//        given(authenticationManager.authenticate(any()))
//                .willReturn(new UsernamePasswordAuthenticationToken(user.getId(), request.getPassword()));
////        given(userRepository.findByIdAndActiveIsTrue(request.getId())).willReturn(user);
//        given(jwtService.generateAccessToken(user)).willReturn(accessToken);
//        given(refreshTokenService.createRefreshToken(request.getId())).willReturn(refreshToken);
//
//        // When
//        ResponseEntity<UserDto.SignUpInResponse> responseEntity = authService.login(request);
//
//        // Then
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody().getReturnCode()).isEqualTo(200);
//        assertThat(responseEntity.getBody().getReturnMessage()).isEqualTo("로그인 성공!");
//        assertThat(responseEntity.getHeaders().get("Authorization")).containsExactly("Bearer " + accessToken);
//
//
//        List<String> setCookieHeaders = responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE);
//        log.error("setCookie::::{}::::::",setCookieHeaders.get(0));
//
//        // 쿠키 헤더에서 refreshToken 추출
//        String refreshTokenHeader = setCookieHeaders.get(0);
//        String[] cookieParts = refreshTokenHeader.split(";"); // 쿠키 헤더를 세미콜론으로 분할하여 배열에 저장
//        String refreshTokenValue = null;
//        for (String cookiePart : cookieParts) {
//            if (cookiePart.trim().startsWith("refreshToken=")) { // refreshToken으로 시작하는 부분을 찾음
//                refreshTokenValue = cookiePart.trim().substring("refreshToken=".length()); // refreshToken= 다음의 값을 추출
//                break;
//            }
//        }
//
//        assertThat(setCookieHeaders).isNotNull();
//        assertThat(setCookieHeaders).hasSize(1);
//        assertThat(refreshTokenValue).isEqualTo(refreshToken_Jwt);
//    }
}