package site.kuzja.vkmusic;

import site.kuzja.vkmusic.objects.Error;

public class ExceptionMapper {
    public static ApiException parseException(Error error) {
        switch (error.getErrorCode()) {
            default:
                return new ApiException(error.getErrorCode(), error.getErrorMsg());
        }
    }
}
