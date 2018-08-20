package example.app;

import example.exception.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired
    MessageSource messageSource;

    // == 예외클래스와 오류메시지를 패밍 ==
    private final Map<Class<? extends Exception>, String> messageMappings =
            Collections.unmodifiableMap(new LinkedHashMap() {
                {
                    put(HttpMessageNotReadableException.class, "Request body is invalid");
                    put(MethodArgumentNotValidException.class, "Request value is invalid");
                }
            });

    // == 오류메세지를 가져오기 위한 메서드 ==
    private String resolveMessage(Exception ex, String defaultMessage) {
        return messageMappings.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(ex.getClass()))
                .findFirst().map(Map.Entry::getValue).orElse(defaultMessage);
    }

    // == REST 에러 생성 ==
    private ApiError createApiError(Exception ex, String defaultMessage) {
        ApiError apiError = new ApiError();
        apiError.setMessage(resolveMessage(ex, defaultMessage));
        apiError.setDocumentationUrl("http://localhost:8080/api/errors");
        return apiError;
    }

    // == REST API 오류 캐치 ==
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        ApiError apiError = createApiError(ex, ex.getMessage());
        return super.handleExceptionInternal(ex, apiError, headers, status, request);
    }

    // == 시스템 예외 처리 ==
    @ExceptionHandler
    public ResponseEntity<Object> handleSystemException(Exception ex, WebRequest request) {
        ApiError apiError = createApiError(ex, "System error is occured");
        return super.handleExceptionInternal(ex, apiError, null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // == REST API Valid 체크 (상세 정보 처리) ==
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        ApiError apiError = createApiError(ex, ex.getMessage());
        ex.getBindingResult().getGlobalErrors().stream()
                .forEach(e -> apiError.addDetail(e.getObjectName(), getMessage(e, request)));
        ex.getBindingResult().getFieldErrors().stream()
                .forEach(e -> apiError.addDetail(e.getField(), getMessage(e, request)));
        return super.handleExceptionInternal(ex, apiError, headers, status, request);
    }

    public String getMessage(MessageSourceResolvable resolvable, WebRequest request) {
        return messageSource.getMessage(resolvable, request.getLocale());
    }
}
