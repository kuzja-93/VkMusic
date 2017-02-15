package site.kuzja.vkmusic.api;

import site.kuzja.vkmusic.api.exceptions.ApiException;
import site.kuzja.vkmusic.api.exceptions.ClientException;
import site.kuzja.vkmusic.api.metods.AudioGet;
import site.kuzja.vkmusic.api.objects.AudioList;
import site.kuzja.vkmusic.api.objects.UserActor;
import site.kuzja.vkmusic.api.metods.DirectAuch;

public class VkApi {
    private static final String APP_ID = "3697615";
    private static final String CLIENT_SECRET = "AlVXZFMUqyrnABp8ncuU";
    private static final String API_VERSION = "5.62";

    private static final int AUDIO = 8;
    private static final int OFFLINE = 65536;

    private int scope;
    public AudioApi audio;

    public VkApi() {
        scope = AUDIO + OFFLINE;
        audio = new AudioApi(API_VERSION);
    }

    public UserActor directAuch(String username, String password) throws ClientException, ApiException {
        return new DirectAuch(APP_ID, CLIENT_SECRET, username, password, scope, API_VERSION)
                .execute();
    }

}

