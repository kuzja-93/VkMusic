package site.kuzja.vkmusic;

import site.kuzja.vkmusic.metods.AudioGet;
import site.kuzja.vkmusic.objects.AudioList;
import site.kuzja.vkmusic.objects.UserActor;
import site.kuzja.vkmusic.metods.OAuch;

public class VkApi {
    private static final String APP_ID = "3697615";
    private static final String CLIENT_SECRET = "AlVXZFMUqyrnABp8ncuU";
    private static final String API_VERSION = "5.62";

    private static final int AUDIO = 8;
    private static final int OFFLINE = 65536;

    private int scope;

    public VkApi() {
        scope = AUDIO + OFFLINE;
    }

    public UserActor auch(String username, String password) throws ClientException, ApiException {
        return new OAuch(APP_ID, CLIENT_SECRET, username, password, scope, API_VERSION)
                .execute();
    }
    public AudioList audioGet(String ownerId, String accessToken) throws ClientException, ApiException {
        return new AudioGet(ownerId, accessToken, API_VERSION).execute();
    }

}

