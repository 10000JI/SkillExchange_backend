package place.skillexchange.backend.comment.service;

import place.skillexchange.backend.comment.dto.CommentDto;

import java.util.List;

public interface CommentSerivce {
    public List<CommentDto.CommentViewResponse> findCommentsByNoticeId(Long noticeId);

    public CommentDto.CommentRegisterResponse createComment(CommentDto.CommentRegisterRequest dto);

    public CommentDto.ResponseBasic deleteComment(Long commentId);
}
