package site.kuzja.vkmusic;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.util.Log;

import site.kuzja.vkmusic.Media.MediaService;
import site.kuzja.vkmusic.Media.MusicItem;


/**
 * Created by user on 12.03.17.
 */

public class MediaNotificationManager extends BroadcastReceiver {
    private static final String LOG_TAG = "NotificationManager";


    private static final int NOTIFICATION_ID = 111;
    private static final int REQUEST_CODE = 100;

    public static final String ACTION_PAUSE = "site.kuzja.VkMusic.pause";
    public static final String ACTION_PLAY = "site.kuzja.VkMusic.play";
    public static final String ACTION_PREV = "site.kuzja.VkMusic.prev";
    public static final String ACTION_NEXT = "site.kuzja.VkMusic.next";


    private MediaService mMediaService;
    private NotificationManager notificationManager;
    private Context context;
    boolean regster = false;

    private final PendingIntent mPauseIntent;
    private final PendingIntent mPlayIntent;
    private final PendingIntent mPreviousIntent;
    private final PendingIntent mNextIntent;

    public MediaNotificationManager(Context context, MediaService mMediaService,
                                    NotificationManager notificationManager) {
        this.mMediaService = mMediaService;
        this.notificationManager = notificationManager;
        this.context = context;

        mPauseIntent = PendingIntent.getBroadcast(context, REQUEST_CODE,
                new Intent(ACTION_PAUSE), PendingIntent.FLAG_CANCEL_CURRENT);
        mPlayIntent = PendingIntent.getBroadcast(context, REQUEST_CODE,
                new Intent(ACTION_PLAY), PendingIntent.FLAG_CANCEL_CURRENT);
        mPreviousIntent = PendingIntent.getBroadcast(context, REQUEST_CODE,
                new Intent(ACTION_PREV), PendingIntent.FLAG_CANCEL_CURRENT);
        mNextIntent = PendingIntent.getBroadcast(context, REQUEST_CODE,
                new Intent(ACTION_NEXT), PendingIntent.FLAG_CANCEL_CURRENT);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PREV);
        context.registerReceiver(this, filter);
        regster = true;

    }


    public void start() {// Строим уведомление
        stop();
        MusicItem item = mMediaService.getCurretItem();
        if (item == null)
            return;

        Notification notification = new Notification.Builder(context)
                .setContentTitle(item.getArtist())
                .setContentText(item.getTitle())
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .addAction(R.drawable.ic_skip_previous_white_48dp, "", mPreviousIntent)
                .addAction(
                        item.getStatus() == MusicItem.STATUS_PLAYING ?
                                R.drawable.ic_pause_white_48dp : R.drawable.ic_play_arrow_white_48dp,
                        "",
                        item.getStatus() == MusicItem.STATUS_PLAYING ? mPauseIntent : mPlayIntent )
                .addAction(R.drawable.ic_skip_next_white_48dp, "", mNextIntent)
                .build();
        if (item.getStatus() == MusicItem.STATUS_PLAYING)
            notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d(LOG_TAG, "Received intent with action " + action);
        switch (action) {
            case ACTION_PAUSE:
                mMediaService.pause(true);
                break;
            case ACTION_PLAY:
                mMediaService.play();
                break;
            case ACTION_NEXT:
                mMediaService.playNext();
                break;
            case ACTION_PREV:
                mMediaService.playPrevious();
                break;
            default:
                Log.w(LOG_TAG, "Неизвестный intent проигнорированн. Action = " + action);
        }
    }

    public void stop() {
        notificationManager.cancel(NOTIFICATION_ID);
        if (regster) {
            context.unregisterReceiver(this);
            regster = false;
        }
    }
}
