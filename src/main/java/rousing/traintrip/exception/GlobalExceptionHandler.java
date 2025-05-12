package rousing.traintrip.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import rousing.traintrip.common.ApiResponse;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * 애플리케이션 전역의 예외를 처리하는 핸들러입니다.
 * REST 컨트롤러에서 발생하는 예외를 일관된 형식으로 처리합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 리소스를 찾을 수 없는 예외를 처리합니다.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Void> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    /**
     * 중복 리소스 예외를 처리합니다.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<Void> handleDuplicateResourceException(DuplicateResourceException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    /**
     * 잘못된 요청 예외를 처리합니다.
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBadRequestException(BadRequestException ex) {
        return ApiResponse.error(ex.getMessage());
    }

    /**
     * 파일 업로드 관련 예외를 처리합니다.
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleIOException(IOException ex) {
        return ApiResponse.error("파일 처리 중 오류가 발생했습니다: " + ex.getMessage());
    }

    /**
     * 파일 크기 초과 예외를 처리합니다.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ApiResponse<Void> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        return ApiResponse.error("파일 크기가 허용된 최대 크기를 초과했습니다.");
    }

    /**
     * 유효성 검사 예외를 처리합니다.
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationExceptions(Exception ex) {
        String errorMessage;
        
        if (ex instanceof MethodArgumentNotValidException) {
            errorMessage = ((MethodArgumentNotValidException) ex).getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        } else {
            errorMessage = ((BindException) ex).getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
        }
        
        return ApiResponse.error("유효성 검증 실패: " + errorMessage);
    }

    /**
     * 제약 조건 위반 예외를 처리합니다.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleConstraintViolation(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));
        
        return ApiResponse.error("제약 조건 위반: " + errorMessage);
    }

    /**
     * 접근 거부 예외를 처리합니다.
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Void> handleAccessDeniedException(AccessDeniedException ex) {
        return ApiResponse.error("접근 권한이 없습니다.");
    }

    /**
     * 기타 모든 예외를 처리합니다.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleAllUncaughtException(Exception ex) {
        return ApiResponse.error("서버 내부 오류가 발생했습니다: " + ex.getMessage());
    }
}
