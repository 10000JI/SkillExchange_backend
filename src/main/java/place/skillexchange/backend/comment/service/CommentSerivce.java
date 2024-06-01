package place.skillexchange.backend.comment.service;

import place.skillexchange.backend.comment.dto.CommentDto;

import java.util.List;

public interface CommentSerivce {
    public List<CommentDto.NoticeCommentViewResponse> findCommentsByNoticeId(Long noticeId);

//    public CommentDto.NoticeCommentRegisterResponse createNoticeComment(CommentDto.NoticeCommentRegisterRequest dto);
//
//    public CommentDto.TalentCommentRegisterResponse createTalentComment(CommentDto.TalentCommentRegisterRequest dto);

    public CommentDto.ResponseBasic deleteComment(Long commentId);
}
