package place.skillexchange.backend.talent.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import place.skillexchange.backend.talent.entity.SubjectCategory;
import place.skillexchange.backend.common.util.NestedConvertHelper;

import java.util.ArrayList;
import java.util.List;

public class SubjectCategoryDto {

    /**
     * 카테고리 목록 응답 Dto
     */
    @Getter
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryListResponse {
        private Long id;
        private String name;
        private List<CategoryListResponse> children;

        public static List<CategoryListResponse> toDtoList(List<SubjectCategory> categories) {
            NestedConvertHelper helper = NestedConvertHelper.newInstance(
                    //계층형 구조로 변환할 엔티티 목록
                    categories,
                    //엔티티를 DTO로 변환하는 함수
                    c -> new CategoryListResponse(c.getId(), c.getSubjectName(), new ArrayList<>()),
                    //엔티티의 부모를 반환하는 함수
                    c -> c.getParent(),
                    //엔티티의 ID를 반환하는 함수
                    c -> c.getId(),
                    //DTO의 자식 목록을 반환하는 함수
                    d -> d.getChildren());
            //계층형 변환
            return helper.convert();
        }
    }
}
