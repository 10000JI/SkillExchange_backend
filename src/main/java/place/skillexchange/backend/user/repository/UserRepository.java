package place.skillexchange.backend.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import place.skillexchange.backend.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    //이메일 찾기
    Optional<User> findByEmail(String email);

    //활성화된 사용자(계정) 반환
    User findByIdAndActiveIsTrue(String id);

    //email와 id가 일치하는 활성화되지 않은 사용자
    Optional<User> findByEmailAndIdAndActiveIsFalse(String email, String id);


}
