package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;

/**
 * Created by Furkan on 21.10.2017.
 */

public class ButtonPlayPause extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent ıntent) {
        if (MSettings.webView != null) {
//                Notification.PlayOrPauseImage(false);
//                Notification.PlayOrPauseImage(true);
            MSettings.webView.loadUrl("javascript:playPauseReturn()");
        }
    }
}
