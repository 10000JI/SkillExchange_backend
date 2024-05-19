package place.skillexchange.backend.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.file.service.FileServiceImpl;
import place.skillexchange.backend.user.dto.UserDto;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.user.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private FileServiceImpl fileHandler;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    @DisplayName("프로필 수정 테스트")
    public void testProfileUpdate() throws IOException {
        // given
        // 테스트에 필요한 데이터 설정
        UserDto.ProfileRequest profileRequest = UserDto.ProfileRequest.builder()
                .gender("FEMALE")
                .careerSkills("mySkill")
                .preferredSubject("guitar")
                .mySubject("Programming").build();// 필요한 프로파일 요청 정보 생성

        MultipartFile multipartFile = mock(MultipartFile.class); // MultipartFile 모킹

        // 현재 인증된 사용자 설정
        Authentication authentication = new TestingAuthenticationToken("testUser", null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // UserRepository의 findById() 메서드를 모킹하여 반환값 설정
        given(userRepository.findById(authentication.getPrincipal().toString())).willReturn(Optional.of(new User()));

        // fileHandler.uploadFilePR() 메서드의 반환값 설정
        given(fileHandler.uploadFilePR(any(MultipartFile.class), any(User.class))).willReturn(new File());

        // when
        // 프로필 수정 서비스 메서드 호출
        UserDto.ProfileResponse result = userService.profileUpdate(profileRequest, multipartFile);

        // then
        // 결과 검증
        assertThat(result.getReturnMessage()).isEqualTo("프로필이 성공적으로 변경되었습니다.");
    }

    @Test
    @DisplayName("프로필 조회 테스트")
    public void testProfileRead_Success() {
        // Given
        // 현재 인증된 사용자 설정
        Authentication authentication = new TestingAuthenticationToken("testUser", null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = User.builder().id(authentication.getPrincipal().toString()).build();
        given(userRepository.findById(authentication.getPrincipal().toString()))
                .willReturn(Optional.of(user));

        // When
        UserDto.MyProfileResponse dto = userService.profileRead();

        // Then
        assertThat(dto.getId()).isEqualTo(authentication.getPrincipal().toString());
    }

    @Test
    @DisplayName("비밀번호 변경 실패: 현재 비밀번호가 일치하지 않는 경우")
    public void testUpdatePassword_NewPasswordNotMatch() {
        // Given
        // 테스트에 필요한 입력 데이터와 사용자 인증 정보를 설정
        UserDto.UpdatePwRequest request = new UserDto.UpdatePwRequest("wrongPassword", "newPassword", "newPassword");
        BindingResult bindingResult = mock(BindingResult.class);

        // 현재 인증된 사용자 정보를 설정
        Authentication authentication = new TestingAuthenticationToken("testUser", null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // UserRepository의 findById() 메서드를 모킹하여 사용자 객체를 반환하도록 설정
        User user = User.builder().id(authentication.getPrincipal().toString()).password("encodedPassword").build();
        given(userRepository.findById(authentication.getPrincipal().toString()))
                .willReturn(Optional.of(user));
        // 사용자의 실제 비밀번호와 입력한 현재 비밀번호가 일치하지 않도록 설정
        given(passwordEncoder.matches(request.getPassword(), user.getPassword())).willReturn(false);

        // When
        // 비밀번호 업데이트 서비스 메서드를 호출
        Throwable thrown = catchThrowable(() -> userService.updatePw(request, bindingResult));

        // Then
        // 예외가 발생하는지 확인, 만약 예외가 발생하지 않으면 테스트는 실패
        assertThat(thrown).isInstanceOf(MethodArgumentNotValidException.class);
        // 예외가 발생했을 때, 예상대로 메서드에서 예외를 던지는지 확인
        verify(bindingResult).rejectValue("password", "user.nowPassword.notEqual");
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    public void testUpdatePassword_Success() throws MethodArgumentNotValidException {
        // Given
        String currentPassword = "currentPassword";
        String newPassword = "newPassword";
        String encodedNewPassword = passwordEncoder.encode(newPassword);// 새로운 비밀번호 해시화

        UserDto.UpdatePwRequest request = new UserDto.UpdatePwRequest(currentPassword, newPassword, newPassword);
        BindingResult bindingResult = mock(BindingResult.class);

        Authentication authentication = new TestingAuthenticationToken("testUser", null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = User.builder().id(authentication.getPrincipal().toString()).password("encodedPassword").build();
        given(userRepository.findById(authentication.getPrincipal().toString()))
                .willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getPassword(), user.getPassword())).willReturn(true);

        // When
        UserDto.ResponseBasic response = userService.updatePw(request, bindingResult);

        // Then
        assertThat(response.getReturnCode()).isEqualTo(200);
        assertThat(response.getReturnMessage()).isEqualTo(authentication.getPrincipal().toString() + "님의 비밀번호가 변경되었습니다.");
        // 변경된 비밀번호 확인
        assertThat(user.getPassword()).isEqualTo(encodedNewPassword);
    }
}