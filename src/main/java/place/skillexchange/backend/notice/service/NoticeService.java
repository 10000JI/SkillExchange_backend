package place.skillexchange.backend.notice.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import place.skillexchange.backend.notice.dto.NoticeDto;

import java.io.IOException;
import java.util.List;

public interface NoticeService {

    public NoticeDto.NoticeRegisterResponse register(NoticeDto.NoticeRegisterRequest dto, List<MultipartFile> multipartFiles) throws IOException;

    public NoticeDto.NoticeReadResponse read(Long noticeId);

    public NoticeDto.NoticeUpdateResponse update(NoticeDto.NoticeUpdateRequest dto, List<MultipartFile> multipartFile, Long noticeId) throws IOException;


    public NoticeDto.ResponseBasic delete(Long noticeId);

    public Page<NoticeDto.NoticeListResponse> getNotices(int limit, int skip, String keyword);

}
