package place.skillexchange.backend.talent.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import place.skillexchange.backend.talent.dto.SubjectCategoryDto;
import place.skillexchange.backend.talent.service.SubjectCategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/subjectCategory/")
public class SubjectCategoryController {
    private final SubjectCategoryService categoryService;

    @GetMapping("/list")
    public List<SubjectCategoryDto.CategoryListResponse> list() {
        return categoryService.findAll();
    }
}
