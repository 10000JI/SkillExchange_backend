package place.skillexchange.backend.auth.services;


import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import place.skillexchange.backend.exception.user.UserIdLoginException;
import place.skillexchange.backend.user.dto.UserDto;
import place.skillexchange.backend.user.entity.RefreshToken;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.exception.user.UserNotFoundException;
import place.skillexchange.backend.exception.user.UserTokenExpriedException;
import place.skillexchange.backend.user.repository.UserRepository;
import place.skillexchange.backend.common.service.MailService;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final UserDetailsService userDetailsService;

    /* 회원가입 ~ 로그인 까지 (JWT 생성) */

    /**
     * 회원가입
     */
    @Override
    public UserDto.SignUpInResponse register(UserDto.SignUpRequest dto, BindingResult bindingResult) throws MethodArgumentNotValidException, MessagingException, IOException {

        boolean isValid = validateDuplicateMember(dto, bindingResult);
        if (isValid) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }

        dto.setPassword(passwordEncoder.encode(dto.getPassword()));

        //user 저장
        User user = userRepository.save(dto.toEntity());

        //5분 뒤 회원의 active가 0이라면 db에서 회원 정보 삭제 (active 토큰 만료일에 맞춰서)
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 5분 후에 실행될 작업
                if (userRepository.findByIdAndActiveIsTrue(user.getId()) == null) {
                    userRepository.delete(user);
                }
                timer.cancel(); // 작업 완료 후 타이머 종료
            }
        }, 5 * 60 * 1000); // 5분 후

        String activeToken = jwtService.generateActiveToken(user);
        //active Token (계정 활성화 토큰) 발급
        mailService.getEmail(dto.getEmail(), dto.getId(), activeToken);

        return new UserDto.SignUpInResponse(user, 201, "이메일(" + dto.getEmail() + ")을 확인하여 회원 활성화를 완료해주세요.");
    }


    /**
     * 회원가입 검증
     */
    @Override
    @Transactional
    public boolean validateDuplicateMember(UserDto.SignUpRequest dto, BindingResult bindingResult) {

        boolean checked = false;
        //checked가 true면 검증 발견
        //checked가 false면 검증 미발견

        checked = bindingResult.hasErrors();

        //방법1. 동일 id와 email만 계속해서 접근 가능 , 동일 id나 email이 다르면 접근 불가능 / 동일 email이나 id가 다르면 접근 불가능 (유효성검사)
        //active가 0이고, id와 email이 db에 있는 경우엔 if문을 건너뛴다.
        Optional<User> userOptional = userRepository.findByEmailAndIdAndActiveIsFalse(dto.getEmail(), dto.getId());
        if (!userOptional.isPresent()) {
            //id가 db에 있는 경우 if문 실행
            if(userRepository.findById(dto.getId()) != null) {
                //id 중복 검증
                Optional<User> byId = userRepository.findById(dto.getId());
                if (!byId.isEmpty()) {
                    bindingResult.rejectValue("id", "user.id.notEqual");
                    checked = true;
                }
            }
            //email이 db에 있는 경우 if문 실행
            if(userRepository.findByEmail(dto.getEmail()) != null) {

                //email 중복 검증
                Optional<User> userEmail = userRepository.findByEmail(dto.getEmail());
                if (userEmail.isPresent()) {
                    bindingResult.rejectValue("email", "user.email.notEqual");
                    checked = true;
                }
            }
        }

        //password 일치 검증
        if (!dto.getPasswordCheck().equals(dto.getPassword())) {
            bindingResult.rejectValue("passwordCheck", "user.password.notEqual");
            checked = true;
        }

        return checked;
    }

    /**
     * activeToken 발급
     */
    @Override
    @Transactional
    public UserDto.ResponseBasic activation(Map<String, String> requestBody) {
        String activeToken = requestBody.get("activeToken");
        String id = jwtService.extractUsername(activeToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(id);
        // 여기서 activeToken을 검증하고 처리하는 로직을 추가
        //isTokenValid가 false일때 토큰 만료 exception이 출려되어야 함 !!!
        if (!jwtService.isActiveTokenValid(activeToken, userDetails)) {
            // 토큰이 유효하지 않은 경우 예외를 발생시킴
            throw UserTokenExpriedException.EXCEPTION;
        }

        // active 0->1 로 변경 (active가 1이여야 로그인 가능)
        updateUserActiveStatus(id);

        return new UserDto.ResponseBasic(200, "계정이 활성화 되었습니다.");
    }

    /**
     * active 컬럼 0->1 변경
     */
    @Transactional
    @Override
    public void updateUserActiveStatus(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> UserNotFoundException.EXCEPTION);
        user.changeActive(true);
        //userRepository.save(user);
    }

    /**
     * 로그인
     */
    @Override
    public ResponseEntity<UserDto.SignUpInResponse> login(UserDto.SignInRequest dto) {
        try {
            //authenticationManager가 authenticate() = 인증한다.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getId(),
                            dto.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            // 잘못된 아이디 패스워드 입력으로 인한 예외 처리
            throw UserIdLoginException.EXCEPTION;
        }

        //유저의 아이디 및 계정활성화 유무를 가지고 유저 객체 조회
        User user = userRepository.findByIdAndActiveIsTrue(dto.getId());
        if (user == null) {
            throw UserIdLoginException.EXCEPTION;
        }

        //accessToken 생성
        String accessToken = jwtService.generateAccessToken(user);
        //refreshToken 생성
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(dto.getId());

        // 헤더에 access 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        //쿠키에 refresh 토큰 추가
        ResponseCookie responseCookie = ResponseCookie
                .from("refreshToken", refreshToken.getRefreshToken())
                .secure(true)
                .httpOnly(true)
                .path("/")
                .sameSite("None")
                .build();
        headers.add(HttpHeaders.SET_COOKIE, responseCookie.toString());

        // ResponseEntity에 헤더만 설정하여 반환
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(new UserDto.SignUpInResponse(user, 200, "로그인 성공!"));
    }

    @Override
    public UserDto.ResponseBasic withdraw(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }
}
