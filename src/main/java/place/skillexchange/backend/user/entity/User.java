package place.skillexchange.backend.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import place.skillexchange.backend.talent.entity.GenderForTalent;
import place.skillexchange.backend.user.dto.UserDto;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.talent.entity.TalentScrap;

import java.util.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class User implements UserDetails {

    @Id
    @Column(name = "user_id", length = 50, unique = true, nullable = false)
    private String id;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "email", length = 50, nullable = false)
    private String email;

    @Column(name = "active")
    @ColumnDefault("0")
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(length = 6)
    private Gender gender;

    @Column(name = "job", length = 50)
    private String job;

    @Column(name = "career_skills", length = 100)
    private String careerSkills;

    @Column(name = "preferred_subject", length = 50)
    private String preferredSubject;

    @Column(name = "my_subject", length = 50)
    private String mySubject;


    /**
     * Security에서 권한 정보 로드할 때 LAZY(지연)로딩이 아니라 EAGER(즉시)로딩으로 설정해야 함
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
    private Set<Authority> authorities;

//    /**
//     * User와 RefreshToken은 1:1 관계
//     */
//    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
//    private RefreshToken refreshToken;

    /**
     * User와 File은 1:1 관계
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private File file;

    /**
     * User와 TalentScrap 양방향 매핑
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Set<TalentScrap> talentScraps = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthorityName()));
        }
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return id;
    }

    /**
     * active 컬럼 0->1 변경
     */
    public void changeActive(boolean active) {
        this.active = active;
    }

    /**
     * 임시 password
     */
    public void changePw(String password) {
        this.password = password;
    }

    /**
     * 프로필 수정
     */
    public void changeProfileField(UserDto.ProfileRequest dto) {
        this.gender = Gender.valueOf(dto.getGender());
        this.job = dto.getJob();
        this.careerSkills = dto.getCareerSkills();
        this.preferredSubject = dto.getPreferredSubject();
        this.mySubject = dto.getMySubject();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
