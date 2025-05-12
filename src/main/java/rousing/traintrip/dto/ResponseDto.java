package rousing.traintrip.dto;

import lombok.Getter;
import java.util.Map;

@Getter
public class ResponseDto<T> {
    private boolean success;
    private T data;
    private String message;

    public ResponseDto(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(true, data, null);
    }

    public static <T> ResponseDto<T> fail(String message) {
        return new ResponseDto<>(false, null, message);
    }
    
    public static <T> ResponseDto<T> fail(String message, Map<String, String> errors) {
        return new ResponseDto<>(false, (T) errors, message);
    }
}