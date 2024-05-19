package place.skillexchange.backend.talent.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import place.skillexchange.backend.talent.dto.TalentDto;

public interface CustomTalentRepository {
    Page<TalentDto.TalentListResponse> findAllWithPagingAndSearch(String keyword, Pageable pageable, Long subjectCategoryId);
}
