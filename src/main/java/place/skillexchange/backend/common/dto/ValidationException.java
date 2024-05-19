package place.skillexchange.backend.common.dto;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ValidationException {

    private final boolean success = false;
    private int status;
    private String code;
    private String message;
    private List<String> details;
    private LocalDateTime timeStamp;
    private String path;

    public ValidationException(int status, String code, String message, List<String> details, String path) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.details = details;
        this.timeStamp = LocalDateTime.now();
        this.path = path;
    }

}

