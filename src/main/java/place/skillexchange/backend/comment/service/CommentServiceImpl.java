package place.skillexchange.backend.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import place.skillexchange.backend.comment.dto.CommentDto;
import place.skillexchange.backend.comment.entity.Comment;
import place.skillexchange.backend.comment.entity.DeleteStatus;
import place.skillexchange.backend.exception.board.BoardNotFoundException;
import place.skillexchange.backend.exception.board.BoardNumNotFoundException;
import place.skillexchange.backend.exception.board.CommentNotFoundException;
import place.skillexchange.backend.exception.user.UserNotFoundException;
import place.skillexchange.backend.notice.entity.Notice;
import place.skillexchange.backend.exception.user.WriterAndLoggedInUserMismatchExceptionAll;
import place.skillexchange.backend.comment.repository.CommentRepository;
import place.skillexchange.backend.notice.repository.NoticeRepository;
import place.skillexchange.backend.talent.entity.Talent;
import place.skillexchange.backend.talent.repository.TalentRepository;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.user.repository.UserRepository;
import place.skillexchange.backend.common.util.SecurityUtil;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentSerivce {
    private final NoticeRepository noticeRepository;
    private final CommentRepository commentRepository;
    private final SecurityUtil securityUtil;
    private final UserRepository userRepository;
    private final TalentRepository talentRepository;

    /**
     * 공지사항 게시물 번호의 댓글 조회
     */
    @Override
    public List<CommentDto.CommentViewResponse> findCommentsByNoticeId(Long noticeId) {
        if (noticeRepository.countByNoticeId(noticeId) < 1) {
            throw BoardNotFoundException.EXCEPTION;
        }
        //댓글 조회 메서드 `convertNestedStructure`
        return convertNestedStructure(commentRepository.findCommentByNoticeId(noticeId));
    }

    /**
     * 재능교환 게시물 번호의 댓글 조회
     */
    @Override
    public List<CommentDto.CommentViewResponse> findCommentsByTalentId(Long talentId) {
        if (talentRepository.countByTalentId(talentId) < 1) {
            throw BoardNotFoundException.EXCEPTION;
        }
        //댓글 조회 메서드 `convertNestedStructure`
        return convertNestedStructure(commentRepository.findCommentByTalentId(talentId));
    }

    private List<CommentDto.CommentViewResponse> convertNestedStructure(List<Comment> comments) {
        //조회 결과인 comments List는 (매개변수) 깊이와 작성순으로 정렬된 결과를 가지고 있다.
        List<CommentDto.CommentViewResponse> result = new ArrayList<>();
        Map<Long, CommentDto.CommentViewResponse> map = new HashMap<>();
        comments.stream().forEach(c -> {
            CommentDto.CommentViewResponse dto = CommentDto.CommentViewResponse.entityToDto(c);
            //자식 댓글을 확인할 때는 부모 댓글이 이미 map에 들어가있는 상황
            map.put(dto.getId(), dto);
            //부모가 있는 자식 댓글이라면 부모 댓글의 자식 댓글 리스트에 현재 자식 댓글을 추가
            if(c.getParent() != null) map.get(c.getParent().getId()).getChildren().add(dto);
            //최상위 댓글(부모)이라면 result에 넣어줌
            else result.add(dto);
        });
        return result;
        //따라서 기존 예시로 보자면, result에는 1번과 8번 댓글이 담겨있고,
        //1번의 자식 댓글 리스트에는 2번과 3번 댓글이 담겨있고,
        //2번의 자식 댓글 리스트에는 4번과 5번 댓글이 담겨있는 형태의 중첩구조
    }

    /**
     * 공지사항 게시물 번호의 댓글 등록
     */
    public CommentDto.CommentRegisterResponse createNoticeComment(CommentDto.CommentRegisterRequest dto) {
        Function<Long, Notice> noticeFinder = noticeId -> noticeRepository.findById(noticeId)
                .orElseThrow(() -> BoardNotFoundException.EXCEPTION);
        return createComment(dto, noticeFinder);
    }

    /**
     * 재능교환소 게시물 번호의 댓글 등록
     */
    public CommentDto.CommentRegisterResponse createTalentComment(CommentDto.CommentRegisterRequest dto) {
        Function<Long, Talent> talentFinder = talentId -> talentRepository.findById(talentId)
                .orElseThrow(() -> BoardNotFoundException.EXCEPTION);
        return createComment(dto, talentFinder);
    }

    /**
     * 댓글 등록 범용 메서드
     */
    public <T> CommentDto.CommentRegisterResponse createComment(CommentDto.CommentRegisterRequest<T> dto, Function<Long, T> boardFinder) {
        // 로그인한 user 객체 가져옴
        String id = securityUtil.getCurrentMemberUsername();
        User user = userRepository.findWithFileById(id).orElseThrow(() -> UserNotFoundException.EXCEPTION);
        if (!Objects.equals(id, dto.getWriter())) {
            throw WriterAndLoggedInUserMismatchExceptionAll.EXCEPTION;
        }

        // dto의 게시물번호(pk)가 null로 들어오면 exception 발생
        if (dto.getBoardId() == null) {
            throw BoardNumNotFoundException.EXCEPTION;
        }

        // dto의 게시물번호(pk)를 지닌 board 가져옴
        T board = boardFinder.apply(dto.getBoardId());

        // dto의 부모댓글번호가 null 이라면 comment도 null
        // dto의 부모댓글번호가 존재한다면 부모댓글번호를 pk로 하는 comment 가져옴
        Comment parent = dto.getParentId() != null ?
                commentRepository.findById(dto.getParentId())
                        .orElseThrow(() -> CommentNotFoundException.EXCEPTION) : null;

        // 댓글 저장
        Comment saveComment = commentRepository.save(dto.toEntity(user, board, parent));
        return new CommentDto.CommentRegisterResponse(saveComment, 201, "댓글이 성공적으로 등록되었습니다.");
    }

    /**
     * 공지사항 게시물 번호의 댓글 삭제
     */
    @Override
    @Transactional
    public CommentDto.ResponseBasic deleteComment(Long commentId) {
        String id = securityUtil.getCurrentMemberUsername();

        Comment comment = commentRepository.findCommentByIdWithParent(commentId)
                .orElseThrow(() -> CommentNotFoundException.EXCEPTION);

        if (!Objects.equals(id, comment.getWriter().getId())) {
            throw WriterAndLoggedInUserMismatchExceptionAll.EXCEPTION;
        }

        //해당 댓글이 자식 댓글을 가지고 있는지 확인
        if(comment.getChildren().size() != 0) {
            //자식 댓글이 있다면, 댓글의 삭제 상태를 'Y'로 변경
            comment.changeDeletedStatus(DeleteStatus.Y);
        //그렇지 않다면, 삭제 가능한 부모 댓글을 찾아서 삭제
        } else {
            //"삭제 가능한 조상 댓글"을 확인하는 과정에서, 자식 댓글이 삭제되었을 경우 해당 부모 댓글과 자식 댓글을 데이터베이스에서 삭제
            commentRepository.delete(getDeletableAncestorComment(comment));
        }
        return new CommentDto.ResponseBasic(200,"댓글이 성공적으로 삭제되었습니다.");
    }

    //댓글의 삭제 가능한 부모 댓글을 찾기 위한 보조 메소드
    public Comment getDeletableAncestorComment(Comment comment) {
        //댓글의 부모를 확인
        Comment parent = comment.getParent();

        //부모 댓글이 존재하는지 확인하고, 해당 부모 댓글이 삭제 상태(Y)인지 확인
        //만약 삭제 상태라면, 해당 부모 댓글이 자식 댓글을 하나만 가지고 있는지 확인
        if(parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted() == DeleteStatus.Y)
            //만약 조건을 모두 만족한다면, 해당 부모 댓글에 대해 재귀적으로 getDeletableAncestorComment 메소드를 호출
            return getDeletableAncestorComment(parent);

        //조건에 만족하지 않는 경우에는 현재 댓글을 반환
        return comment;
    }

}
