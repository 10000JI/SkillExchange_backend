package place.skillexchange.backend.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import place.skillexchange.backend.notice.entity.Notice;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice,Long>, CustomNoticeRepository {
    Optional<Notice> findById(Long noticeId);

    void deleteById(Long noticeId);

}

