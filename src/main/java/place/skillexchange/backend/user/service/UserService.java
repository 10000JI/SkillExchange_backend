package place.skillexchange.backend.user.service;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;
import place.skillexchange.backend.user.dto.UserDto;

import java.io.IOException;

public interface UserService {
    public UserDto.ProfileResponse profileUpdate(UserDto.ProfileRequest dto, MultipartFile multipartFile) throws IOException;

    public UserDto.MyProfileResponse profileRead();

    public UserDto.ResponseBasic updatePw(UserDto.UpdatePwRequest dto, BindingResult bindingResult) throws MethodArgumentNotValidException;

}
