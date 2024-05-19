package place.skillexchange.backend.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import place.skillexchange.backend.exception.board.BoardNotFoundException;
import place.skillexchange.backend.notice.dto.NoticeDto;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.notice.entity.Notice;
import place.skillexchange.backend.file.service.FileServiceImpl;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.exception.user.WriterAndLoggedInUserMismatchExceptionAll;
import place.skillexchange.backend.exception.user.UserNotFoundException;
import place.skillexchange.backend.notice.repository.NoticeRepository;
import place.skillexchange.backend.user.repository.UserRepository;
import place.skillexchange.backend.common.util.SecurityUtil;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{

    private final SecurityUtil securityUtil;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final FileServiceImpl fileService;

    /**
     * 공지사항 등록
     */
    @Override
    @Transactional
    public NoticeDto.NoticeRegisterResponse register(NoticeDto.NoticeRegisterRequest dto, List<MultipartFile> multipartFiles) throws IOException {
        String id = securityUtil.getCurrentMemberUsername();
        User user = userRepository.findById(id).orElseThrow(() -> UserNotFoundException.EXCEPTION);
        if (!Objects.equals(id, dto.getWriter())) {
            throw WriterAndLoggedInUserMismatchExceptionAll.EXCEPTION;
        }
        Notice notice = noticeRepository.save(dto.toEntity(user));

        List<File> files = null;
        System.out.println("MultiPartFiles: "+multipartFiles);
        if (multipartFiles != null) {
            files = fileService.registerNoticeImg(multipartFiles,notice);
        }

        return new NoticeDto.NoticeRegisterResponse(user, files , notice,201,"공지가 등록되었습니다.");
    }

    /**
     * 공지사항 조회
     */
    @Override
    @Transactional
    public NoticeDto.NoticeReadResponse read(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> BoardNotFoundException.EXCEPTION);
        notice.updateHit(); //update가 발생하므로 @Transactional
        return new NoticeDto.NoticeReadResponse(notice);
    }


    /**
     * 공지사항 업데이트
     */
    @Override
    @Transactional
    public NoticeDto.NoticeUpdateResponse update(NoticeDto.NoticeUpdateRequest dto, List<MultipartFile> multipartFiles, Long noticeId) throws IOException {
        String id = securityUtil.getCurrentMemberUsername();
        User user = userRepository.findById(id).orElseThrow(() -> UserNotFoundException.EXCEPTION);
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> BoardNotFoundException.EXCEPTION);

        if (!Objects.equals(id, dto.getWriter()) || !Objects.equals(id, notice.getWriter().getId()) || !Objects.equals(dto.getWriter(), notice.getWriter().getId())) {
            throw WriterAndLoggedInUserMismatchExceptionAll.EXCEPTION;
        }

        notice.changeNotice(dto);

        List<File> files = fileService.updateNoticeImg(dto.getImgUrl(), multipartFiles, notice);

        return new NoticeDto.NoticeUpdateResponse(user, files , notice,200,"공지가 수정되었습니다.");
    }

    /**
     * 공지사항 삭제
     */
    @Override
    @Transactional
    //Notice와 File은 양방향 매핑으로 Notice가 삭제되면 File도 삭제되도록 Cascade 설정을 했기 때문에 @Transactional이 필요
    //Notice와 Comment은 양방향 매핑으로 Notice가 삭제되면 Comment도 삭제되도록 Cascade 설정을 했기 때문에 @Transactional이 필요
    public NoticeDto.ResponseBasic delete(Long noticeId) {
        String id = securityUtil.getCurrentMemberUsername();
        Optional<Notice> deletedNotice = noticeRepository.findById(noticeId);
        if (deletedNotice.isPresent()) {
            if (!Objects.equals(id, deletedNotice.get().getWriter().getId())) {
                throw WriterAndLoggedInUserMismatchExceptionAll.EXCEPTION;
            }
            noticeRepository.deleteById(noticeId);
//            fileService.deleteNoticeImg(deletedNotice.get());
            return new NoticeDto.ResponseBasic(200, "공지사항이 성공적으로 삭제되었습니다.");
        } else {
            throw BoardNotFoundException.EXCEPTION;
        }
    }

    /**
     * 공지사항 목록
     */
    @Override
    public Page<NoticeDto.NoticeListResponse> getNotices(int limit, int skip, String keyword) {
        Pageable pageable = PageRequest.of(skip, limit);
        return noticeRepository.findNoticesWithPagingAndKeyword(keyword, pageable);
    }
}
