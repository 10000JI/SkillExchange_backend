package place.skillexchange.backend.common.util;

import place.skillexchange.backend.talent.dto.TalentDto;

import java.util.*;

public class DummyDataGenerator{

    // 장소 목록
    private static final List<String> places = Arrays.asList(
            "강남", "신사", "잠실", "홍대", "신촌", "사당", "신림", "구로", "여의도",
            "강서", "종로", "명동", "대학로", "건대", "노원", "천호", "구리", "하남",
            "남양주", "분당", "판교", "일산", "인천", "부천", "수원", "동탄", "용인",
            "안양", "안산", "의정부", "대전", "천안", "청주", "충주", "세종", "원주",
            "춘천", "강릉", "대구", "부산", "울산", "진주", "포항", "전주", "군산",
            "광주", "제주", "서귀포", "온라인"
    );

    // 주제 카테고리 목록
    private static final List<String> subjectCategories = Arrays.asList(
            "영어", "일어", "중국어", "프랑스어", "독일어", "스페인어", "한국어",
            "러시아어", "이테리어", "태국어", "베트남어", "그리스어", "피아노",
            "재즈", "기타", "베이스", "바이올린", "첼로", "플롯", "오보에", "색소폰",
            "드럼", "클라리넷", "우크렐러", "국악", "보컬", "성악", "작곡", "화성악",
            "미디", "미술", "디자인", "사진", "일러스트", "영상", "요리", "커피",
            "켈라그라피", "발레", "연기", "뷰티", "공예", "의상", "프로그래밍",
            "포토샵", "3D/캐드", "쇼핑몰", "MS오피스", "수영", "헬스", "요가",
            "골프", "테니스", "배드민턴", "승마", "스키", "스노우보드", "볼링", "스쿠버다이빙",
            "운전", "심리학", "수학", "역사학", "의학", "마케팅", "경제학",
            "물리학", "철학"
    );

    // 더미 데이터 생성 메서드
    public static TalentDto.TalentRegisterRequest generateDummyData(String responseUser) {
        Random random = new Random();

        // 장소 선택
        String placeName = places.get(random.nextInt(places.size()));

        // 가르칠 주제 선택
        String teachingSubject = getRandomSubject();

        // 가르쳐 받을 주제 선택
        String teachedSubject = getRandomSubject();

        // 요일 선택
        Set<String> selectedDays = new HashSet<>(Arrays.asList("MON", "WED"));

        // 연령대 설정
        Long minAge = 20L;
        Long maxAge = 30L;

        // 성별 설정
        String gender = "UNKNOWN";

        // 제목 및 내용 설정
        String title = teachingSubject + " 가르쳐줄 선생님 구해요~ " + teachedSubject + " 알려드릴게요";
        String content = teachedSubject + " 중급정도 실력 됩니다. 전 " + teachingSubject + "을 잘 못쳐서 ㅠ! "
                +" 끝나고 작업실에서 " + teachingSubject + " 배워요! " +
                teachedSubject + "는 대면으루 작업실에서 가르쳐줄 생각입니다";

        // 작성자 설정
        String writer = responseUser;

        // 더미 데이터 객체 생성
        return TalentDto.TalentRegisterRequest.builder()
                .writer(writer)
                .placeName(placeName)
                .teachingSubject(teachingSubject)
                .teachedSubject(teachedSubject)
                .selectedDays(selectedDays)
                .minAge(minAge)
                .maxAge(maxAge)
                .gender(gender)
                .title(title)
                .content(content)
                .build();
    }

    // 무작위 주제 선택 메서드
    private static String getRandomSubject() {
        Random random = new Random();
        return subjectCategories.get(random.nextInt(subjectCategories.size()));
    }

}
