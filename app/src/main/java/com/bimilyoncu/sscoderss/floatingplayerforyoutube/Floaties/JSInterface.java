package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Floaties;

import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.Notification.Notification;

/**
 * Created by Sabahattin on 24.03.2017.
 */

public class JSInterface {
    private WebView wv;

    public JSInterface(WebView wv) {
        this.wv = wv;
    }

    /*@JavascriptInterface
    public int getApiLevel() {
        return Build.VERSION.SDK_INT;
    }*/

    /*@JavascriptInterface
    public void onNoPlaying()
    {
        this.a.setPlaying(false);
    }*/

    @JavascriptInterface
    public void onPlayerNext() {
        this.wv.post(new Runnable() {
            public void run() {
                MSettings.LoadVideo();
            }
        });
    }

    @JavascriptInterface
    public void playPauseReturn(boolean isPlay) {
        if (MSettings.webView != null) {
            Notification.PlayOrPauseImage(isPlay);
        }
    }

    @JavascriptInterface
    public void onPlayerReady(int paramInt1, int paramInt2) {
        this.wv.post(new Runnable() {
            public void run() {
                MSettings.currentVideoId = MSettings.nextVideoId;
                if (MSettings.nextVideoTitle != null)
                    MSettings.currentVideoTitle = MSettings.nextVideoTitle;
                MSettings.LoadVideo();
            }
        });
    }

    @JavascriptInterface
    public void onPlayerStateChangedToPlaying(int paramInt1, int paramInt2, final int paramInt3, final int paramInt4) {
        //Toast.makeText(MSettings.activeActivity,"sdfsdf",Toast.LENGTH_SHORT);
        this.wv.post(new Runnable() {
            public void run() {
                if (Integer.valueOf(android.os.Build.VERSION.SDK) < 21 && !MSettings.isFirstOpenApp) {
                    MSettings.isFirstOpenApp = true;
//                    MSettings.clickableRelative.setVisibility(View.VISIBLE);
                }
            }
        });

        /*if (Build.VERSION.SDK_INT <= 20)
            wv.postDelayed(new Runnable() {
                               public void run() {
                                   Object[] arrayOfObject = new Object[2];
                                   arrayOfObject[0] = Integer.valueOf(paramInt3)-1;
                                   arrayOfObject[1] = Integer.valueOf(paramInt4)-1;
                                   MSettings.webView.loadUrl(String.format(Locale.US, "javascript:player.setSize(%d, %d);", arrayOfObject));
                                   wv.postDelayed(new Runnable() {
                                                      public void run() {
                                                          Object[] arrayOfObject = new Object[2];
                                                          arrayOfObject[0] = Integer.valueOf(paramInt3);
                                                          arrayOfObject[1] = Integer.valueOf(paramInt4);
                                                          MSettings.webView.loadUrl(String.format(Locale.US, "javascript:player.setSize(%d, %d);", arrayOfObject));
                                                      }
                                                  }
                                           , 500L);
                               }
                           }
                    , 2000L);*/
    }
     /*
    @JavascriptInterface
    public boolean onPlaying(int paramInt1, int paramInt2)
    {
        this.a.setPlaying(true);
        this.a.getSeekBar().setProgress(paramInt1);
        if ((this.a.getTextViewDuration() != null) && (this.a.getTextViewPosition() != null))
            this.a.post(new Runnable(paramInt1, paramInt2)
            {
                public void run()
                {
                    d.this.a.getTextViewPosition().setText(u.a(this.a));
                    d.this.a.getTextViewDuration().setText(u.a(this.b));
                }
            });
        if (((KeyguardManager)this.b.getSystemService("keyguard")).inKeyguardRestrictedInputMode())
        {
            this.a.setPausedByLockScreen(true);
            this.b.startService(new Intent(this.b, WebPlayerService.class).setAction("ACTION_SCHEDULE_PLAY_AFTER_UNLOCK"));
            return false;
        }
        return true;
    }
    }*/
}