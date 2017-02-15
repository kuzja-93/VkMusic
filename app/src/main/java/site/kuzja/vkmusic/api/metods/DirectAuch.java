package site.kuzja.vkmusic.api.metods;

import site.kuzja.vkmusic.api.objects.UserActor;

public class DirectAuch extends AbstractMethod<UserActor> {

    private static final String AUCH_URL = "https://oauth.vk.com/token";
    public DirectAuch(String clientID, String clientSecret, String userName,
                 String password, int scope, String version) {
        super(AUCH_URL, UserActor.class);
        addParam("grant_type", "password");
        addParam("client_id", clientID);
        addParam("client_secret", clientSecret);
        addParam("username", userName);
        addParam("password", password);
        addParam("scope", scope);
        version(version);
    }
}
