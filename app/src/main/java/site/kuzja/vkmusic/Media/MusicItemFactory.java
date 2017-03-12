package site.kuzja.vkmusic.Media;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import site.kuzja.vkmusic.api.objects.Audio;
import site.kuzja.vkmusic.api.objects.AudioList;

/**
 * Фабрика создания списка MusicItem из AudioList
 */

public class MusicItemFactory {
    private final static String DIRECTORY_VK = "Vk";
    public static List<MusicItem> getMusicItemsList(AudioList list) {
        List<MusicItem> resultList = new ArrayList<>();
        for (Audio audio:list.getItems()) {
            String fileName = String.format("%s/%s/%s/%s - %s.mp3",
                    Environment.getExternalStorageDirectory().toString(),
                    Environment.DIRECTORY_MUSIC,
                    DIRECTORY_VK,
                    audio.getArtist(),
                    audio.getTitle());
            MusicItem item = new MusicItem(audio, fileName);
            if (new File(fileName).exists()) {
                Log.d("MusicItemFactory", "Файл обнаружен в файловой системе");
                item.setDownloadingStatus(MusicItem.DOWNLOADED);
            }
            Log.d("MusicItemFactory", String.format("Файл %s - %sзагружен",
                    fileName,
                    item.getDownloadingStatus() == MusicItem.DOWNLOADED ? "" : "не "));
            resultList.add(item);
        }
        return resultList;
    }
}
