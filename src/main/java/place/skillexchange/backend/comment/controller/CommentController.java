package place.skillexchange.backend.comment.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import place.skillexchange.backend.comment.dto.CommentDto;
import place.skillexchange.backend.comment.service.CommentServiceImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comment/")
@Tag(name = "comment-controller", description = "게시물 댓글 컨트롤러입니다.")
public class CommentController {

    private final CommentServiceImpl commentServiceImpl;

    /**
     * 공지사항 게시물 번호의 댓글 조회
     */
    @GetMapping(value = "/notice/{noticeId}")
    public List<CommentDto.CommentViewResponse> findAllCommentsByNoticeId(@Parameter(description = "게시물 ID", required = true, example = "1") @PathVariable("noticeId") Long noticeId) {
        return commentServiceImpl.findCommentsByNoticeId(noticeId);
    }
    /**
     * 재능교환 게시물 번호의 댓글 조회
     */
    @GetMapping(value = "/talent/{talentId}")
    public List<CommentDto.CommentViewResponse> findAllCommentsByTalentId(@Parameter(description = "게시물 ID", required = true, example = "1") @PathVariable("talentId") Long talentId) {
        return commentServiceImpl.findCommentsByTalentId(talentId);
    }

    /**
     * 공지사항 댓글 등록
     */
    @PostMapping(value="/notice/register")
    public ResponseEntity<CommentDto.CommentRegisterResponse> createNoticeComment(@Validated @RequestBody CommentDto.CommentRegisterRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentServiceImpl.createNoticeComment(dto));
    }

    /**
     * 재능교환소 댓글 등록
     */
    @PostMapping(value="/talent/register")
    public ResponseEntity<CommentDto.CommentRegisterResponse> createTalentComment(@Validated @RequestBody CommentDto.CommentRegisterRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentServiceImpl.createTalentComment(dto));
    }

    /**
     * 공지사항, 재능교환소 댓글 삭제
     */
    @DeleteMapping(value =  "/{commentId}")
    public CommentDto.ResponseBasic deleteNoticeComment(@PathVariable("commentId") Long commentId) {
        return commentServiceImpl.deleteComment(commentId);
    }
}
