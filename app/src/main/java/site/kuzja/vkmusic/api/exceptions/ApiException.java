package site.kuzja.vkmusic.api.exceptions;

/**
 * Базовое исключение при работе с апи
 */
public class ApiException extends Exception{
    private String description;

    private String message;

    private Integer code;

    private ApiException(Integer code, String description, String message) {
        this.description = description;
        this.code = code;
        this.message = message;
    }

    ApiException(Integer code, String message) {
        this.description = "Unknown";
        this.code = code;
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return description + " (" + code + "): " + message;
    }
}
