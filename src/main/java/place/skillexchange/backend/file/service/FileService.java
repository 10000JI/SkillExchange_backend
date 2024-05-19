package place.skillexchange.backend.file.service;

import org.springframework.web.multipart.MultipartFile;
import place.skillexchange.backend.file.entity.File;
import place.skillexchange.backend.notice.entity.Notice;
import place.skillexchange.backend.talent.entity.Talent;
import place.skillexchange.backend.user.entity.User;

import java.io.IOException;
import java.util.List;

public interface FileService {
    public File uploadFilePR(MultipartFile multipartFile, User user) throws IOException;

    public List<File> registerNoticeImg(List<MultipartFile> multipartFiles, Notice notice) throws IOException;

    public List<File> registerTalentImg(List<MultipartFile> multipartFiles, Talent talent) throws IOException;

    public List<File> updateNoticeImg(List<String> imgUrls, List<MultipartFile> multipartFiles, Notice notice) throws IOException;

    public List<File> updateTalentImg(List<String> imgUrl, List<MultipartFile> multipartFiles, Talent talent) throws IOException;

}
