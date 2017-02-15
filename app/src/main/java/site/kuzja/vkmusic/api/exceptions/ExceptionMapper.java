package site.kuzja.vkmusic.api.exceptions;

import site.kuzja.vkmusic.api.exceptions.ApiException;
import site.kuzja.vkmusic.api.objects.Error;

/**
 * Генератор иcключений из ошибок api
 */
public class ExceptionMapper {
    public static ApiException parseException(Error error) {
        switch (error.getErrorCode()) {
            default:
                return new ApiException(error.getErrorCode(), error.getErrorMsg());
        }
    }
}
