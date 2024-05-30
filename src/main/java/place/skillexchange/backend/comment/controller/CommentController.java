package place.skillexchange.backend.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "공지사항 게시물 번호의 댓글 조회 API", description = "noticeId를 이용해서 댓글들을 조회합니다.")
    @GetMapping(value = "/notice/{noticeId}")
    public List<CommentDto.CommentViewResponse> findAllCommentsByNoticeId(@Parameter(description = "게시물 ID", required = true, example = "1") @PathVariable("noticeId") Long noticeId) {
        return commentServiceImpl.findCommentsByNoticeId(noticeId);
    }

    /**
     * 공지사항 댓글 등록
     */
    @Operation(summary = "공지사항 게시물 번호의 댓글 등록 API", description = "parentId가 null이면 부모 댓글, parentId가 부모 댓글의 noticeId 값이라면 자식 댓글(대댓글)입니다.")
    @PostMapping(value="/notice/register")
    public ResponseEntity<CommentDto.CommentRegisterResponse> createComment(@Validated @RequestBody CommentDto.CommentRegisterRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentServiceImpl.createComment(dto));
    }

    /**
     * 공지사항 댓글 삭제
     */
    @Operation(summary = "공지사항 게시물 번호의 댓글 삭제 API", description = "noticeId를 이용해서 댓글을 삭제합니다.")
    @DeleteMapping(value =  "/notice/{commentId}")
    public CommentDto.ResponseBasic deleteComment(@PathVariable("commentId") Long commentId) {
        return commentServiceImpl.deleteComment(commentId);
    }
}
