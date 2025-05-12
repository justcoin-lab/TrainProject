package rousing.traintrip.exception;

/**
 * 잘못된 요청 파라미터가 제공되었을 때 발생하는 예외
 */
public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
    
    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}