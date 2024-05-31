package place.skillexchange.backend.notice.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import place.skillexchange.backend.notice.entity.Notice;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice,Long>, CustomNoticeRepository {

    @EntityGraph(attributePaths = {"writer","writer.file","files"})
    Optional<Notice> findWithWriterAndFilesById(Long noticeId);

    @EntityGraph(attributePaths = {"writer","writer.file"})
    Optional<Notice> findWithWriterById(Long noticeId);

    void deleteById(Long noticeId);

    @Query("SELECT COUNT(n) FROM Notice n WHERE n.id = :noticeId")
    Long countByNoticeId(@Param("noticeId") Long noticeId);

}

