package place.skillexchange.backend.auth.services;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import place.skillexchange.backend.user.dto.UserDto;

import java.io.IOException;
import java.util.Map;

public interface AuthService {

    public UserDto.SignUpInResponse register(UserDto.SignUpRequest dto, BindingResult bindingResult) throws MethodArgumentNotValidException, MessagingException, IOException;

    public boolean validateDuplicateMember(UserDto.SignUpRequest dto, BindingResult bindingResult);


    public UserDto.ResponseBasic activation(Map<String, String> requestBody);

    void updateUserActiveStatus(String id);

    public ResponseEntity<UserDto.SignUpInResponse> login(UserDto.SignInRequest dto);

    public UserDto.ResponseBasic withdraw(HttpServletRequest request, HttpServletResponse response);
}
