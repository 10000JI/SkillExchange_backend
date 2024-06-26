package place.skillexchange.backend.exception;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.UriComponentsBuilder;
import place.skillexchange.backend.common.dto.ErrorReason;
import place.skillexchange.backend.common.dto.ErrorResponse;
import place.skillexchange.backend.common.dto.ValidationException;
import place.skillexchange.backend.exception.user.UserErrorCode;

import java.util.*;

@RestController
@ControllerAdvice //AOP
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(Exception.class) //타 Controller 실행 중 Exception 에러 발생 시 handlerAllExceptions()가 작업 우회
    public final ResponseEntity<Object> handlerAllExceptions(Exception ex, WebRequest request) {

        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        String url =
                UriComponentsBuilder.fromUri(
                                new ServletServerHttpRequest(servletWebRequest.getRequest()).getURI())
                        .build()
                        .toUriString();

        //log.error("INTERNAL_SERVER_ERROR", ex);
        GlobalErrorCode internalServerError = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse =
                new ErrorResponse(
                        internalServerError.getStatus(),
                        internalServerError.getCode(),
                        internalServerError.getReason(),
                        url);
        return ResponseEntity.status(HttpStatus.valueOf(internalServerError.getStatus()))
                .body(errorResponse);
    }

    /**
     * 이메일 전송 시 오류
     */
    @ExceptionHandler(MessagingException.class) //타 Controller 실행 중 MessagingException 에러 발생 시 handlerInValidEmailException()가 작업 우회
    public final ResponseEntity<Object> handlerInValidEmailException(HttpServletRequest request) {
        ErrorResponse access_denied = new ErrorResponse(UserErrorCode.EMAIL_SEND_FAILURE.getErrorReason(), request.getRequestURI().toString());
        return new ResponseEntity(access_denied, HttpStatus.BAD_REQUEST);
    }


    /**
     * 유효성 검사 실패
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        String url =
                UriComponentsBuilder.fromUri(
                                new ServletServerHttpRequest(servletWebRequest.getRequest()).getURI())
                        .build()
                        .toUriString();

        // 에러 메시지 목록
        List<String> errorMessages = new ArrayList<>();
        for (FieldError fieldError : errors) {
            String errorMessage = messageSource.getMessage(fieldError, Locale.getDefault());
            if (errorMessage != null) {
                errorMessages.add(errorMessage);
            }
        }
        ValidationException errorResponse =
                new ValidationException(status.value(), "유효성 검사 실패", status.toString(), errorMessages, url);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


    /*    *//**
     * 이미지 크기 초과 예외
     *//*
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public final ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, WebRequest request) {
        ExceptionResponse.OneDetail exceptionResponse = new ExceptionResponse.OneDetail(new Date(), "이미지 크기가 너무 큽니다.", request.getDescription(false));
        return new ResponseEntity(exceptionResponse, HttpStatus.BAD_REQUEST);
    }*/

    /**
     * 전체 eception 처리
     */
    @ExceptionHandler(AllCodeException.class)
    public ResponseEntity<ErrorResponse> CodeExceptionHandler(
            AllCodeException e, HttpServletRequest request) {
        BaseErrorCode code = e.getErrorCode();
        ErrorReason errorReason = code.getErrorReason();
        ErrorResponse errorResponse =
                new ErrorResponse(errorReason, request.getRequestURL().toString());
        return ResponseEntity.status(HttpStatus.valueOf(errorReason.getStatus()))
                .body(errorResponse);
    }
}
