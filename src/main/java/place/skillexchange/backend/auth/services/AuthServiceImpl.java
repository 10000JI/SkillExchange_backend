package place.skillexchange.backend.auth.services;


import jakarta.mail.MessagingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.client.RestTemplate;
import place.skillexchange.backend.comment.entity.Comment;
import place.skillexchange.backend.comment.repository.CommentRepository;
import place.skillexchange.backend.common.util.RedisUtil;
import place.skillexchange.backend.common.util.SecurityUtil;
import place.skillexchange.backend.exception.user.SocialLoginRequriedException;
import place.skillexchange.backend.exception.user.UserIdLoginException;
import place.skillexchange.backend.file.repository.FileRepository;
import place.skillexchange.backend.talent.entity.Talent;
import place.skillexchange.backend.talent.repository.TalentRepository;
import place.skillexchange.backend.user.dto.UserDto;
import place.skillexchange.backend.user.entity.Refresh;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.exception.user.UserNotFoundException;
import place.skillexchange.backend.exception.user.UserTokenExpriedException;
import place.skillexchange.backend.user.repository.RefreshRepository;
import place.skillexchange.backend.user.repository.UserRepository;
import place.skillexchange.backend.common.service.MailService;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final UserDetailsService userDetailsService;
    private final RefreshRepository refreshRepository;
    private final SecurityUtil securityUtil;
    private final RedisUtil redisUtil;
    private final TalentRepository talentRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;

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
        Optional<User> userOptional = userRepository.findByEmailAndId(dto.getEmail(), dto.getId());
        if (!userOptional.isPresent()) {
            // id가 DB에 있는 경우
            Optional<User> userById = userRepository.findById(dto.getId());
            if (userById.isPresent()) {
                bindingResult.rejectValue("id", "user.id.notEqual");
                checked = true;
            }
            // email이 DB에 있는 경우
            Optional<User> userByEmail = userRepository.findByEmail(dto.getEmail());
            if (userByEmail.isPresent()) {
                bindingResult.rejectValue("email", "user.email.notEqual");
                checked = true;
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
    public UserDto.SignUpInResponse login(UserDto.SignInRequest dto, HttpServletRequest request,
                                                          HttpServletResponse response) {
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
        response.setHeader("Authorization", "Bearer " + accessToken);

        //RefreshToken 생성 (이미 있어도 덮어쓰기 가능)
        Refresh redis = Refresh.builder()
                .refreshToken(UUID.randomUUID().toString())
                .userId(user.getId())
                .build();
        refreshRepository.save(redis);

        Cookie cookie = new Cookie("refreshToken", redis.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        // 2주 후 만료일 설정
        cookie.setMaxAge(60 * 60 * 24 * 14); // 초 단위로 설정
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "None"); //쿠키에 samesite 속성 추가

        response.addCookie(cookie);
        return new UserDto.SignUpInResponse(user, 200, "로그인 성공!");
    }

    //로그아웃
    @Override
    public UserDto.ResponseBasic logout(HttpServletRequest request) {
        String id = invalidateToken(request);

        /* oauth2 access 토큰 삭제 */
        if (redisUtil.getValues("AT(oauth2):" + id) != null) {
            String socialAccessToken = (String) redisUtil.getValues("AT(oauth2):" + id);
            int underscoreIndex = id.indexOf("_");
            if (underscoreIndex != -1) {
                String socialType = id.substring(0, underscoreIndex);
                if (socialType.equals("google")) {
                    googleLogout(socialAccessToken);
                } else if (socialType.equals("kakao")) {
                    kakaoLogout(socialAccessToken);
                }
            }
            redisUtil.deleteValues("AT(oauth2):" + id);
        }


        return new UserDto.ResponseBasic(200, "로그아웃 되었습니다.");
    }

    // 액세스/리프레시 토큰 무효화
    private String invalidateToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Date date = jwtService.extractExpiration(token);
        Long now = new Date().getTime();
        Long expiration = date.getTime() - now;
        String id = securityUtil.getCurrentMemberUsername();

        // 엑세스 토큰 블랙리스트 관리
        redisUtil.setBlackList(token, "logout", Duration.ofMillis(expiration));

        // 리프레시 토큰 삭제
        if (refreshRepository.findById(id).isPresent()) {
            refreshRepository.deleteById(id);
        }
        return id;
    }

    @Override
    @Transactional
    public UserDto.ResponseBasic withdraw(HttpServletRequest request) {
        String id = SecurityUtil.getCurrentMemberUsername();

        /* 카카오 및 구글 연결 해제 */
        int underscoreIndex = id.indexOf("_");
        if (underscoreIndex != -1) {
            String socialType = id.substring(0, underscoreIndex);
            if (socialType.equals("google")) {
                googleUnlink(id, request);
            } else if (socialType.equals("kakao")) {
                kakaoUnlink(id, request);
            }
        }

        if (refreshRepository.findById(id).isPresent()) { //리프레시 토큰 삭제
            refreshRepository.deleteById(id);
        }

        // 사용자의 모든 게시물(Talent)에 대한 댓글 관계 제거 및 삭제
        List<Talent> userTalents = talentRepository.findByWriterId(id);
        for (Talent talent : userTalents) {
            commentRepository.removeParentRelationForChildCommentsByTalentId(talent.getId());
            commentRepository.deleteParentCommentsByTalentId(talent.getId());
        }

        // 사용자가 올린 게시물 이미지 삭제
        for (Talent talent : userTalents) {
            fileRepository.deleteByTalentId(talent.getId());
        }

        // 사용자의 게시물 삭제
        talentRepository.deleteByWriterId(id);

        // 사용자가 작성한 댓글은 null로 변경 (재능교환 게시물은 삭제하나 댓글은 삭제하지 않음)
        commentRepository.nullifyWriterByUserId(id);

        userRepository.deleteById(id);

        return new UserDto.ResponseBasic(200, "회원 탈퇴가 정상적으로 처리되었습니다.");
    }

    public void kakaoLogout(String access_Token) {
        String reqURL = "https://kapi.kakao.com/v1/user/logout";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void googleLogout(String accessToken) {
        String tokenInfoUrl = "https://oauth2.googleapis.com/tokeninfo?access_token=" + accessToken;
        try {
            URL url = new URL(tokenInfoUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            System.out.println("Google Logout Response Code: " + responseCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kakaoUnlink(String id, HttpServletRequest request) {
        String accessToken = (String) redisUtil.getValues("AT(oauth2):" + id);
        // oauth2 토큰이 만료 시 재 로그인
        if (accessToken == null) {
            invalidateToken(request);
            throw SocialLoginRequriedException.EXCEPTION;
        } else {
            redisUtil.deleteValues("AT(oauth2):" + id);
        }

        String reqURL = "https://kapi.kakao.com/v1/user/unlink";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void googleUnlink(String id, HttpServletRequest request) {
        String accessToken = (String) redisUtil.getValues("AT(oauth2):" + id);
        // oauth2 토큰이 만료 시 재 로그인
        if (accessToken == null) {
            invalidateToken(request);
            throw SocialLoginRequriedException.EXCEPTION;
        } else {
            redisUtil.deleteValues("AT(oauth2):" + id);
        }
        String tokenInfoUrl = "https://oauth2.googleapis.com/revoke";
        try {
            URL url = new URL(tokenInfoUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            String postData = "token=" + accessToken;
            byte[] postDataBytes = postData.getBytes("UTF-8");

            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postDataBytes);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Google Unlink Response Code: " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
