package place.skillexchange.backend.file.dto;

import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.notice.entity.Notice;
import place.skillexchange.backend.talent.entity.Talent;
import place.skillexchange.backend.user.entity.User;
import place.skillexchange.backend.file.files.UploadFile;

public class FileDto {

    /**
     * 프로필 이미지 추가시 사용할 Dto -> Entity
     */
    public static class ProfileDto{
        private String uploadFileName;
//        private String storeFileName;

        private String fileUrl;

        /* Dto -> Entity */
        public File toEntity(UploadFile uploadFile, User user) {
            File file = File.builder()
                    .oriName(uploadFile.getUploadFileName())
                    .user(user)
                    .fileUrl(uploadFile.getFileUrl())
                    .build();
            return file;
        }
    }

//    /**
//     * 프로필 이미지 수정 시 응답 Dto
//     */
//    @Getter
//    public static class ProfileResponse {
//        private String oriName;
////        private String fileName;
//        private String fileUrl;
//        private int returnCode;
//        private String returnMessage;
//
//        /* Entity -> Dto */
//        public ProfileResponse(File file, int returnCode, String returnMessage) {
//            if (file != null) {
//                this.oriName = file.getOriName();
//                this.fileUrl = file.getFileUrl();
//            } else {
//                this.oriName = null;
//                this.fileUrl = null;
//            }
//            this.returnCode = returnCode;
//            this.returnMessage = returnMessage;
//        }
//    }

    /**
    * 공지사항 이미지 추가시 사용할 Dto -> Entity,  재능교환소 게시물 이미지 추가시 사용할 Dto -> Entity
    */
    public static class EntityDto {
        private String uploadFileName;
        private String fileUrl;

        // Dto -> Entity, Object 타입의 reference를 받아 처리
        public File toEntity(UploadFile uploadFile, Object reference) {
            File.FileBuilder builder = File.builder()
                    .oriName(uploadFile.getUploadFileName())
                    .fileUrl(uploadFile.getFileUrl());

            if (reference instanceof Notice) {
                builder.notice((Notice) reference);
            } else if (reference instanceof Talent) {
                builder.talent((Talent) reference);
            }

            return builder.build();
        }
    }
}
