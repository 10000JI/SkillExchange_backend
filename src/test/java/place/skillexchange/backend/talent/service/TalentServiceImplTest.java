package place.skillexchange.backend.talent.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import place.skillexchange.backend.common.util.DayOfWeekUtil;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.file.service.FileServiceImpl;
import place.skillexchange.backend.talent.dto.TalentDto;
import place.skillexchange.backend.talent.entity.*;
import place.skillexchange.backend.talent.repository.PlaceRepository;
import place.skillexchange.backend.talent.repository.SubjectCategoryRepository;
import place.skillexchange.backend.talent.repository.TalentRepository;
import place.skillexchange.backend.talent.repository.TalentScrapRepository;
import place.skillexchange.backend.user.entity.Gender;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.user.repository.UserRepository;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class TalentServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TalentRepository talentRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private SubjectCategoryRepository categoryRepository;

    @Mock
    private FileServiceImpl fileService;

    @Mock
    private TalentScrapRepository scrapRepository;

    @InjectMocks
    private TalentServiceImpl talentService;

    @Test
    @DisplayName("재능교환 게시물 생성 성공 테스트")
    public void testRegister() throws IOException {
        //Given
        Long boardId = 1L;
        String userId = "testUser";
        String writer = "testUser";
        String title = "testTitle";
        String content = "testContent";
        String placeName = "testPlace";
        String teachingSubject = "testTeachingSubject";
        String teachedSubject = "testTeachedSubject";
        Long minAge = 25L;
        Long maxAge = 30L;
        Set<String> selectedDays = new HashSet<>(Arrays.asList("MON", "TUE", "WED"));
        String gender = "FEMALE";
        String img = "img.jpg";
        String imgUrl = "https://.../img1.jpg";

        User user = User.builder().id(writer).build();
        Place place = new Place(1L, placeName);
        SubjectCategory teachingSubjectCategory = new SubjectCategory(7L, teachingSubject, new SubjectCategory(1L, "parentCategory1", null));
        SubjectCategory teachedSubjectCategory = new SubjectCategory(19L, teachedSubject, new SubjectCategory(2L, "parentCategory2", null));
        Talent talent = Talent.builder().id(boardId).writer(user).place(place).teachingSubject(teachingSubjectCategory).teachedSubject(teachedSubjectCategory).title(title).content(content).minAge(minAge).maxAge(maxAge).dayOfWeek(DayOfWeekUtil.convertSelectedDaysToEnum(selectedDays)).gender(GenderForTalent.valueOf(gender)).hit(0L).build();
        //예상으로 반환되는 객체
        List<File> files = new ArrayList<>();
        File file = File.builder().oriName(img).fileUrl(imgUrl).build();
        files.add(file);

        // MultipartFile을 저장할 리스트 생성
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile(img.substring(0, img.lastIndexOf('.')), img, "image/jpeg", new byte[0]));

        TalentDto.TalentRegisterRequest request = TalentDto.TalentRegisterRequest.builder()
                .writer(writer)
                .title(title)
                .content(content)
                .placeName(placeName)
                .teachingSubject(teachingSubject)
                .teachedSubject(teachedSubject)
                .minAge(minAge)
                .minAge(maxAge)
                .gender(gender)
                .selectedDays(selectedDays)
                .selectedDays(selectedDays).build();

        // 현재 인증된 사용자 설정
        Authentication authentication = new TestingAuthenticationToken(userId, null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // userRepository의 동작을 모의화
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        //placeRepository의 동작을 모의화
        when(placeRepository.findByPlaceName(placeName)).thenReturn(Optional.of(place));
        //categoryRepository의 동작을 모의화
        when(categoryRepository.findBySubjectName(teachingSubject)).thenReturn(Optional.of(teachingSubjectCategory));
        //categoryRepository의 동작을 모의화
        when(categoryRepository.findBySubjectName(teachedSubject)).thenReturn(Optional.of(teachedSubjectCategory));
        //talentRepository의 동작을 모의화
        when(talentRepository.save(any(Talent.class))).thenReturn(talent);
        // fileService의 동작을 모의화
        when(fileService.registerTalentImg(multipartFiles, talent)).thenReturn(files);

        //When
        TalentDto.TalentRegisterResponse response = talentService.register(request,multipartFiles);

        // Then
        assertNotNull(response);
        assertThat(userId).isEqualTo(response.getWriter());
        assertThat(boardId).isEqualTo(response.getId());
        assertThat(title).isEqualTo(response.getTitle());
        assertThat(content).isEqualTo(response.getContent());
        assertThat(placeName).isEqualTo(response.getPlaceName());
        assertThat(teachingSubject).isEqualTo(response.getTeachingSubject());
        assertThat(teachedSubject).isEqualTo(response.getTeachedSubject());
        assertThat(minAge).isEqualTo(response.getMinAge());
        assertThat(maxAge).isEqualTo(response.getMaxAge());
        assertThat(selectedDays).isEqualTo(response.getSelectedDays());
        assertThat(gender).isEqualTo(response.getGender());
        assertThat(files.get(0).getFileUrl()).isEqualTo(response.getImgUrl().get(0));
        assertThat(201).isEqualTo(response.getReturnCode());
        assertThat("재능교환 게시물이 등록되었습니다.").isEqualTo(response.getReturnMessage());

        verify(userRepository).findById(userId);
        verify(placeRepository).findByPlaceName(placeName);
        verify(categoryRepository).findBySubjectName(teachingSubject);
        verify(categoryRepository).findBySubjectName(teachedSubject);
        verify(talentRepository).save(any(Talent.class));
        // fileService.registerTalentImg가 올바른 파라미터와 함께 호출되었는지 확인
        verify(fileService).registerTalentImg(multipartFiles, talent);
    }

    @Test
    @DisplayName("재능교환 올린 글쓴이의 프로필 정보 불러오기 성공 테스트")
    public void testWriterInfo() {
        //Given
        Long boardId = 1L;
        String writer = "testUser";
        String gender = "FEMALE";
        String careerSkills = "testCareerSkills";
        String preferredSubject = "testPreferredSubject";
        String mySubject = "testMySubject";

        User user = User.builder().id(writer).gender(Gender.valueOf(gender)).careerSkills(careerSkills).preferredSubject(preferredSubject).mySubject(mySubject).build();
        Talent talent = Talent.builder().id(boardId).writer(user).build();

        //talentRepository의 동작을 모의화
        when(talentRepository.findById(boardId)).thenReturn(Optional.of(talent));
        //userRepository의 동작을 모의화
        when(userRepository.findById(talent.getWriter().getId())).thenReturn(Optional.of(user));

        //When
        TalentDto.WriterInfoResponse response = talentService.writerInfo(boardId);

        // Then
        assertNotNull(response);
        assertThat(writer).isEqualTo(response.getId());
        assertThat(gender).isEqualTo(response.getGender());
        assertThat(careerSkills).isEqualTo(response.getCareerSkills());
        assertThat(preferredSubject).isEqualTo(response.getPreferredSubject());
        assertThat(mySubject).isEqualTo(response.getMySubject());

        verify(talentRepository).findById(boardId);
        verify(userRepository).findById(talent.getWriter().getId());
    }

    @Test
    @DisplayName("재능교환 게시물 조회 성공 테스트")
    public void testRead()  {
        //Given
        Long boardId = 1L;
        String writer = "testUser";
        String title = "testTitle";
        String content = "testContent";
        String placeName = "testPlace";
        String teachingSubject = "testTeachingSubject";
        String teachedSubject = "testTeachedSubject";
        Long minAge = 25L;
        Long maxAge = 30L;
        Set<String> selectedDays = new HashSet<>(Arrays.asList("MON", "TUE", "WED"));
        String gender = "FEMALE";
        String img1 = "img1.jpg";
        String img2 = "img2.jpg";
        String imgUrl1 = "https://.../img1.jpg";
        String imgUrl2 = "https://.../img2.jpg";


        List<File> boardFile = new ArrayList<>();
        File file = File.builder().oriName(img2).fileUrl(imgUrl2).build();
        boardFile.add(file);
        File avatarFile = File.builder().id(1L).oriName(img1).fileUrl(imgUrl1).build();
        User user = User.builder().id(writer).file(avatarFile).build();
        SubjectCategory teachingSubjectCategory = new SubjectCategory(7L, teachingSubject, new SubjectCategory(1L, "parentCategory1", null));
        SubjectCategory teachedSubjectCategory = new SubjectCategory(19L, teachedSubject, new SubjectCategory(2L, "parentCategory2", null));
        Talent talent = Talent.builder().id(boardId).writer(user).place(new Place(1L, placeName)).teachingSubject(teachingSubjectCategory).teachedSubject(teachedSubjectCategory).title(title).content(content).minAge(minAge).maxAge(maxAge).gender(GenderForTalent.valueOf(gender)).dayOfWeek(DayOfWeekUtil.convertSelectedDaysToEnum(selectedDays)).hit(0L).files(boardFile).build();

        //talentRepository의 동작을 모의화
        when(talentRepository.findById(boardId)).thenReturn(Optional.of(talent));

        //When
        TalentDto.TalentReadResponse response = talentService.read(boardId);

        // Then
        assertNotNull(response);
        assertThat(boardId).isEqualTo(response.getId());
        assertThat(imgUrl1).isEqualTo(response.getAvatar());
        assertThat(title).isEqualTo(response.getTitle());
        assertThat(content).isEqualTo(response.getContent());
        assertThat(placeName).isEqualTo(response.getPlaceName());
        assertThat(teachingSubject).isEqualTo(response.getTeachingSubject());
        assertThat(teachedSubject).isEqualTo(response.getTeachedSubject());
        assertThat(minAge).isEqualTo(response.getMinAge());
        assertThat(maxAge).isEqualTo(response.getMaxAge());
        assertThat(selectedDays).isEqualTo(response.getSelectedDays());
        assertThat(gender).isEqualTo(response.getGender());
        assertThat(imgUrl2).isEqualTo(response.getImgUrl().get(0));

        verify(talentRepository).findById(boardId);
    }

    @Test
    @DisplayName("재능교환 게시물 수정 성공 테스트 (변경 데이터: 가르침을 받을 과목(teachedSubject), 새로운 이미치 추가)")
    public void testUpdate() throws IOException {
        //Given
        Long boardId = 1L;
        String userId = "testUser";
        String writer = "testUser";
        String title = "testTitle";
        String content = "testContent";
        String placeName = "testPlace";
        String teachingSubject = "testTeachingSubject";
        String teachedSubject = "testTeachedSubject";
        String updateTeachedSubject = "updateTeachedSubject";
        Long minAge = 25L;
        Long maxAge = 30L;
        Set<String> selectedDays = new HashSet<>(Arrays.asList("MON", "TUE", "WED"));
        String gender = "FEMALE";
        String img1 = "img1.jpg";
        String img2 = "img2.jpg";
        String imgUrl1 = "https://.../img1.jpg";
        String imgUrl2 = "https://.../img2.jpg";


        User user = User.builder().id(writer).build();
        Place place = new Place(1L, placeName);
        SubjectCategory teachingSubjectCategory = new SubjectCategory(7L, teachingSubject, new SubjectCategory(1L, "parentCategory1", null));
        SubjectCategory teachedSubjectCategory = new SubjectCategory(19L, teachedSubject, new SubjectCategory(2L, "parentCategory2", null));

        List<File> files = new ArrayList<>();
        File file1 = File.builder().oriName(img1).fileUrl(imgUrl1).build();
        files.add(file1);

        Talent talent = Talent.builder().id(boardId).writer(user).place(place).teachingSubject(teachingSubjectCategory).teachedSubject(teachedSubjectCategory).title(title).content(content).minAge(minAge).maxAge(maxAge).gender(GenderForTalent.valueOf(gender)).dayOfWeek(DayOfWeekUtil.convertSelectedDaysToEnum(selectedDays)).hit(0L).files(files).build();

        // MultipartFile을 저장할 리스트 생성, 새로운 이미지 요청
        List<MultipartFile> multipartFiles = new ArrayList<>();
        multipartFiles.add(new MockMultipartFile(img2.substring(0, img2.lastIndexOf('.')), img2, "image/jpeg", new byte[0]));

        List<String> imgUrl = new ArrayList<>();
        imgUrl.add(imgUrl1); //기존에 저장한 이미지 url

        File file2 = File.builder().oriName(img2).fileUrl(imgUrl2).build();
        files.add(file2); //MultipartFile로 새롭게 추가한 이미지 파일 리턴 값(=File)

        // 가르침을 받을 과목(teachedSubject) 수정 요청
        SubjectCategory updateTeachedSubjectCategory = new SubjectCategory(37L, updateTeachedSubject, new SubjectCategory(3L, "parentCategory3", null));

        TalentDto.TalentUpdateRequest request = TalentDto.TalentUpdateRequest.builder()
                .writer(writer)
                .title(title)
                .content(content)
                .placeName(placeName)
                .teachingSubject(teachingSubject)
                .teachedSubject(updateTeachedSubject)
                .minAge(minAge)
                .maxAge(maxAge)
                .gender(gender)
                .selectedDays(selectedDays)
                //기존에 저장된 이미지 그대로 사용
                .imgUrl(imgUrl).build();

        // 현재 인증된 사용자 설정
        Authentication authentication = new TestingAuthenticationToken(userId, null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //userRepository의 동작을 모의화
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        //talentRepository의 동작을 모의화
        when(talentRepository.findById(boardId)).thenReturn(Optional.of(talent));
        //categoryRepository의 동작을 모의화 (teachedSubject 수정)
        when(categoryRepository.findBySubjectName(updateTeachedSubject)).thenReturn(Optional.of(updateTeachedSubjectCategory));
        // fileService의 동작을 모의화 (기존 이미지 url + 새로운 이미치 파일 추가)
        when(fileService.updateTalentImg(request.getImgUrl(),multipartFiles, talent)).thenReturn(files);

        //When
        TalentDto.TalentUpdateResponse response = talentService.update(request, multipartFiles, boardId);

        // Then
        assertNotNull(response);
        assertThat(boardId).isEqualTo(response.getId());
        assertThat(title).isEqualTo(response.getTitle());
        assertThat(content).isEqualTo(response.getContent());
        assertThat(placeName).isEqualTo(response.getPlaceName());
        assertThat(teachingSubject).isEqualTo(response.getTeachingSubject());
        //수정된 가르침을 받을 과목(teachedSubject)
        assertThat(updateTeachedSubject).isEqualTo(response.getTeachedSubject());
        assertThat(minAge).isEqualTo(response.getMinAge());
        assertThat(maxAge).isEqualTo(response.getMaxAge());
        assertThat(selectedDays).isEqualTo(response.getSelectedDays());
        assertThat(gender).isEqualTo(response.getGender());
        assertThat(img1).isEqualTo(response.getOriName().get(0));
        assertThat(img2).isEqualTo(response.getOriName().get(1));
        assertThat(imgUrl1).isEqualTo(response.getImgUrl().get(0));
        assertThat(imgUrl2).isEqualTo(response.getImgUrl().get(1));
        assertThat(200).isEqualTo(response.getReturnCode());
        assertThat("재능교환 게시물이 수정되었습니다.").isEqualTo(response.getReturnMessage());

        verify(userRepository).findById(userId);
        verify(talentRepository).findById(boardId);
        verify(categoryRepository).findBySubjectName(updateTeachedSubject);
        verify(fileService).updateTalentImg(request.getImgUrl(), multipartFiles, talent);
    }

    @Test
    @DisplayName("재능교환 게시물 삭제 성공 테스트")
    public void testDelete(){
        //Given
        Long boardId = 1L;
        String userId = "testUser";
        String writer = "testUser";
        String title = "testTitle";
        String content = "testContent";
        String placeName = "testPlace";
        String teachingSubject = "testTeachingSubject";
        String teachedSubject = "testTeachedSubject";
        Long minAge = 25L;
        Long maxAge = 30L;
        Set<String> selectedDays = new HashSet<>(Arrays.asList("MON", "TUE", "WED"));
        String gender = "FEMALE";
        String img1 = "img1.jpg";
        String img2 = "img2.jpg";
        String imgUrl1 = "https://.../img1.jpg";
        String imgUrl2 = "https://.../img2.jpg";

        Place place = new Place(1L, placeName);
        SubjectCategory teachingSubjectCategory = new SubjectCategory(7L, teachingSubject, new SubjectCategory(1L, "parentCategory1", null));
        SubjectCategory teachedSubjectCategory = new SubjectCategory(19L, teachedSubject, new SubjectCategory(2L, "parentCategory2", null));
        List<File> files = new ArrayList<>();
        File file1 = File.builder().oriName(img1).fileUrl(imgUrl1).build();
        File file2 = File.builder().oriName(img2).fileUrl(imgUrl2).build();
        files.add(file1);
        files.add(file2);
        Talent talent = Talent.builder().id(boardId).writer(User.builder().id(writer).build()).place(place).teachingSubject(teachingSubjectCategory).teachedSubject(teachedSubjectCategory).title(title).content(content).minAge(minAge).maxAge(maxAge).gender(GenderForTalent.valueOf(gender)).dayOfWeek(DayOfWeekUtil.convertSelectedDaysToEnum(selectedDays)).hit(0L).files(files).build();

        // 현재 인증된 사용자 설정
        Authentication authentication = new TestingAuthenticationToken(userId, null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //userRepository의 동작을 모의화
        when(talentRepository.findById(boardId)).thenReturn(Optional.of(talent));
        //talentRepository의 동작을 모의화
        doNothing().when(talentRepository).deleteById(boardId);

        //When
        TalentDto.ResponseBasic response = talentService.delete(boardId);

        // Then
        assertNotNull(response);
        assertThat(200).isEqualTo(response.getReturnCode());
        assertThat("재능교환 게시물이 성공적으로 삭제되었습니다.").isEqualTo(response.getReturnMessage());

        verify(talentRepository).findById(boardId);
        verify(talentRepository).deleteById(boardId);
    }

    @Test
    @DisplayName("재능교환별 게시물 목록 조회 테스트")
    public void testList()  {
        // Given
        int limit = 10;
        int skip = 0;
        String keyword = "test"; // 검색어
        //teachedSubject pk 값을 쿼리스트링으로 넣어주면 최근 게시물 목록 -> 카테고리별 목록으로 전환
        Long subjectCategoryId = 7L;
        Pageable pageable = PageRequest.of(skip, limit);

        Long boardId1 = 1L;
        Long boardId2 = 2L;
        String writer = "testUser";
        String title = "testTitle";
        String content = "testContent";
        String placeName = "testPlace";
        String teachingSubject1 = "testTeachingSubject1";
        String teachingSubject2 = "testTeachingSubject2";
        String teachedSubject = "testTeachedSubject";
        Long minAge = 25L;
        Long maxAge = 30L;
        String img1 = "img1.jpg";
        String imgUrl1 = "https://.../img1.jpg";

        File avatarFile = File.builder().id(1L).oriName(img1).fileUrl(imgUrl1).build();
        User user = User.builder().id(writer).file(avatarFile).build();
        Place place = new Place(1L, placeName);
        SubjectCategory teachingSubjectCategory1 = new SubjectCategory(7L, teachingSubject1, new SubjectCategory(1L, "parentCategory1", null));
        SubjectCategory teachingSubjectCategory2 = new SubjectCategory(37L, teachingSubject2, new SubjectCategory(3L, "parentCategory2", null));
        SubjectCategory teachedSubjectCategory = new SubjectCategory(19L, teachedSubject, new SubjectCategory(2L, "parentCategory3", null));

        Talent talent1 = Talent.builder().id(boardId1).writer(user).title(title).content(content).place(place).teachingSubject(teachingSubjectCategory1).teachedSubject(teachedSubjectCategory).minAge(minAge).maxAge(maxAge).hit(0L).build();
        Talent talent2 = Talent.builder().id(boardId2).writer(user).title(title).content(content).place(place).teachingSubject(teachingSubjectCategory2).teachedSubject(teachedSubjectCategory).minAge(minAge).maxAge(maxAge).hit(0L).build();

        // 공지사항 목록 데이터 생성
        List<TalentDto.TalentListResponse> talentList = new ArrayList<>();
        talentList.add(new TalentDto.TalentListResponse(talent1));
        talentList.add(new TalentDto.TalentListResponse(talent2));
        // 페이지 객체 생성
        Page<TalentDto.TalentListResponse> page = new PageImpl<>(talentList, pageable, talentList.size());

        // Mock customNoticeRepository의 동작 설정
        when(talentRepository.findAllWithPagingAndSearch(keyword, pageable,subjectCategoryId)).thenReturn(page);
        when(categoryRepository.findById(subjectCategoryId)).thenReturn(Optional.of(teachedSubjectCategory));
        when(categoryRepository.findByIdAndParentIsNotNull(subjectCategoryId)).thenReturn(Optional.of(teachedSubjectCategory));

        // When
        Page<TalentDto.TalentListResponse> result = talentService.list(limit, skip, keyword, subjectCategoryId);

        // Then
        assertThat(result.getContent()).isEqualTo(talentList);
        assertThat(result.getTotalElements()).isEqualTo(talentList.size());
        assertThat(result.getNumber()).isEqualTo(page.getNumber());
        assertThat(result.getSize()).isEqualTo(page.getSize());

        assertThat(boardId1).isEqualTo(result.getContent().get(0).getId());
        assertThat(writer).isEqualTo(result.getContent().get(0).getWriter());
        assertThat(title).isEqualTo(result.getContent().get(0).getTitle());
        assertThat(content).isEqualTo(result.getContent().get(0).getContent());
        assertThat(placeName).isEqualTo(result.getContent().get(0).getPlaceName());
        assertThat(teachingSubject1).isEqualTo(result.getContent().get(0).getTeachingSubject());
        assertThat(teachedSubject).isEqualTo(result.getContent().get(0).getTeachedSubject());
        assertThat(minAge).isEqualTo(result.getContent().get(0).getMinAge());
        assertThat(maxAge).isEqualTo(result.getContent().get(0).getMaxAge());
        assertThat(imgUrl1).isEqualTo(result.getContent().get(0).getAvatar());

        assertThat(boardId2).isEqualTo(result.getContent().get(1).getId());
        assertThat(teachingSubject2).isEqualTo(result.getContent().get(1).getTeachingSubject());

        verify(talentRepository).findAllWithPagingAndSearch(keyword, pageable,subjectCategoryId);
    }

    @Test
    @DisplayName("재능교환 게시물 스크랩 성공 테스트")
    public void testScrap() {
        //Given
        Long boardId = 1L;
        String userId = "testUser";
        String writer = "testUser";
        String title = "testTitle";
        String content = "testContent";
        String placeName = "testPlace";
        String teachingSubject = "testTeachingSubject";
        String teachedSubject = "testTeachedSubject";
        Long minAge = 25L;
        Long maxAge = 30L;
        Set<String> selectedDays = new HashSet<>(Arrays.asList("MON", "TUE", "WED"));
        String gender = "FEMALE";
        String img1 = "img1.jpg";
        String img2 = "img2.jpg";

        User user = User.builder().id(writer).build();
        Place place = new Place(1L, placeName);
        SubjectCategory teachingSubjectCategory = new SubjectCategory(7L, teachingSubject, new SubjectCategory(1L, "parentCategory1", null));
        SubjectCategory teachedSubjectCategory = new SubjectCategory(19L, teachedSubject, new SubjectCategory(2L, "parentCategory2", null));
        Talent talent = Talent.builder().id(boardId).writer(user).place(place).teachingSubject(teachingSubjectCategory).teachedSubject(teachedSubjectCategory).title(title).content(content).maxAge(maxAge).minAge(minAge).gender(GenderForTalent.valueOf(gender)).dayOfWeek(DayOfWeekUtil.convertSelectedDaysToEnum(selectedDays)).hit(0L).build();
        TalentScrap scrap = TalentScrap.of(user, talent);

        // 현재 인증된 사용자 설정
        Authentication authentication = new TestingAuthenticationToken(userId, null, "ROLE_USER");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // userRepository의 동작을 모의화
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        //scrapRepository의 동작을 모의화
        when(scrapRepository.findByTalentIdAndUserId(boardId, userId)).thenReturn(null);
        //talentRepository의 동작을 모의화
        when(talentRepository.findById(boardId)).thenReturn(Optional.of(talent));
        //scrapRepository의 동작을 모의화
        when(scrapRepository.save(any(TalentScrap.class))).thenReturn(scrap);

        //When
        TalentDto.ResponseBasic response = talentService.scrap(boardId);

        // Then
        assertNotNull(response);
        assertThat(201).isEqualTo(response.getReturnCode());
        assertThat("스크랩이 완료되었습니다.").isEqualTo(response.getReturnMessage());

        verify(userRepository).findById(userId);
        verify(scrapRepository).findByTalentIdAndUserId(boardId, userId);
        verify(talentRepository).findById(boardId);
        verify(scrapRepository).save(any(TalentScrap.class));
    }
}