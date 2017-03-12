package site.kuzja.vkmusic.api.metods;

import site.kuzja.vkmusic.api.objects.UserActor;

/**
 * Прямая авторизация (доступно только разрешенным приложениям)
 */
public class DirectAuth extends AbstractMethod<UserActor> {

    private static final String AUTH_URL = "https://oauth.vk.com/token";
    public DirectAuth(String clientID, String clientSecret, String userName,
                      String password, int scope, String version) {
        super(AUTH_URL, UserActor.class);
        addParam("grant_type", "password");
        addParam("client_id", clientID);
        addParam("client_secret", clientSecret);
        addParam("username", userName);
        addParam("password", password);
        addParam("scope", scope);
        version(version);
    }
}
