package com.bimilyoncu.sscoderss.floatingplayerforyoutube;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Locale;


/**
 * Created by Sabahattin on 18.03.2017.
 */
public class FloatingPlayer extends Service{
    private WindowManager wM;
    private String vId;

    private WebView mWebView;

    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        wM = (WindowManager) getSystemService(WINDOW_SERVICE);
        loadWebView();

        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(600, 400, WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        parameters.gravity = Gravity.CENTER | Gravity.CENTER;
        parameters.x = 0;
        parameters.y = 0;

        wM.addView(mWebView,parameters);
        mWebView.setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams updatedParameters = parameters;
            double x;
            double y;
            double pressedX;
            double pressedY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        x = updatedParameters.x;
                        y = updatedParameters.y;

                        pressedX = event.getRawX();
                        pressedY = event.getRawY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x + (event.getRawX() - pressedX));
                        updatedParameters.y = (int) (y + (event.getRawY() - pressedY));

                        wM.updateViewLayout(mWebView, updatedParameters);

                    default:
                        break;
                }

                return false;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        vId=String.valueOf(intent.getStringExtra("VIDEO_ID"));
        return super.onStartCommand(intent, flags, startId);
    }
    @SuppressLint({"SetJavaScriptEnabled"})
    private void loadWebView(){
        mWebView = new WebView(this);
        mWebView.setClickable(false);
        mWebView.setFocusable(false);
        WebSettings settings = mWebView.getSettings();
        if (Build.VERSION.SDK_INT >= 17)
            settings.setMediaPlaybackRequiresUserGesture(false);
        if (Build.VERSION.SDK_INT >= 19)
            mWebView.setWebContentsDebuggingEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);


        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://sabo.azurewebsites.net/index.html");
        //mWebView.loadUrl("https://www.amherst.edu/media/video_page/348705?full=1&autoplay=1");
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                mWebView.loadUrl(String.format(Locale.US,"javascript:loadVideoById(\"%s\");",new Object[]{vId}));
            }
        });

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setVerticalScrollBarEnabled(false);
        //mWebView.addJavascriptInterface(new JSInterface(),"WebPlayerInterface");
        mWebView.setHorizontalScrollBarEnabled(false);
    }
}
