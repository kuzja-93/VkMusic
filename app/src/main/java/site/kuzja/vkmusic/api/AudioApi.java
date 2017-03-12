package site.kuzja.vkmusic.api;

import site.kuzja.vkmusic.api.exceptions.ApiException;
import site.kuzja.vkmusic.api.exceptions.ClientException;
import site.kuzja.vkmusic.api.metods.AudioGet;
import site.kuzja.vkmusic.api.objects.AudioList;

/**
 * Класс групперующий все методы для работы с аудио
 */

public class AudioApi {
    private String version;
    AudioApi(String version)
    {
        this.version = version;
    }
    public AudioList get(int ownerId, String accessToken) throws ClientException, ApiException {
        return new AudioGet(ownerId, accessToken, version).execute();
    }
}
