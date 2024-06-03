package place.skillexchange.backend.notice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import place.skillexchange.backend.comment.entity.Comment;
import place.skillexchange.backend.comment.entity.DeleteStatus;
import place.skillexchange.backend.comment.repository.CommentRepository;
import place.skillexchange.backend.file.repository.FileRepository;
import place.skillexchange.backend.notice.entity.Notice;
import place.skillexchange.backend.notice.repository.NoticeRepository;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.user.repository.UserRepository;

import java.util.stream.IntStream;

@SpringBootTest
public class NoticeAndCommentDeleteTest {


    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FileRepository fileRepository;

    @BeforeEach
    @DisplayName("더미 공지사항 댓글 생성")
    public void 더미_공지사항_댓글_생성() {
        User user = userRepository.findById("admin").get();

        Notice notice = Notice.builder()
                .title("필독! 새로운 이벤트가 시작됩니다!")
                .content("참여하고 풍성한 보상을 받아가세요! ")
                .writer(user)
                .hit(0L)
                .build();
        noticeRepository.save(notice);

        IntStream.rangeClosed(1, 10).forEach(i -> {
            Comment comment = Comment.builder()
                    .content("댓글 내용"+i)
                    .isDeleted(DeleteStatus.N)
                    .writer(user)
                    .notice(notice)
                    .build();
            commentRepository.save(comment);
        });
    }

    @DisplayName("todo Delete Test")
    @Test
    @Commit
    @Transactional
    void DELETE_TEST() {
        commentRepository.removeParentRelationForChildCommentsByNoticeId(265L);
        commentRepository.deleteParentCommentsByNoticeId(265L);
        noticeRepository.deleteById(265L);
    }
}
