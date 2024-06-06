package place.skillexchange.backend.notice.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import place.skillexchange.backend.comment.repository.CommentRepository;
import place.skillexchange.backend.common.service.RedisService;
import place.skillexchange.backend.common.util.CookieUtil;
import place.skillexchange.backend.exception.board.BoardNotFoundException;
import place.skillexchange.backend.file.repository.FileRepository;
import place.skillexchange.backend.notice.dto.NoticeDto;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.notice.entity.Notice;
import place.skillexchange.backend.file.service.FileServiceImpl;
import place.skillexchange.backend.talent.entity.Talent;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.exception.user.WriterAndLoggedInUserMismatchExceptionAll;
import place.skillexchange.backend.exception.user.UserNotFoundException;
import place.skillexchange.backend.notice.repository.NoticeRepository;
import place.skillexchange.backend.user.repository.UserRepository;
import place.skillexchange.backend.common.util.SecurityUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeServiceImpl implements NoticeService{

    private final SecurityUtil securityUtil;
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final FileServiceImpl fileService;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;
    private final RedisService redisService;
    private final CookieUtil cookieUtil;

    /**
     * 공지사항 등록
     */
    @Override
    @Transactional
    public NoticeDto.NoticeRegisterResponse register(NoticeDto.NoticeRegisterRequest dto, List<MultipartFile> multipartFiles) throws IOException {
        String id = securityUtil.getCurrentMemberUsername();
        User user = userRepository.findWithFileById(id).orElseThrow(() -> UserNotFoundException.EXCEPTION);
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
    public NoticeDto.NoticeReadResponse read(Long noticeId, HttpServletRequest request, HttpServletResponse response) {
        Notice notice = noticeRepository.findWithWriterAndFilesById(noticeId)
                .orElseThrow(() -> BoardNotFoundException.EXCEPTION);
        String id = securityUtil.getCurrentMemberUsernameOrNonMember();
        if (id.equals("non-Member")) {
            Cookie[] cookies = request.getCookies();
            boolean checkCookie = false;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    // 이미 조회를 한 경우 체크
                    if (cookie.getName().equals(cookieUtil.getCookieName(noticeId,notice))) checkCookie = true;
                }
                if (!checkCookie) {
                    Cookie newCookie = cookieUtil.createCookieForForNotOverlap(noticeId,notice);
                    response.addCookie(newCookie);
                    notice.updateHit();
                }
            } else {
                Cookie newCookie = cookieUtil.createCookieForForNotOverlap(noticeId, notice);
                response.addCookie(newCookie);
                notice.updateHit();
            }
        } else {
            if (redisService.isFirstIpRequest(id, noticeId, notice)) {
                log.debug("same user requests duplicate in 24hours: {}, {}", id, noticeId);
                increasePostHitCount(notice, noticeId, id);
            }
        }
         //update가 발생하므로 @Transactional
        return new NoticeDto.NoticeReadResponse(notice);
    }

    /*
     * 조회수 중복 방지를 위한 Redis 키 생성 메서드
     */
    private void increasePostHitCount(Notice notice, Long noticeId , String userId) {
        notice.updateHit();
        redisService.writeClientRequest(userId, noticeId, notice);
    }


    /**
     * 공지사항 업데이트
     */
    @Override
    @Transactional
    public NoticeDto.NoticeUpdateResponse update(NoticeDto.NoticeUpdateRequest dto, List<MultipartFile> multipartFiles, Long noticeId) throws IOException {
        String id = securityUtil.getCurrentMemberUsername();
        Notice notice = noticeRepository.findWithWriterById(noticeId).orElseThrow(() -> BoardNotFoundException.EXCEPTION);

        if (!Objects.equals(id, dto.getWriter()) || !Objects.equals(id, notice.getWriter().getId()) || !Objects.equals(dto.getWriter(), notice.getWriter().getId())) {
            throw WriterAndLoggedInUserMismatchExceptionAll.EXCEPTION;

        }
        List<File> files = fileService.updateNoticeImg(dto.getImgUrl(), multipartFiles, notice);

        notice.changeNotice(dto);


        return new NoticeDto.NoticeUpdateResponse(files , notice,200,"공지가 수정되었습니다.");
    }

    /**
     * 공지사항 삭제
     */
    @Override
    @Transactional
    public NoticeDto.ResponseBasic delete(Long noticeId) {
        String id = securityUtil.getCurrentMemberUsername();
        Optional<Notice> deletedNotice = noticeRepository.findById(noticeId);
        if (deletedNotice.isPresent()) {
            if (!Objects.equals(id, deletedNotice.get().getWriter().getId())) {
                throw WriterAndLoggedInUserMismatchExceptionAll.EXCEPTION;
            }

            commentRepository.removeParentRelationForChildCommentsByNoticeId(noticeId);
            commentRepository.deleteParentCommentsByNoticeId(noticeId);
            fileRepository.deleteByNoticeId(noticeId);
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
