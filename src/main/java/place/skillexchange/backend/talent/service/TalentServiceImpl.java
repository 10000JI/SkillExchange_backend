package place.skillexchange.backend.talent.service;

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
import place.skillexchange.backend.exception.board.*;
import place.skillexchange.backend.file.repository.FileRepository;
import place.skillexchange.backend.talent.dto.RequestSkillInfo;
import place.skillexchange.backend.talent.dto.TalentDto;
import place.skillexchange.backend.exception.user.WriterAndLoggedInUserMismatchExceptionAll;
import place.skillexchange.backend.exception.user.UserNotFoundException;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.file.service.FileService;
import place.skillexchange.backend.talent.entity.Place;
import place.skillexchange.backend.talent.entity.SubjectCategory;
import place.skillexchange.backend.talent.entity.Talent;
import place.skillexchange.backend.talent.entity.TalentScrap;
import place.skillexchange.backend.talent.repository.PlaceRepository;
import place.skillexchange.backend.talent.repository.SubjectCategoryRepository;
import place.skillexchange.backend.talent.repository.TalentRepository;
import place.skillexchange.backend.talent.repository.TalentScrapRepository;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.user.repository.UserRepository;
import place.skillexchange.backend.common.util.SecurityUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TalentServiceImpl implements TalentService {

    private final SecurityUtil securityUtil;
    private final TalentRepository talentRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final SubjectCategoryRepository categoryRepository;
    private final FileService fileService;
    private final TalentScrapRepository scrapRepository;
    private final CommentRepository commentRepository;
    private final FileRepository fileRepository;
    private final RedisService redisService;
    private final CookieUtil cookieUtil;

    /**
     * 재능교환 게시물 생성
     */
    @Override
    public TalentDto.TalentRegisterResponse register(TalentDto.TalentRegisterRequest dto, List<MultipartFile> multipartFiles) throws IOException {
        String id = securityUtil.getCurrentMemberUsername();
        User user = userRepository.findWithFileById(id).orElseThrow(() -> UserNotFoundException.EXCEPTION);
        if (!Objects.equals(id, dto.getWriter())) {
            throw WriterAndLoggedInUserMismatchExceptionAll.EXCEPTION;
        }
        Place place = placeRepository.findByPlaceName(dto.getPlaceName()).orElseThrow(() -> PlaceNotFoundException.EXCEPTION);
        SubjectCategory teachingSubject = categoryRepository.findBySubjectName(dto.getTeachingSubject()).orElseThrow(() -> SubjectCategoryNotFoundException.EXCEPTION);
        SubjectCategory teachedSubject = categoryRepository.findBySubjectName(dto.getTeachedSubject()).orElseThrow(() -> SubjectCategoryNotFoundException.EXCEPTION);

        Talent talent = talentRepository.save(dto.toEntity(user, place, teachingSubject, teachedSubject));

        List<File> files = null;
        System.out.println("MultiPartFiles:  "+multipartFiles);
        if (multipartFiles != null) {
            files = fileService.registerTalentImg(multipartFiles,talent);
        }
        return new TalentDto.TalentRegisterResponse(user,talent,files,201,"재능교환 게시물이 등록되었습니다.");
    }

    /**
     * 게시물 올린 글쓴이의 프로필 정보 불러오기
     */
    @Override
    public TalentDto.WriterInfoResponse writerInfo(Long talentId) {
        Talent talent = talentRepository.findById(talentId).orElseThrow(() -> BoardNotFoundException.EXCEPTION);
//        User user = userRepository.findById(talent.getWriter().getId()).orElseThrow(() -> UserNotFoundException.EXCEPTION);
        User user = talent.getWriter();
        return new TalentDto.WriterInfoResponse(user);
    }

    /**
     * 재능교환 게시물 조회
     */
    @Override
    @Transactional
    public TalentDto.TalentReadResponse read(Long talentId, HttpServletRequest request, HttpServletResponse response) {
        Talent talent = talentRepository.findWithAllAssociationsById(talentId)
                .orElseThrow(() -> BoardNotFoundException.EXCEPTION);
        String id = securityUtil.getCurrentMemberUsernameOrNonMember();
        if (id.equals("non-Member")) {
            Cookie[] cookies = request.getCookies();
            boolean checkCookie = false;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    // 이미 조회를 한 경우 체크
                    if (cookie.getName().equals(cookieUtil.getCookieName(talentId,talent))) checkCookie = true;
                }
                if (!checkCookie) {
                    Cookie newCookie = cookieUtil.createCookieForForNotOverlap(talentId,talent);
                    response.addCookie(newCookie);
                    talent.updateHit();
                }
            } else {
                Cookie newCookie = cookieUtil.createCookieForForNotOverlap(talentId, talent);
                response.addCookie(newCookie);
                talent.updateHit();
            }
        } else {
            if (redisService.isFirstIpRequest(id, talentId, talent)) {
                log.debug("same user requests duplicate in 24hours: {}, {}", id, talentId);
                increasePostHitCount(talent, talentId, id);
            }
        }
        return new TalentDto.TalentReadResponse(talent);
    }

    /*
     * 조회수 중복 방지를 위한 Redis 키 생성 메서드
     */
    private void increasePostHitCount(Talent talent, Long talentId ,String userId) {
        talent.updateHit();
        redisService.writeClientRequest(userId, talentId, talent);
    }

    /**
     * 게시물 수정
     */
    @Override
    @Transactional
    public TalentDto.TalentUpdateResponse update(TalentDto.TalentUpdateRequest dto, List<MultipartFile> multipartFiles, Long talentId) throws IOException {
        String id = securityUtil.getCurrentMemberUsername();
        Talent talent = talentRepository.findWithPartAssociationsById(talentId)
                .orElseThrow(() -> BoardNotFoundException.EXCEPTION);
        if (!Objects.equals(id, dto.getWriter()) || !Objects.equals(id,talent.getWriter().getId()) || !Objects.equals(dto.getWriter(),talent.getWriter().getId())) {
            throw WriterAndLoggedInUserMismatchExceptionAll.EXCEPTION;
        }
        Place place = null;
        if (!talent.getPlace().getPlaceName().equals(dto.getPlaceName())) {
            place = placeRepository.findByPlaceName(dto.getPlaceName()).orElseThrow(() -> PlaceNotFoundException.EXCEPTION);
        }
        SubjectCategory teachedSubject = null;
        if (!talent.getTeachedSubject().getSubjectName().equals(dto.getTeachedSubject())) {
            teachedSubject = categoryRepository.findBySubjectName(dto.getTeachedSubject()).orElseThrow(() -> SubjectCategoryNotFoundException.EXCEPTION);
        }
        SubjectCategory teachingSubject = null;
        if(!talent.getTeachingSubject().getSubjectName().equals(dto.getTeachingSubject())) {
            teachingSubject = categoryRepository.findBySubjectName(dto.getTeachingSubject()).orElseThrow(() -> SubjectCategoryNotFoundException.EXCEPTION);
        }
        talent.changeTalent(dto, place, teachedSubject, teachingSubject);

        List<File> files = fileService.updateTalentImg(dto.getImgUrl(), multipartFiles, talent);


        return new TalentDto.TalentUpdateResponse(talent, files, 200, "재능교환 게시물이 수정되었습니다.");
    }

    /**
     * 게시물 삭제
     */
    @Override
    @Transactional
    public TalentDto.ResponseBasic delete(Long talentId) {
        String id = securityUtil.getCurrentMemberUsername();
        Optional<Talent> deleteTalent = talentRepository.findById(talentId);
        if (deleteTalent.isPresent()) {
            if (!Objects.equals(id, deleteTalent.get().getWriter().getId())) {
                throw WriterAndLoggedInUserMismatchExceptionAll.EXCEPTION;
            }
            commentRepository.removeParentRelationForChildCommentsByTalentId(talentId);
            commentRepository.deleteParentCommentsByTalentId(talentId);
            fileRepository.deleteByTalentId(talentId);
            talentRepository.deleteById(talentId);
            return new TalentDto.ResponseBasic(200, "재능교환 게시물이 성공적으로 삭제되었습니다.");
        } else {
            throw BoardNotFoundException.EXCEPTION;
        }
    }

    /**
     * 게시물 목록
     */
    @Override
    public Page<TalentDto.TalentListResponse> list(int limit, int skip, String keyword, Long subjectCategoryId) {
        if (subjectCategoryId != null) {
            categoryRepository.findById(subjectCategoryId).orElseThrow(() -> SubjectCategoryNotFoundException.EXCEPTION); //과목의 자식 카테고리가 존재하는 카테고리 번호인지 체크
            categoryRepository.findByIdAndParentIsNotNull(subjectCategoryId).orElseThrow(() -> SubjectCategoryBadRequestException.EXCEPTION); //과목의 부모 카테고리로는 조회 X
        }
        Pageable pageable = PageRequest.of(skip, limit);
        return talentRepository.findAllWithPagingAndSearch(keyword, pageable, subjectCategoryId);
    }

    /**
     * 게시물 스크랩
     */
    @Override
    public TalentDto.ResponseBasic scrap(Long talentId) {
        String id = securityUtil.getCurrentMemberUsername();
        User user = userRepository.findWithFileById(id).orElseThrow(() -> UserNotFoundException.EXCEPTION);
        if (scrapRepository.findByTalentIdAndUserId(talentId, id) != null) {
            throw BoardAleadyScrappedException.EXCEPTION;
        }
        Talent talent = talentRepository.findById(talentId).orElseThrow(() -> BoardNotFoundException.EXCEPTION);
        TalentScrap scrap = TalentScrap.of(user, talent);
        scrapRepository.save(scrap);

        return new TalentDto.ResponseBasic(201,"스크랩이 완료되었습니다.");
    }

    //관련 게시물
    @Override
    public List<TalentDto.RelatedPostsResponse> getRelatedPosts(String subjectName) {
        List<TalentDto.RelatedPostsResponse> result = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 5);
        List<Talent> relatedPostsById = talentRepository.findRelatedPostsById(pageable, subjectName);
        if (!relatedPostsById.isEmpty()) {
            for (Talent talent : relatedPostsById) {
                result.add(new TalentDto.RelatedPostsResponse(talent));
            }
        } else {
            throw BoardNotFoundException.EXCEPTION;
        }
        return result;
    }

    // 재능교환 요청
    @Override
    @Transactional
    public TalentDto.ResponseBasic talentExchange(Long talentId) {
        String userId = securityUtil.getCurrentMemberUsername();
        if (talentRepository.findRequestSkill(talentId, userId).isPresent()) {
            throw BoardAleadyRequestSkillException.EXCEPTION;
        }
        Talent talent = talentRepository.findById(talentId).orElseThrow(() -> BoardNotFoundException.EXCEPTION);
        if (talent.getWriter().getId().equals(userId)) {
            throw BoardSelfRequestSkillException.EXCEPTION;
        }
        talent.addExchangeRequester(userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.EXCEPTION));
        return new TalentDto.ResponseBasic(201,"재능교환 요청이 완료되었습니다");
    }

    //재능교환 요청 목록
    @Override
    public List<RequestSkillInfo> talentExchangeInfo() {
        String userId = securityUtil.getCurrentMemberUsername();
        User user = userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.EXCEPTION);
        List<RequestSkillInfo> exchangeRequestInfoByWriter = talentRepository.findExchangeRequestInfoByWriter(user);
        return exchangeRequestInfoByWriter;
    }

    //재능교환 요청 수락
    @Override
    @Transactional
    public TalentDto.ResponseBasic talentExchangeApprove(Long talentId, TalentDto.ExchangeApproveRequest dto) {
        String userId = securityUtil.getCurrentMemberUsername();
        Talent talent = talentRepository.findRequestSkillApprove(talentId, dto.getGuestId(),userId).orElseThrow(() -> BoardNotFoundException.EXCEPTION);
        talent.completeExchange();
        talentRepository.deleteExchangeRequester(talentId);
        return new TalentDto.ResponseBasic(201,"재능교환 서비스가 매칭되었습니다.");
    }


}
