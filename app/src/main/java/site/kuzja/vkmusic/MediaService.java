package site.kuzja.vkmusic;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

import site.kuzja.vkmusic.api.objects.Audio;

interface UpdateImpl {
    void update();
    void playNextItem(Audio item);
}

public class MediaService implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener{
    private MediaPlayer mMediaPlayer = null;
    private Audio currentItem = null;
    private UpdateImpl updateImpl = null;

    private static final String LOG_TAG = "MediaService";

    private void updateStatus(Audio item, int status) {
        item.setStatus(status);
        if (updateImpl != null)
            updateImpl.update();
    }

    public MediaService(UpdateImpl impl) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        updateImpl = impl;
    }

    public boolean setAudioItem(Audio item) {
        if (currentItem != null &&
                currentItem.getId() == item.getId()) {
            if (currentItem.getStatus() == Audio.STATUS_PLAYING) {
                mMediaPlayer.pause();
                updateStatus(currentItem, Audio.STATUS_PAUSED);
            } else if (currentItem.getStatus() == Audio.STATUS_PAUSED) {
                mMediaPlayer.start();
                updateStatus(currentItem, Audio.STATUS_PLAYING);
            }
        } else {
            Log.d(LOG_TAG, "start HTTP");
            if (currentItem != null)
                updateStatus(currentItem, Audio.STATUS_STOPED);
            mMediaPlayer.reset();
            try {
                mMediaPlayer.setDataSource(item.getUrl());
            } catch (IOException e) {
                return false;
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Log.d(LOG_TAG, "prepareAsync");
            currentItem = item;
            updateStatus(currentItem, Audio.STATUS_PREPARING);
            mMediaPlayer.prepareAsync();
        }
        return true;
    }

    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        updateStatus(currentItem, Audio.STATUS_STOPED);
        updateImpl.playNextItem(currentItem);
    }

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
        updateStatus(currentItem, Audio.STATUS_PLAYING);
    }
}
