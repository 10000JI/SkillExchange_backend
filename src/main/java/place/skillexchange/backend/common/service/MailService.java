package place.skillexchange.backend.common.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import place.skillexchange.backend.exception.user.UserEmailNotFoundException;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.common.util.PasswordGeneratorUtil;
import place.skillexchange.backend.user.repository.UserRepository;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class MailService {
    private final TemplateEngine templateEngine;
    private final JavaMailSender emailSender;
    private final UserRepository userRepository;
    private final PasswordGeneratorUtil passwordGeneratorUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String sender;


    public void getEmail(String email, String id, String activeToken) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        //메일 제목 설정
        helper.setSubject("재능교환소 계정 활성화 인증");

        //수신자 설정
        helper.setTo(email);

        //송신자 설정
        helper.setFrom(sender);

        //템플릿에 전달할 데이터 설정
        HashMap<String, String> emailValues = new HashMap<>();
        emailValues.put("id", id);
        emailValues.put("jwtLink", "http://localhost:3000/active/"+activeToken);

        Context context = new Context();
        emailValues.forEach((key, value)->{
            context.setVariable(key, value);
        });

        //메일 내용 설정 : 템플릿 프로세스
        String html = templateEngine.process("email_activation", context);
        helper.setText(html, true);

        /*//템플릿에 들어가는 이미지 cid로 삽입
        helper.addInline("image", new ClassPathResource("static/img/Logo.png"));*/

        // 외부 URL에서 이미지 다운로드
        ByteArrayResource imageResource = getByteArrayResource();
        // CID로 이미지 삽입
        helper.addInline("image", imageResource, "image/png");

        //메일 보내기
        emailSender.send(message);
    }

    public void getEmailToFindId(String email) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        //메일 제목 설정
        helper.setSubject("재능교환소 계정 아이디");

        //수신자 설정
        helper.setTo(email);

        //송신자 설정
        helper.setFrom(sender);

        User user = userRepository.findByEmail(email).orElseThrow(() -> UserEmailNotFoundException.EXCEPTION);
        Context context = new Context();
        context.setVariable("id", user.getId());

        //메일 내용 설정 : 템플릿 프로세스
        String html = templateEngine.process("email_findId", context);
        helper.setText(html, true);

       /* //템플릿에 들어가는 이미지 cid로 삽입
        helper.addInline("image", new ClassPathResource("static/img/Logo.png"));*/

        // 외부 URL에서 이미지 다운로드
        ByteArrayResource imageResource = getByteArrayResource();
        // CID로 이미지 삽입
        helper.addInline("image", imageResource, "image/png");

        //메일 보내기
        emailSender.send(message);
    }

    @Transactional
    public void getEmailToFindPw(String email) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        //메일 제목 설정
        helper.setSubject("재능교환소 계정 임시 비밀번호");

        //수신자 설정
        helper.setTo(email);

        //송신자 설정
        helper.setFrom(sender);

        String newPw = passwordGeneratorUtil.generatePassword();
        User user = userRepository.findByEmail(email).orElseThrow(() -> UserEmailNotFoundException.EXCEPTION);
        user.changePw(passwordEncoder.encode(newPw));
        //userRepository.save(user);
        Context context = new Context();
        context.setVariable("password",newPw);

        //메일 내용 설정 : 템플릿 프로세스
        String html = templateEngine.process("email_findPw", context);
        helper.setText(html, true);

        /*//템플릿에 들어가는 이미지 cid로 삽입
        helper.addInline("image", new ClassPathResource("static/img/Logo.png"));*/

        // 외부 URL에서 이미지 다운로드
        ByteArrayResource imageResource = getByteArrayResource();
        // CID로 이미지 삽입
        helper.addInline("image", imageResource, "image/png");

        //메일 보내기
        emailSender.send(message);
    }

    private static ByteArrayResource getByteArrayResource() throws IOException {
        // 외부 URL에서 이미지 다운로드
        URL imageUrl = new URL("https://skillexchange.s3.ap-northeast-2.amazonaws.com/images/LOGO.png");
        byte[] imageBytes = imageUrl.openStream().readAllBytes();
        ByteArrayResource imageResource = new ByteArrayResource(imageBytes);
        return imageResource;
    }
}
