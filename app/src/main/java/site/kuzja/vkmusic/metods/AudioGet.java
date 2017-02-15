package site.kuzja.vkmusic.metods;

import site.kuzja.vkmusic.objects.AudioList;

/**
 * Created by user on 14.02.17.
 */

public class AudioGet extends AbstractApiMethod<AudioList>{

    public AudioGet(String ownerId, String accessToken, String version) {
        super("audio.get", accessToken, version, AudioList.class);
        addParam("owner_id", ownerId);
    }
}
