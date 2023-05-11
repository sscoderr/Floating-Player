package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.MainActivity;

import static android.content.Context.POWER_SERVICE;

public class BroadcastPowerButton extends BroadcastReceiver {
    private static String LOG_TAG = "BroadcastPowerButton";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            /*if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.e(LOG_TAG, "Screen OFF");
                if (MSettings.webView != null) {
                    MSettings.webView.loadUrl("javascript:playPause();");
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.e(LOG_TAG, "Screen ON");
                if (MSettings.webView != null) {
                    MSettings.webView.loadUrl("javascript:playPause();");
                }
            }*/

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.e(LOG_TAG, "Screen OFF");
                if (MSettings.webView != null) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O && MainActivity.appVers == MainActivity.versionCode) {
                        PowerManager powerManager = (PowerManager) MSettings.activeActivity.getSystemService(POWER_SERVICE);
                        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                                "MyApp::MyWakelockTag");
                        wakeLock.acquire();
                        wakeLock.release();
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && MainActivity.appVers == MainActivity.versionCode)
                        MSettings.webView.loadUrl("javascript:pauseMVideo();");

                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
