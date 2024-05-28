package place.skillexchange.backend.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import place.skillexchange.backend.user.entity.User;

import java.util.Date;

@Entity
@Table(name = "refreshToken")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tokenId;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private Date expirationTime;

    /**
     * User와 RefreshToken Lazy 로딩
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    /**
     * 로그인 시 리프레시 토큰 만료일자 변경
     */
    public void changeRefreshTokenExp(Date expirationTime, String refreshToken) {
        this.expirationTime = expirationTime;
        this.refreshToken = refreshToken;
    }

}
