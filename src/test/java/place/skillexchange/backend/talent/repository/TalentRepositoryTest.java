package place.skillexchange.backend.talent.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import place.skillexchange.backend.common.util.DummyDataGenerator;
import place.skillexchange.backend.exception.board.PlaceNotFoundException;
import place.skillexchange.backend.exception.board.SubjectCategoryNotFoundException;
import place.skillexchange.backend.exception.user.UserNotFoundException;
import place.skillexchange.backend.talent.dto.TalentDto;
import place.skillexchange.backend.talent.entity.Place;
import place.skillexchange.backend.talent.entity.SubjectCategory;
import place.skillexchange.backend.talent.entity.Talent;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TalentRepositoryTest {
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private PlaceRepository placeRepository;
//    @Autowired
//    private SubjectCategoryRepository categoryRepository;
//    @Autowired
//    private TalentRepository talentRepository;
//
//    @RepeatedTest(30)
//    @DisplayName("더미 데이터 생성 테스트")
//    void testGenerateDummyData() {
//        // Given
//        String writer = "alswl3322"; // 작성자 설정
//        // 이외의 필드들은 무작위로 생성됩니다.
//
//        // When
//        TalentDto.TalentRegisterRequest dto = DummyDataGenerator.generateDummyData(writer);
//
//        User user = userRepository.findById(writer).orElseThrow(() -> UserNotFoundException.EXCEPTION);
//        Place place = placeRepository.findByPlaceName(dto.getPlaceName()).orElseThrow(() -> PlaceNotFoundException.EXCEPTION);
//        SubjectCategory teachingSubject = categoryRepository.findBySubjectName(dto.getTeachingSubject()).orElseThrow(() -> SubjectCategoryNotFoundException.EXCEPTION);
//        SubjectCategory teachedSubject = categoryRepository.findBySubjectName(dto.getTeachedSubject()).orElseThrow(() -> SubjectCategoryNotFoundException.EXCEPTION);
//
//        Talent talent = talentRepository.save(dto.toEntity(user, place, teachingSubject, teachedSubject));
//
//        // Then
//        assertNotNull(dto);
//        assertEquals(writer, dto.getWriter());
//        assertNotNull(dto.getPlaceName());
//        assertNotNull(dto.getTeachingSubject());
//        assertNotNull(dto.getTeachedSubject());
//        assertNotNull(dto.getSelectedDays());
//        assertNotNull(dto.getMinAge());
//        assertNotNull(dto.getMaxAge());
//        assertNotNull(dto.getGender());
//        assertNotNull(dto.getTitle());
//        assertNotNull(dto.getContent());
//    }
}