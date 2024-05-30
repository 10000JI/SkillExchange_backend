package place.skillexchange.backend.file.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.notice.entity.Notice;
import place.skillexchange.backend.talent.entity.Talent;
import place.skillexchange.backend.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File,Long> {
    Optional<File> findByUser(User user);

    //notice를 가지고 있는 단일 File (프로필)
    Optional<File> findByNotice(Notice notice);

    //notice를 가지고 있는 File들 (게시물)
    List<File> findAllByNotice(Notice notice);

    //talent를 가지고 있는 File들 (게시물)
    List<File> findAllByTalent(Talent talent);

    @Modifying
    @Query("delete from File f where f.notice.id = :noticeId")
    void deleteByNoticeId(@Param("noticeId") Long noticeId);

    @Modifying
    @Query("delete from File f where f.fileUrl = :fileUrl")
    void deleteByFileUrl(@Param("fileUrl") String fileUrl);

}
