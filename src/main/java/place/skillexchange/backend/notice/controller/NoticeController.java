package place.skillexchange.backend.notice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import place.skillexchange.backend.notice.dto.NoticeDto;
import place.skillexchange.backend.notice.service.NoticeService;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/notices/")
@Tag(name = "notice-controller", description = "공지사항 컨트롤러입니다.")
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 등록
     */
    @Operation(summary = "공지사항 등록 API", description = "noticeDto: 게시물 필드 요소들(application/json), files: 업로드 할 이미지들(multipart/form-data)")
    @PostMapping("/register")
    public ResponseEntity<NoticeDto.NoticeRegisterResponse> register(@Validated @RequestPart("noticeDto") NoticeDto.NoticeRegisterRequest dto, @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeService.register(dto, multipartFiles));
    }

    /**
     * 공지사항 조회
     */
    @Operation(summary = "공지사항 조회 API", description = "noticeId를 이용해서 게시물을 조회합니다.")
    @GetMapping("/{noticeId}")
    public NoticeDto.NoticeReadResponse read(@Parameter(description = "게시물 번호", required = true, example = "1") @PathVariable Long noticeId) {
        return noticeService.read(noticeId);
    }

    /**
     * 공지사항 수정
     */
    @Operation(summary = "공지사항 수정 API", description = "noticeId를 이용해서 게시물을 수정합니다. noticeDto: 게시물 필드 요소들(application/json), files: 업로드 할 이미지들(multipart/form-data)")
    @PatchMapping("/{noticeId}")
    public NoticeDto.NoticeUpdateResponse update(@Validated @RequestPart("noticeDto") NoticeDto.NoticeUpdateRequest dto, @RequestPart(value = "files", required = false) List<MultipartFile> multipartFiles, @Parameter(description = "게시물 번호", required = true, example = "1") @PathVariable Long noticeId) throws IOException {
        return noticeService.update(dto, multipartFiles, noticeId);
    }

    /**
     * 공지사항 삭제
     */
    @Operation(summary = "공지사항 삭제 API", description = "noticeId를 이용해서 게시물을 삭제합니다.")
    @DeleteMapping("/{noticeId}")
    public NoticeDto.ResponseBasic delete(@Parameter(description = "게시물 번호", required = true, example = "1") @PathVariable Long noticeId)  {
        return noticeService.delete(noticeId);
    }

    /**
     * 공지사랑 목록
     */
    @Operation(summary = "공지사항 목록 API", description = "디폴트로 공지사항 목록을 10개씩 출력하며, 페이징 처리 및 검색어 처리가 가능합니다.")
    @GetMapping("/list")
    public ResponseEntity<Page<NoticeDto.NoticeListResponse>> getNotices(
            @Parameter(description = "게시물 개수", required = false, example = "1") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "페이지 번호-1", required = false, example = "1") @RequestParam(defaultValue = "0") int skip,
            @Parameter(description = "키워드", required = false, example = "1") @RequestParam(required = false) String keyword) {

        Page<NoticeDto.NoticeListResponse> notices = noticeService.getNotices(limit, skip, keyword);

        return ResponseEntity.ok(notices);
    }
}