package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BroadcastPowerButton extends BroadcastReceiver {
    private static String LOG_TAG = "BroadcastPowerButton";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.e(LOG_TAG, "Screen OFF");

                if (MSettings.webView != null) {
                    MSettings.webView.loadUrl("javascript:pauseVideo();");
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.e(LOG_TAG, "Screen ON");

                if (MSettings.webView != null) {
                    MSettings.webView.loadUrl("javascript:playVideo();");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
