package place.skillexchange.backend.auth.services;


import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import place.skillexchange.backend.exception.user.RefreshTokenExpiredException;
import place.skillexchange.backend.exception.user.RefreshTokenNotFoundException;
import place.skillexchange.backend.user.entity.RefreshToken;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.exception.user.UserNotFoundException;
import place.skillexchange.backend.user.repository.RefreshTokenRepository;
import place.skillexchange.backend.user.repository.UserRepository;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * refreshToken 생성
     */
    @Transactional
    public RefreshToken createRefreshToken(String id) {
        //사용자 이름이 존재하면 User 객체 반환, 없다면 사용자를 찾을 수 없다는 예외
        User user = userRepository.findById(id)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

//        //user의 refreshToken을 가져와 RefreshToken 객체 추출
//        RefreshToken refreshToken = user.getRefreshToken();

        RefreshToken refreshToken = refreshTokenRepository.findRefreshTokenByUser(user).orElseThrow(() -> RefreshTokenNotFoundException.EXCEPTION);

        //refreshToken이 NULL이라면 refreshToken을 새롭게 만든다
        if (refreshToken == null) {
            refreshToken = RefreshToken.builder()
                    //refreshToken은 UUID로 생성
                    .refreshToken(UUID.randomUUID().toString())
                    //만료일은 2분 (실제로는 2주 정도로 설정)
                    .expirationTime(new Date((new Date()).getTime() + /*5 * 60 * 1000*/14 * 24 * 60 * 60 * 1000))
                    .user(user)
                    .build();

            refreshTokenRepository.save(refreshToken);
        } else {
            refreshToken.changeRefreshTokenExp(new Date((new Date()).getTime() +  /*5 * 60 * 1000*/14 * 24 * 60 * 60 * 1000),UUID.randomUUID().toString());
            //refreshTokenRepository.save(refreshToken);
        }

        return refreshToken;
    }

    /**
     * refreshToken 확인
     */
    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken).get(); //필터영역이기에 aop exception 처리 불가능

        //refreshToken의 만료시간이 현재 시간보다 작다면 refreshToken 삭제
        if (refToken.getExpirationTime().compareTo(Date.from(Instant.now())) < 0) {
            refreshTokenRepository.delete(refToken);
        }

        return refToken;
    }
}