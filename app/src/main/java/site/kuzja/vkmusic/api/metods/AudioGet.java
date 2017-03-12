package site.kuzja.vkmusic.api.metods;

import site.kuzja.vkmusic.api.objects.AudioList;

/**
 * Метод получения списка аудиозаписей
 */

public class AudioGet extends AbstractApiMethod<AudioList>{
    public AudioGet(int ownerId, String accessToken, String version) {
        super("audio.get", accessToken, version, AudioList.class);
        addParam("owner_id", ownerId);
    }
}
