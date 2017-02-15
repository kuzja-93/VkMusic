package site.kuzja.vkmusic.api.exceptions;

/**
 * Created by user on 14.02.17.
 */
public class ClientException extends Exception {
    private String description;

    private String message;


    public ClientException(String description, String message) {
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
