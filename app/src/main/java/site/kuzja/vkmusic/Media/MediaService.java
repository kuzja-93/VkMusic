package site.kuzja.vkmusic.Media;

import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnCompletionListener;

import java.io.IOException;

import site.kuzja.vkmusic.interfaces.OnPlayNextItem;
import site.kuzja.vkmusic.interfaces.OnPlayPreviousItem;
import site.kuzja.vkmusic.interfaces.OnUIUpdateListener;

public class MediaService implements OnPreparedListener,
        OnCompletionListener, OnAudioFocusChangeListener{
    //Слушатели
    private OnUIUpdateListener onUIUpdateListener;
    private OnPlayNextItem onPlayNextItem;
    private OnPlayPreviousItem onPlayPreviousItem;

    private MediaPlayer mMediaPlayer;
    private MusicItem currentItem;
    private AudioManager mAudioManager;
    private boolean needFocus = true;

    //private static final String LOG_TAG = "MediaService";

    public MusicItem getCurretItem() {
        return currentItem;
    }
    private void updateStatus(int status) {
        if (currentItem == null)
            return;
        currentItem.setStatus(status);
        if (onUIUpdateListener != null)
            onUIUpdateListener.updateUI();
    }

    public MediaService(AudioManager audioManager) {
        mMediaPlayer = new MediaPlayer();
        mAudioManager = audioManager;
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    public void setOnUIUpdateListener(OnUIUpdateListener onUIUpdateListener) {
        this.onUIUpdateListener = onUIUpdateListener;
    }
    public void setOnPlayNextItem(OnPlayNextItem onPlayNextItem) {
        this.onPlayNextItem = onPlayNextItem;
    }
    public void setOnPlayPreviousItem(OnPlayPreviousItem onPlayPreviousItem) {
        this.onPlayPreviousItem = onPlayPreviousItem;
    }
    public void release() {
        updateStatus(MusicItem.STATUS_STOPPED);
        mAudioManager.abandonAudioFocus(this);
        currentItem = null;
        mMediaPlayer.release();
    }

    public void playNext() {
        abandonAudioFocus();
        if (onPlayNextItem != null && currentItem != null)
            onPlayNextItem.playNextItem(currentItem);

    }

    public void playPrevious() {
        abandonAudioFocus();
        if (onPlayPreviousItem != null && currentItem != null)
            onPlayPreviousItem.playPreviousItem(currentItem);

    }

    public void pause(boolean resetFocus) {
        mMediaPlayer.pause();
        updateStatus(MusicItem.STATUS_PAUSED);
        if (resetFocus)
            abandonAudioFocus();

    }

    public void play() {
        if (!mMediaPlayer.isPlaying() &&
                requestAudioFocus()) {
            mMediaPlayer.start();
            updateStatus(MusicItem.STATUS_PLAYING);
        }
    }

    private boolean requestAudioFocus() {
        if (!needFocus)
            return true;
        int requestResult = mAudioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        boolean success = requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        needFocus = !success;
        return success;
    }

    private void abandonAudioFocus() {
        if (needFocus)
            return;

        mAudioManager.abandonAudioFocus(this);

    }
    public boolean setMusicItem(MusicItem item) {
        if (currentItem != null &&
                currentItem.getId() == item.getId()) {
            if (currentItem.getStatus() == MusicItem.STATUS_PLAYING)
                pause(true);
            else if (currentItem.getStatus() == MusicItem.STATUS_PAUSED)
                play();
        } else {
            updateStatus(MusicItem.STATUS_STOPPED);
            mMediaPlayer.reset();
            abandonAudioFocus();

            if (!requestAudioFocus())
                return false;
            try {
                mMediaPlayer.setDataSource(item.getDownloadingStatus() == MusicItem.DOWNLOADED ?
                        item.getFileName() : item.getUrl());
            } catch (IOException e) {
                return false;
            }
            currentItem = item;
            updateStatus(MusicItem.STATUS_PREPARING);
            mMediaPlayer.prepareAsync();
        }
        return true;
    }



    @Override
    public void onCompletion(MediaPlayer mp) {
        updateStatus(MusicItem.STATUS_STOPPED);
        playNext();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        play();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                pause(false);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pause(false);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mMediaPlayer.setVolume(0.5f, 0.5f);
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                play();
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;
        }
    }
}
