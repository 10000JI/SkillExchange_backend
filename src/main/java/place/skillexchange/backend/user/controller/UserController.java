package place.skillexchange.backend.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import place.skillexchange.backend.auth.services.AuthService;
import place.skillexchange.backend.user.dto.UserDto;
import place.skillexchange.backend.common.service.MailService;
import place.skillexchange.backend.user.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user/")
@Slf4j
@Tag(name = "user-controller", description = "일반 사용자를 위한 컨트롤러입니다.")
public class UserController {

    private final AuthService authService;
    private final MailService mailService;
    private final UserService userService;

    /**
     * 회원가입
     */
    @PostMapping("/signUp")
    @Operation(summary = "사용자 회원가입 API", description = "사용자 ID, 이메일, 패스워드만 가지고 회원가입을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공")
    })
    public ResponseEntity<UserDto.SignUpInResponse> register(@Validated @RequestBody UserDto.SignUpRequest dto, BindingResult bindingResult) throws MethodArgumentNotValidException, MessagingException, IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto, bindingResult));
    }


    /**
     * active Token (계정 활성화 토큰) 검증
     */
    @Operation(summary = "active Token (계정 활성화 토큰) 검증 API", description = "회원가입 한 사용자가 로그인이 가능하도록 계정 활성화 토큰 검증")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "계정 활성화 완료")
    })
    @PostMapping("/activation")
    public UserDto.ResponseBasic activation(@RequestBody Map<String, String> requestBody) {
        return authService.activation(requestBody);
    }

    /**
     * 사용자 로그인
     */
    @Operation(summary = "사용자 로그인 API", description = "활성화 토큰 검증 이후 로그인이 가능하다.")
    @PostMapping("/signIn")
    public UserDto.SignUpInResponse login(@RequestBody UserDto.SignInRequest dto, HttpServletRequest request,
                                                          HttpServletResponse response) {
        return authService.login(dto,request,response);
    }

    /**
     * 아이디 찾기
     */
    @Operation(summary = "아이디 찾기 API", description = "사용자 이메일로 회원가입 했던 ID를 보내준다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공")
    })
    @PostMapping("/emailToFindId")
    public UserDto.ResponseBasic emailToFindId(@Parameter @RequestBody UserDto.EmailRequest dto) throws MessagingException, IOException {
        mailService.getEmailToFindId(dto.getEmail());
        return new UserDto.ResponseBasic(200, "이메일이 성공적으로 전송되었습니다.");
    }

    /**
     * 비밀번호 찾기
     */
    @Operation(summary = "비밀번호 찾기 API", description = "사용자 이메일로 임시 비밀번호를 보내준다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공")
    })
    @PostMapping("/emailToFindPw")
    public UserDto.ResponseBasic emailToFindPw(@RequestBody UserDto.EmailRequest dto) throws MessagingException, IOException {
        mailService.getEmailToFindPw(dto.getEmail());
        return new UserDto.ResponseBasic(200, "이메일이 성공적으로 전송되었습니다.");
    }

    /**
     * 프로필 수정
     */
    @Operation(summary = "프로필 수정 API", description = "사용자가 입력/수정 하고 싶은 필드만 보내준다. (NULL 가능)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공")
    })
    @PatchMapping("/profileUpdate")
    public UserDto.ProfileResponse profileUpdate(@RequestPart("profileDto") UserDto.ProfileRequest dto, @RequestPart(value="imgFile", required = false) MultipartFile multipartFile) throws IOException {
        return userService.profileUpdate(dto, multipartFile);
    }

    /**
     * 프로필 조회
     */
    @Operation(summary = "프로필 조회 API", description = "엑세스 토큰을 보내주면 검증 이후 프로필 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공")
    })
    @GetMapping("/userInfo")
    public UserDto.MyProfileResponse profileRead() {
        return userService.profileRead();
    }

    /**
     * 비밀번호 변경
     */
    @Operation(summary = "비밀번호 변경 API", description = "임시비밀번호 발급 이후 비밀번호 변경, 보안을 위해 비밀번호 변경을 진행한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공")
    })
    @PostMapping("/updatePw")
    public UserDto.ResponseBasic updatePw(@Validated @RequestBody UserDto.UpdatePwRequest dto, BindingResult bindingResult) throws MethodArgumentNotValidException {
        return userService.updatePw(dto, bindingResult);
    }

    /**
     * 내가 스크랩한 게시물 목록 확인
     */
    @GetMapping("/scrap")
    public List<UserDto.MyScrapResponse> scrapRead() {
        return userService.scrapRead();
    }

    /**
     * 로그아웃
     */
    @PatchMapping("/logout")
    public UserDto.ResponseBasic logout(HttpServletRequest request) {
        return authService.logout(request);
    }

    /**
     * 회원탈퇴
     */
    @PostMapping("/withdraw")
    public UserDto.ResponseBasic withdraw() {
        return authService.withdraw();
    }
}
