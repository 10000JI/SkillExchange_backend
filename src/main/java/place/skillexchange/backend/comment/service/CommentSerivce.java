package place.skillexchange.backend.comment.service;

import place.skillexchange.backend.comment.dto.CommentDto;

import java.util.List;

public interface CommentSerivce {
    public List<CommentDto.CommentViewResponse> findCommentsByNoticeId(Long noticeId);

    public List<CommentDto.CommentViewResponse> findCommentsByTalentId(Long talentId);

    public CommentDto.ResponseBasic deleteComment(Long commentId);
}
