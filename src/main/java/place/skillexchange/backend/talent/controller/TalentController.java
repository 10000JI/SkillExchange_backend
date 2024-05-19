package place.skillexchange.backend.talent.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import place.skillexchange.backend.talent.dto.TalentDto;
import place.skillexchange.backend.talent.service.TalentService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/talent/")
public class TalentController {

    private final TalentService talentService;

    /**
     * 재능교환 게시물 등록
     */
    @PostMapping("/register")
    public ResponseEntity<TalentDto.TalentRegisterResponse> register(@Validated @RequestPart("talentDto") TalentDto.TalentRegisterRequest dto, @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(talentService.register(dto, multipartFiles));
    }

    /**
     * 게시물 올린 글쓴이의 프로필 정보 불러오기
     */
    @GetMapping("/writerInfo/{talentId}")
    public TalentDto.WriterInfoResponse writerInfo(@PathVariable Long talentId) {
        return talentService.writerInfo(talentId);
    }

    /**
     * 게시물 정보 불러오기
     */
    @GetMapping("/{talentId}")
    public TalentDto.TalentReadResponse read(@PathVariable Long talentId) {
        return talentService.read(talentId);
    }

    /**
     * 게시물 정보 수정
     */
    @PatchMapping("/{talentId}")
    public TalentDto.TalentUpdateResponse update(@Validated @RequestPart("talentDto") TalentDto.TalentUpdateRequest dto, @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles, @PathVariable Long talentId) throws IOException {
        return talentService.update(dto, multipartFiles, talentId);
    }

    /**
     * 게시물 삭제
     */
    @DeleteMapping("/{talentId}")
    public TalentDto.ResponseBasic delete(@PathVariable Long talentId) {
        return talentService.delete(talentId);
    }

    /**
     * 카테고리 별 게시물 목록
     */
    @GetMapping("/list")
    public ResponseEntity<Page<TalentDto.TalentListResponse>> list(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long subjectCategoryId) {
        Page<TalentDto.TalentListResponse> talent = talentService.list(limit, skip, keyword, subjectCategoryId);

        return ResponseEntity.ok(talent);
    }

    /**
     * 게시물 스크랩
     */
    @PostMapping("/scrap/{talentId}")
    public ResponseEntity<TalentDto.ResponseBasic> scrap(@PathVariable Long talentId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(talentService.scrap(talentId));
    }
}
