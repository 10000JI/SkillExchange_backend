package place.skillexchange.backend.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import place.skillexchange.backend.auth.services.AuthService;
import place.skillexchange.backend.user.controller.UserController;
import place.skillexchange.backend.user.dto.UserDto;
import org.springframework.validation.BindingResult;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// MockitoExtension을 사용하여 Mockito를 확장합니다. Mockito를 사용하여 Mock 객체를 만들고 주입
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    //Mock 객체들을 주입받아 테스트할 대상 객체를 생성
    @InjectMocks
    UserController userController;

    //Mock 객체를 생성
    @Mock
    AuthService authService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    //각 테스트 메서드가 실행되기 전에 실행할 메서드를 지정
    //MockMvc 객체를 초기화
    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("회원가입 테스트")
    void register() throws Exception {
        //given
        UserDto.SignUpRequest req = UserDto.SignUpRequest.builder()
                .id("sksk436")
                .email("sksk436@naver.com")
                .password("12345qwerQWER!")
                .passwordCheck("12345qwerQWER!")
                .build();

        mockMvc.perform(
                        post("/v1/user/signUp")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        verify(authService).register(eq(req), any(BindingResult.class));
    }

//    @Test
//    @DisplayName("계정 활성화 토큰(activeToken) 검증")
//    void activation() throws Exception {
//        //given
//        Map<String, String> requestBody = new HashMap<>();
//        requestBody.put("activeToken", ".EVzBEKmmaPprZlqtYIO7mIK6NzBeOx3DaExuzM1V-Nw");
//
//        mockMvc.perform(
//                        post("/v1/user/activation")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(requestBody)))
//                .andExpect(status().isBadRequest());
//
//        verify(authService, never()).activation(eq(requestBody)); // 활성화 메서드가 호출되지 않았는지 확인
//    }

//    @DisplayName("로그인 테스트")
//    @Test
//    void signInTest() throws Exception {
//        // given
//        UserDto.SignInRequest req = new UserDto.SignInRequest("sksk436", "12345qwerQWER!");
//        given(authService.login(req)).willReturn(new TokenResponseDto("access", "refresh"));
//
//        // when, then
//        mockMvc.perform(
//                        post("/auth/sign-in")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(req)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.result.data.accessToken").value("access"))
//                .andExpect(jsonPath("$.result.data.refreshToken").value("refresh"));
//
//        verify(authService).signIn(req);
//    }
}