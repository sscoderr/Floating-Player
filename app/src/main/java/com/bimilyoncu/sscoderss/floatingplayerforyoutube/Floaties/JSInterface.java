package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Floaties;

import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.BroadcastPowerButton;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;

/**
 * Created by Sabahattin on 24.03.2017.
 */

public class JSInterface {
    private WebView wv;

    public JSInterface(WebView wv) {
        this.wv = wv;
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
    public void onPlayerStateChangedToPlaying(int paramInt1, int paramInt2, final int paramInt3, final int paramInt4) {

    }
}