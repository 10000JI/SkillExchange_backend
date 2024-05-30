package place.skillexchange.backend.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.notice.entity.Notice;
import place.skillexchange.backend.user.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NoticeDto {

    /**
     * 게시물 등록 시 요청된 Dto
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "공지사항 등록 요청을 위한 도메인 객체")
    public static class NoticeRegisterRequest {

        @NotBlank(message = "작성자: 필수 정보입니다.")
        private String writer;

        @NotBlank(message = "제목: 필수 정보입니다.")
        private String title;

        @NotBlank(message = "내용: 필수 정보입니다.")
        private String content;

        /* Dto -> Entity */
        public Notice toEntity(User user) {
            Notice notice = Notice.builder()
                    .writer(user)
                    .title(title)
                    .content(content)
                    .hit(0L)
                    .build();
            return notice;
        }
    }

    /**
     * 게시물 등록 성공시 보낼 Dto
     */
    @Getter
    public static class NoticeRegisterResponse {
        private Long id;
        private String writer;
        private String title;
        private String content;
        private LocalDateTime regDate;
        private LocalDateTime modDate;
        private List<String> oriName = new ArrayList<>();
        private List<String> imgUrl = new ArrayList<>();
        private int returnCode;
        private String returnMessage;

        /* Entity -> Dto */
        public NoticeRegisterResponse(User user, List<File> files , Notice notice, int returnCode, String returnMessage) {
            this.writer = user.getId();
            this.id = notice.getId();
            this.title = notice.getTitle();
            this.content = notice.getContent();
            this.regDate = notice.getRegDate();
            this.modDate = notice.getModDate();


            if (files != null && !files.isEmpty()) {
                for (File file : files) {
                    this.oriName.add(file.getOriName());
                    this.imgUrl.add(file.getFileUrl());
                }
            } else {
                this.oriName = null;
                this.imgUrl = null;
            }
            this.returnCode = returnCode;
            this.returnMessage = returnMessage;
        }
    }

    /**
     * 게시물 조회 성공시 보낼 Dto
     */
    @Getter
    public static class NoticeReadResponse {
        private Long id;
        private String writer;
        private String avatar;
        private String title;
        private String content;
        private LocalDateTime regDate;
        private LocalDateTime modDate;
        private List<String> imgUrl = new ArrayList<>();
        private Long hit;

        /* Entity -> Dto */
        public NoticeReadResponse(Notice notice) {
            this.writer = notice.getWriter().getId();
            if (notice.getWriter() != null && notice.getWriter().getFile() != null) {
                this.avatar = notice.getWriter().getFile().getFileUrl(); //조회 한번 더 일어남
            } else {
                this.avatar = null;
            }
            this.id = notice.getId();
            this.title = notice.getTitle();
            this.content = notice.getContent();
            this.regDate = notice.getRegDate();
            this.modDate = notice.getModDate();
            this.hit = notice.getHit();


            if (!notice.getFiles().isEmpty()) {
                //자바8 람다식 + forEach 메서드
                notice.getFiles().forEach(file -> this.imgUrl.add(file.getFileUrl()));
            } else {
                this.imgUrl = null;
            }
        }
    }

    /**
     * 게시물 수정 시 요청된 Dto
     */
    @Getter
    @Builder
    public static class NoticeUpdateRequest {

        @NotBlank(message = "작성자: 필수 정보입니다.")
        private String writer;

        @NotBlank(message = "제목: 필수 정보입니다.")
        private String title;

        @NotBlank(message = "내용: 필수 정보입니다.")
        private String content;

        private List<String> imgUrl = new ArrayList<>();

    }

    /**
     * 게시물 수정 성공시 보낼 Dto
     */
    @Getter
    public static class NoticeUpdateResponse {
        private Long id;
        private String writer;
        private String title;
        private String content;
        private LocalDateTime regDate;
        private LocalDateTime modDate;
        private List<String> oriName = new ArrayList<>();
        private List<String> imgUrl = new ArrayList<>();
        private int returnCode;
        private String returnMessage;

        /* Entity -> Dto */
        public NoticeUpdateResponse(User user, List<File> files , Notice notice, int returnCode, String returnMessage) {
            this.writer = user.getId();
            this.id = notice.getId();
            this.title = notice.getTitle();
            this.content = notice.getContent();
            this.regDate = notice.getRegDate();
            this.modDate = notice.getModDate();


            if (files != null && !files.isEmpty()) {
                for (File file : files) {
                    this.oriName.add(file.getOriName());
                    this.imgUrl.add(file.getFileUrl());
                }
            } else {
                //이미지 미첨부 시
                this.oriName = null;
                this.imgUrl = null;
            }
            this.returnCode = returnCode;
            this.returnMessage = returnMessage;
        }
    }

    /**
     * 응답코드, 응답메세지
     */
    @Getter
    @AllArgsConstructor
    public static class ResponseBasic {
        private int returnCode;
        private String returnMessage;
    }


    /**
     * 게시물 목록 응답 Dto
     */
    @Getter
    @AllArgsConstructor
    public static class NoticeListResponse {
        private Long id;
        private String writer;
        private String title;
        private Long hit;
        private Long commentCount; // 댓글 개수 필드 추가
        private LocalDateTime regDate;

        public NoticeListResponse(Notice notice, Long commentCount) {
            this.id = notice.getId();
            this.writer = notice.getWriter().getId();
            this.title = notice.getTitle();
            this.hit = notice.getHit();
            this.commentCount = commentCount;
            this.regDate = notice.getRegDate();
        }
    }
}
