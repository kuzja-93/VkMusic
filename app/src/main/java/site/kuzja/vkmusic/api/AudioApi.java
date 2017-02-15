package site.kuzja.vkmusic.api;

import site.kuzja.vkmusic.api.exceptions.ApiException;
import site.kuzja.vkmusic.api.exceptions.ClientException;
import site.kuzja.vkmusic.api.metods.AudioGet;
import site.kuzja.vkmusic.api.objects.AudioList;

/**
 * Created by user on 15.02.17.
 */

public class AudioApi {
    String version;
    public AudioApi(String version)
    {
        this.version = version;
    }
    public AudioList get(String ownerId, String accessToken) throws ClientException, ApiException {
        return new AudioGet(ownerId, accessToken, version).execute();
    }
}
