package site.kuzja.vkmusic.api.metods;

import java.lang.reflect.Type;

abstract class AbstractApiMethod <T> extends AbstractMethod <T> {
    private static final String API_URL = "https://api.vk.com/method/";
    AbstractApiMethod(String method, String accessToken,
                      String version, Type responseClass) {
        super(API_URL + method, responseClass);
        accessToken(accessToken);
        version(version);
    }
}
