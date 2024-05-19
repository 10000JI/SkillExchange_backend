package place.skillexchange.backend.notice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import place.skillexchange.backend.notice.dto.NoticeDto;

public interface CustomNoticeRepository {
    //공지사항 페이징 처리 및 검색어 처리
    Page<NoticeDto.NoticeListResponse> findNoticesWithPagingAndKeyword(String keyword, Pageable pageable);
}
