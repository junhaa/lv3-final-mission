package finalmission.global.config;

import finalmission.exception.ForbiddenException;
import finalmission.exception.InvalidInputException;
import finalmission.exception.NotFoundException;
import finalmission.exception.UnauthorizedException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    Logger LOG = LoggerFactory.getLogger(this.getClass());

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> body = new HashMap<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            body.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        LOG.warn("Validation Exception: {}", body);

        return body;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public String handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        LOG.warn(exception.getMessage());
        return "요청 형식이 올바르지 않습니다.";
    }

    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleInvalidInputException(InvalidInputException exception) {
        LOG.warn("InvalidInput Exception: {}", exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnAuthorizedException(UnauthorizedException exception) {
        LOG.warn("UnAuthorized Exception: {}", exception.getMessage());
        return "인증에 실패했습니다.";
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleForbiddenException(ForbiddenException exception) {
        LOG.warn("Forbidden Exception: {}", exception.getMessage());
        return "접근 권한이 없습니다.";
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException exception) {
        LOG.warn("NotFound Exception: {}", exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception exception) {
        LOG.error("Exception: {}", exception.getMessage());
        return "서버 내에서 오류가 발생했습니다. 관리자에게 문의해주세요.";
    }
}
