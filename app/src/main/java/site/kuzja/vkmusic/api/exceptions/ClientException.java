package site.kuzja.vkmusic.api.exceptions;

/**
 * "Клиентское" исключение
 */
public class ClientException extends Exception {
    private String description;

    private String message;


    private ClientException(String description, String message) {
        this.description = description;
        this.message = message;
    }

    public ClientException(String message) {
        this("Unknown", message);
    }

    @Override
    public String getMessage() {
        return description + " " + message;
    }
}
