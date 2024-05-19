package place.skillexchange.backend.talent.service;

import place.skillexchange.backend.talent.dto.SubjectCategoryDto;

import java.util.List;

public interface SubjectCategoryService {

    public List<SubjectCategoryDto.CategoryListResponse> findAll();
}
