package site.kuzja.vkmusic.metods;

import site.kuzja.vkmusic.objects.UserActor;

public class OAuch extends AbstractMethod<UserActor> {

    private static final String AUCH_URL = "https://oauth.vk.com/token";
    public OAuch(String clientID, String clientSecret, String userName,
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
