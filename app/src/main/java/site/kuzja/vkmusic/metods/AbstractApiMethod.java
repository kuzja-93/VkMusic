package site.kuzja.vkmusic.metods;

import java.lang.reflect.Type;

public abstract class AbstractApiMethod <T> extends AbstractMethod <T> {

    private static final String API_URL = "https://api.vk.com/method/";
    public AbstractApiMethod(String method, String accessToken,
                             String version, Type responseClass) {
        super(API_URL + method, responseClass);
        accessToken(accessToken);
        version(version);
    }
}
