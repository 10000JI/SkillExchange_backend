package place.skillexchange.backend.notice.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import place.skillexchange.backend.notice.entity.Notice;
import place.skillexchange.backend.notice.repository.NoticeRepository;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.user.repository.UserRepository;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
class CustomNoticeRepositoryImplTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("더미 공지사항 생성")
    public void 더미_공지사항_생성() {
        Optional<User> userOptional = userRepository.findById("admin");

        userOptional.ifPresent(user -> {
            IntStream.rangeClosed(2, 20).forEach(i -> {
                StringBuilder titleBuilder = new StringBuilder();
                titleBuilder.append("필독! 새로운 이벤트가 시작됩니다!");

                StringBuilder contentBuilder = new StringBuilder();
                contentBuilder.append("안녕하세요, 스킬교환 회원 여러분! ");
                contentBuilder.append("이번 주에 새로운 이벤트가 시작됩니다. ");
                contentBuilder.append("참여하고 풍성한 보상을 받아가세요! ");
                contentBuilder.append("자세한 내용은 이벤트 게시판에서 확인해주세요.");

                Notice notice = Notice.builder()
                        .title(titleBuilder.toString())
                        .content(contentBuilder.toString())
                        .writer(user)
                        .hit(0L)
                        .build();
                noticeRepository.save(notice);
            });
        });
    }
}