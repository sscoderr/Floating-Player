package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.Notification;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;

/**
 * Created by monkeyCoder on 21.10.2017.
 */

public  class Notification {
    private Context context;

    private static NotificationManager notificationManager;
    private static RemoteViews remoteViews;
    private static int notification_id;
    private static NotificationCompat.Builder builder;

    public Notification(Context context, NotificationManager notificationManager, RemoteViews remoteViews, int notification_id, NotificationCompat.Builder builder) {
        this.context = context;
        this.notificationManager = notificationManager;
        this.remoteViews = remoteViews;
        this.notification_id = notification_id;
        this.builder = builder;

        this.remoteViews.setImageViewResource(R.id.image_notification_playpause, R.drawable.pause);
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }

    public static RemoteViews getRemoteViews() {
        return remoteViews;
    }

    public int getNotification_id() {
        return notification_id;
    }

    public NotificationCompat.Builder getBuilder() {
        return builder;
    }

    public static void PlayOrPauseImage(boolean isPlay) {
        if (remoteViews != null) {
            if (isPlay) {
                remoteViews.setImageViewResource(R.id.image_notification_playpause, R.drawable.pause);
            } else {
                remoteViews.setImageViewResource(R.id.image_notification_playpause, R.drawable.play);
            }

            if (MSettings.CheckService) {
                builder.setCustomContentView(remoteViews);
                notificationManager.notify(notification_id, builder.build());
            }
        } else {
            Log.e(".asd.", "null rem0teView -- PlayOrPauseImage");
        }
    }

    public static void VideoAndNextVideoTitle(String VideoName) {
        if (remoteViews != null) {
            remoteViews.setTextViewText(R.id.textview_notificatilon_music, VideoName);
        } else {
            Log.e(".asd.", "null rem0teView -- VideoAndNextVideoTitle");
        }
    }
}