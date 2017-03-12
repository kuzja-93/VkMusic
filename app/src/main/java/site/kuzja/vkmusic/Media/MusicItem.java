package site.kuzja.vkmusic.Media;

import site.kuzja.vkmusic.api.objects.Audio;

/**
 * Адаптер для аудиозаписи
 */

public class MusicItem {
    public static final int STATUS_STOPPED = 0;
    public static final int STATUS_PAUSED = 1;
    public static final int STATUS_PLAYING = 2;
    public static final int STATUS_PREPARING = 3;

    public static final int DOWNLOADING = -1;
    public static final int DOWNLOADED = -2;
    public static final int NOT_DOWNLOADED = -3;


    private int status = STATUS_STOPPED;
    private int downloadingStatus = NOT_DOWNLOADED;
    private int downloadingProgress = 0;

    private Audio audio;
    private String fileName;

    public MusicItem(Audio audio, String fileName) {
        this.audio = audio;
        this.fileName = fileName;
    }
    public String getArtist() {
        return audio.getArtist();
    }

    public String getTitle() {
        return audio.getTitle();
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        if (status >= STATUS_STOPPED && status <= STATUS_PREPARING)
        this.status = status;
    }

    public int getDownloadingProgress() {
        return downloadingProgress;
    }

    public void setDownloadingProgress(int progress) {
        downloadingProgress = progress;
    }

    public int getDownloadingStatus() {
        return downloadingStatus;
    }

    public void setDownloadingStatus(int status) {
        if (status <= DOWNLOADING && status >= NOT_DOWNLOADED)
            downloadingStatus = status;
    }

    public int getId() {
        return audio.getId();
    }

    public String getUrl() {
        return audio.getUrl();
    }

    public String getFileName() {
        return fileName;
    }
}
