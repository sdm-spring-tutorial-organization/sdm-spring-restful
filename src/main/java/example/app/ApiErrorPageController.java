package example.app;

import example.exception.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@RestController
public class ApiErrorPageController {

    @RequestMapping("/error")
    public ApiError handleError(HttpServletRequest request) {

        String message;
        Exception ex = (Exception) request
                .getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Integer statusCode = (Integer) request
                .getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(ex != null) {
            message = ex.getMessage();
        } else {
            if(Arrays.asList(HttpStatus.values()).stream()
                    .anyMatch(status -> status.value() == statusCode)) {
                message = HttpStatus.valueOf(statusCode).getReasonPhrase();
            } else {
                message = "Custom error(" + statusCode + ") is occured";
            }
        }

        ApiError apiError = new ApiError();
        apiError.setMessage(message);
        apiError.setDocumentationUrl("http://example.com/api/errors");
        return apiError;
    }
}
