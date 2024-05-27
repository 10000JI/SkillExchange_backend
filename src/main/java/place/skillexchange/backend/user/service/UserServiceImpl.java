package place.skillexchange.backend.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;
import place.skillexchange.backend.exception.board.BoardNotFoundException;
import place.skillexchange.backend.exception.user.ScrapNotFoundException;
import place.skillexchange.backend.file.service.FileServiceImpl;
import place.skillexchange.backend.talent.entity.TalentScrap;
import place.skillexchange.backend.talent.repository.TalentRepository;
import place.skillexchange.backend.talent.repository.TalentScrapRepository;
import place.skillexchange.backend.user.dto.UserDto;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.exception.user.UserNotFoundException;
import place.skillexchange.backend.user.repository.UserRepository;
import place.skillexchange.backend.common.util.SecurityUtil;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileServiceImpl fileHandler;
    private final TalentScrapRepository scrapRepository;
    private final TalentRepository talentRepository;

    /**
     * 프로필 수정
     */
    @Override
    @Transactional
    public UserDto.ProfileResponse profileUpdate(UserDto.ProfileRequest dto, MultipartFile multipartFile) throws IOException {
        String id = securityUtil.getCurrentMemberUsername();
        User user = userRepository.findById(id).orElseThrow(() -> UserNotFoundException.EXCEPTION);
        user.changeProfileField(dto);

        File file = null;
        if (multipartFile != null) {
           file  = fileHandler.uploadFilePR(multipartFile, user);
        }

        return new UserDto.ProfileResponse(user, file,200, "프로필이 성공적으로 변경되었습니다.");
    }

    /**
     * 프로필 조회
     */
    @Override
    public UserDto.MyProfileResponse profileRead() {
        String id = securityUtil.getCurrentMemberUsername();
        User user = userRepository.findById(id).orElseThrow(() -> UserNotFoundException.EXCEPTION);

        return new UserDto.MyProfileResponse(user, 200, id+"님의 프로필");
    }

    /**
     * 비밀번호 변경
     */
    @Override
    @Transactional
    public UserDto.ResponseBasic updatePw(UserDto.UpdatePwRequest dto, BindingResult bindingResult) throws MethodArgumentNotValidException {
        String id = securityUtil.getCurrentMemberUsername();
        User user = userRepository.findById(id).orElseThrow(() -> UserNotFoundException.EXCEPTION);

        boolean checked = false;

        checked = bindingResult.hasErrors();

        //계정의 비밀번호와 내가 입력한 현재 비밀번호와 동일한지 유효성 검사
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            bindingResult.rejectValue("password","user.nowPassword.notEqual");
            checked = true;
        }


        //password 일치 검증
        if (!dto.getNewPassword().equals(dto.getNewPasswordCheck())) {
            bindingResult.rejectValue("newPasswordCheck", "user.newPassword.notEqual");
            checked = true;
        }

        // checked가 true면 유효섬 검사 실패, 에러핸들링 동작
        if (checked) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }

        // 새비밀번호로 변경 (트랜잭션으로 영속성 컨텍스트 속성 이용)
        user.changePw(passwordEncoder.encode(dto.getNewPassword()));

        return new UserDto.ResponseBasic(200, id+"님의 비밀번호가 변경되었습니다.");
    }

    /**
     * 스크랩한 게시물 목록 확인
     */
    @Override
    public List<UserDto.MyScrapResponse> scrapRead() {
        String id = securityUtil.getCurrentMemberUsername();
        List<TalentScrap> talentScrapByIdUserId = scrapRepository.findTalentScrapById_UserId(id);
        List<UserDto.MyScrapResponse> list = new ArrayList<>();
        for (TalentScrap talentScrap : talentScrapByIdUserId) {
            UserDto.MyScrapResponse scrap = new UserDto.MyScrapResponse(talentRepository.findById(talentScrap.getId().getTalentId()).orElseThrow(() -> BoardNotFoundException.EXCEPTION));
            list.add(scrap);
        }
        if (list.isEmpty()) {
            throw ScrapNotFoundException.EXCEPTION;
        }
        return list;
    }
}
