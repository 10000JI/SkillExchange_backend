package place.skillexchange.backend.talent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import place.skillexchange.backend.talent.dto.SubjectCategoryDto;
import place.skillexchange.backend.talent.entity.SubjectCategory;
import place.skillexchange.backend.talent.repository.SubjectCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectCategoryServiceImpl implements SubjectCategoryService {
    private final SubjectCategoryRepository subjectCategoryRepository;

    @Override
    public List<SubjectCategoryDto.CategoryListResponse> findAll() {
        List<SubjectCategory> subjectCategories = subjectCategoryRepository.findAllOrderByParentIdAscNullsFirstCategoryIdAsc();
        return SubjectCategoryDto.CategoryListResponse.toDtoList(subjectCategories);
    }
}
