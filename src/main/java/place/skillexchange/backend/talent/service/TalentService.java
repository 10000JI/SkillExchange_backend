package place.skillexchange.backend.talent.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import place.skillexchange.backend.talent.dto.TalentDto;

import java.io.IOException;
import java.util.List;

public interface TalentService {

    public TalentDto.TalentRegisterResponse register(TalentDto.TalentRegisterRequest dto, List<MultipartFile> multipartFiles) throws IOException;

    public TalentDto.WriterInfoResponse writerInfo(Long writerId);

    public TalentDto.TalentReadResponse read(Long writerId, HttpServletRequest request,
                                             HttpServletResponse response);

    public TalentDto.TalentUpdateResponse update(TalentDto.TalentUpdateRequest dto, List<MultipartFile> multipartFiles, Long talentId) throws IOException;

    public TalentDto.ResponseBasic delete(Long talentId);

    public Page<TalentDto.TalentListResponse> list(int limit, int skip, String keyword, Long subjectCategoryId);

    public TalentDto.ResponseBasic scrap(Long talentId);

    public List<TalentDto.RelatedPostsResponse> getRelatedPosts(String SubjectName);
}
