package site.kuzja.vkmusic.Media;

import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnCompletionListener;

import java.io.IOException;

import site.kuzja.vkmusic.interfaces.OnPlayNextItem;
import site.kuzja.vkmusic.interfaces.OnUIUpdateListener;

public class MediaService implements OnPreparedListener,
        OnCompletionListener, OnAudioFocusChangeListener{
    //Слушатели
    private OnUIUpdateListener onUIUpdateListener;
    private OnPlayNextItem onPlayNextItem;

    private MediaPlayer mMediaPlayer;
    private MusicItem currentItem;
    private AudioManager mAudioManager;

    //private static final String LOG_TAG = "MediaService";

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
    public void release() {
        updateStatus(MusicItem.STATUS_STOPPED);
        mAudioManager.abandonAudioFocus(this);
        currentItem = null;
        mMediaPlayer.release();
    }

    public boolean setMusicItem(MusicItem item) {
        if (currentItem != null &&
                currentItem.getId() == item.getId()) {
            if (currentItem.getStatus() == MusicItem.STATUS_PLAYING) {
                mMediaPlayer.pause();
                updateStatus(MusicItem.STATUS_PAUSED);
            } else if (currentItem.getStatus() == MusicItem.STATUS_PAUSED) {
                mMediaPlayer.start();
                updateStatus(MusicItem.STATUS_PLAYING);
            }
        } else {
            updateStatus(MusicItem.STATUS_STOPPED);
            mMediaPlayer.reset();
            mAudioManager.abandonAudioFocus(this);
            int requestResult = mAudioManager.requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_FAILED)
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
        mAudioManager.abandonAudioFocus(this);
        if (onPlayNextItem != null)
            onPlayNextItem.playNextItem(currentItem);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
        updateStatus(MusicItem.STATUS_PLAYING);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                mMediaPlayer.pause();
                updateStatus(MusicItem.STATUS_PAUSED);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                mMediaPlayer.pause();
                updateStatus(MusicItem.STATUS_PAUSED);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mMediaPlayer.setVolume(0.5f, 0.5f);
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    updateStatus(MusicItem.STATUS_PLAYING);
                }
                mMediaPlayer.setVolume(1.0f, 1.0f);
                break;
        }
    }
}
