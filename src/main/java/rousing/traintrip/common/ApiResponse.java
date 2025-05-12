package rousing.traintrip.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private String error;

    /**
     * 성공 응답을 생성합니다.
     *
     * @param data    응답 데이터
     * @param message 성공 메시지
     * @return 성공 응답 객체
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message, null);
    }

    /**
     * 데이터만 포함된 성공 응답을 생성합니다.
     *
     * @param data 응답 데이터
     * @return 성공 응답 객체
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "성공적으로 처리되었습니다.");
    }

    /**
     * 메시지만 포함된 성공 응답을 생성합니다.
     *
     * @param message 성공 메시지
     * @return 성공 응답 객체
     */
    public static <T> ApiResponse<T> success(String message) {
        return success(null, message);
    }

    /**
     * 에러 응답을 생성합니다.
     *
     * @param error   에러 코드
     * @param message 에러 메시지
     * @return 에러 응답 객체
     */
    public static <T> ApiResponse<T> error(String error, String message) {
        return new ApiResponse<>(false, null, message, error);
    }

    /**
     * 메시지만 포함된 에러 응답을 생성합니다.
     *
     * @param message 에러 메시지
     * @return 에러 응답 객체
     */
    public static <T> ApiResponse<T> error(String message) {
        return error("ERROR", message);
    }
}
