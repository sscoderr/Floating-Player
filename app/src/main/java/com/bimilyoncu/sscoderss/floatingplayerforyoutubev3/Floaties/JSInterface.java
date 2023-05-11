package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Floaties;

import androidx.core.app.NotificationManagerCompat;

import android.os.Build;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.SeekBar;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;

/**
 * Created by Sabahattin on 24.03.2017.
 */

public class JSInterface {
    private WebView wv;
    //private SeekBar sk;

    public JSInterface(WebView wv) {
        this.wv = wv;
        //this.sk = sk;
    }

    @JavascriptInterface
    public void onPlayerNext() {
        this.wv.post(new Runnable() {
            public void run() {
                MSettings.IsRetry = false;
                MSettings.IsonPlayerNext = true;
                MSettings.videoFinishStopVideoClicked = false;
                MSettings.LoadVideo();
            }
        });
    }

    @JavascriptInterface
    public void playPauseReturn(boolean isPlay) {

    }

    @JavascriptInterface
    public void onPlayerReady(int paramInt1, int paramInt2) {
    }

    @JavascriptInterface
    public void onPlayerStateChangedToPlaying(int currentTime, int duration, final int paramInt3, final int paramInt4) {
        //this.sk.setMax(duration);
    }
    @JavascriptInterface
    public boolean onPlaying(int currentTime, int duration) {
        //this.sk.setProgress(currentTime);
        return true;
    }

    @JavascriptInterface
    public void onmPlayPause(boolean isPlaying) {
        if (NotificationManagerCompat.from(MSettings.activeActivity).areNotificationsEnabled()) {
            Floaty.resetNotification(true);
            if (isPlaying) {
                Floaty.updateNotification(R.drawable.ic_pause_white);
                MSettings.activeActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MSettings.imagePlayPause.setImageResource(R.drawable.ic_pause_white);
                        MSettings.imagePlayPauseMainPlayer.setImageResource(R.drawable.ic_pause_white);
                    }
                });

            }
            else {
                Floaty.updateNotification(R.drawable.ic_play_arrow_white);
                MSettings.activeActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MSettings.imagePlayPause.setImageResource(R.drawable.ic_play_arrow_white);
                        MSettings.imagePlayPauseMainPlayer.setImageResource(R.drawable.ic_play_arrow_white);
                    }
                });
            }
        }
    }

    @JavascriptInterface
    public int getApiLevel() {
        return Build.VERSION.SDK_INT;
    }

}