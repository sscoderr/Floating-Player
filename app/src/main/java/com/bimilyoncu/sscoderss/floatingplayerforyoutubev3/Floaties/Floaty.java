package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Floaties;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.MainActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.BuildConfig;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.BroadcastPowerButton;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.DestroyOrShowBanner;


/**
 *
 * Created by ericbhatti on 11/24/15.
 *
 * @author Eric Bhatti
 * @since 24 November, 2015
 *
 */
public class Floaty {

    private final View head;
    private final View body;
    public final Context context;
    private final int notificationId;
    public static Floaty floaty;
    private final FloatyOrientationListener floatyOrientationListener;
    private float ratioY = 0;
    private float oldWidth = 0;
    private float oldX = 0;
    private Boolean confChange = false;

    public static WindowManager.LayoutParams params;
    public static int[] clickLocation = new int[2];
    public static LinearLayout mLinearLayout;
    public static WindowManager windowManager;

    private static final String LOG_TAG = "Floaty";


    public static  DisplayMetrics metrics;

    /**Notification**/
    public static NotificationManager notificationManager;
    public static NotificationCompat.Builder notBuilder;
    private static RemoteViews notRemoteView;
    public static int NOTIFICATION_ID=1579;
    final public static String ACTION_PLAY_PREVIOUS_FP="PREVIOUSVIDFP";
    final public static String ACTION_PLAY_NEXT_FP="NEXTVIDFP";
    final public static String ACTION_EXPAND_FP="EXPANDFP";
    final public static String ACTION_PLAY_PAUSE_FP="PlayPauseFP";
    final public static String ACTION_CLOSE_FP="CloseFP";

    protected BroadcastReceiver broadcastReceiver;
    protected IntentFilter intentFilter;
    private boolean justLeaveFromPrevious=false;
    private boolean justLeaveFromNext=false;
    public static Context serviceActivity;

    /**
     * @return The body of the floaty which is assigned through the {@link #createInstance} method.
     */
    public View getBody() {
        return floaty.body;
    }

    public View getHead() {
        return floaty.head;
    }
    /**
     * Creates a Singleton of the Floating Window
     *
     * @param context        The application context
     * @param body           The body View
     * @param notificationId The notificationId for your notification
     * @param notification   The notification which is displayed for the foreground player
     * @return A Floating Window
     */
    public static synchronized Floaty createInstance(Context context, View head, View body, int notificationId, Notification notification) {
        if (floaty == null) {
            floaty = new Floaty(context, head, body, notificationId, notification, new FloatyOrientationListener() {
                @Override
                public void beforeOrientationChange(Floaty floaty) {
                    Log.d(LOG_TAG, "beforeOrientationChange");
                }

                @Override
                public void afterOrientationChange(Floaty floaty) {
                    Log.d(LOG_TAG, "afterOrientationChange");
                }
            });
        }
        return floaty;
    }

    /**
     * @return The same instance of Floating Window, which has been created through {@link #createInstance}. Don't call this method before createInstance
     */
    public static synchronized Floaty getInstance() {
        if (floaty == null) {
            throw new NullPointerException("Floaty not initialized! First call createInstance method, then to access Floaty in any other class call getInstance()");
        }
        return floaty;
    }

    private Floaty(Context context, View head, View body, int notificationId, Notification notification, FloatyOrientationListener floatyOrientationListener) {
        this.head = head;
        this.body = body;
        this.context = context;
        this.notificationId = notificationId;
        this.floatyOrientationListener = floatyOrientationListener;
    }

    /**
     * Starts the player and adds it to the screen
     */
    public void startService() {
        Log.e(LOG_TAG, "startService");
        if(MainActivity.appVers != MainActivity.versionCode)
            MSettings.imageViewMinimizeFP.setVisibility(View.VISIBLE);
        MSettings.CheckService = true;
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    onBroadcastReceived(intent);
                }
                catch (Exception e){
                }
            }};
        this.intentFilter = new IntentFilter();
        Intent intent = new Intent(context, FloatHeadService.class);
        context.startService(intent);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (serviceActivity==null);
                    setupBroadcastReceiver(intentFilter);
                    registerBroadcastReceiver();
                }
                catch (Exception e){
                }
            }
        }).start();
    }

    /**
     * Stops the player and removes it from the screen
     */
    public void stopService() {
        try{
            DestroyOrShowBanner(MSettings.adViewTrendMusic,false);
            DestroyOrShowBanner(MSettings.adViewTrendVideo,false);
            DestroyOrShowBanner(MSettings.adViewMyData,false);
            DestroyOrShowBanner(MSettings.adViewPlaylist,false);
            MSettings.webView.loadUrl("javascript:stopVideo();");
            unregisterBroadcastReceiver();
            Log.e(LOG_TAG, "stopService");
            MSettings.CheckService = false;
            Intent intent = new Intent(context, FloatHeadService.class);
            context.stopService(intent);
            notificationManager.cancel(NOTIFICATION_ID);
            notificationManager.cancelAll();
            notificationManager=null;
            notBuilder=null;
        }catch (Exception e){

        }
    }


    protected void setupBroadcastReceiver(IntentFilter intentFilter) {
        intentFilter.addAction(ACTION_CLOSE_FP);
        intentFilter.addAction(ACTION_EXPAND_FP);
        intentFilter.addAction(ACTION_PLAY_PAUSE_FP);
        intentFilter.addAction(ACTION_PLAY_NEXT_FP);
        intentFilter.addAction(ACTION_PLAY_PREVIOUS_FP);
    }

    public void onBroadcastReceived(Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        switch (intent.getAction()) {
            case ACTION_EXPAND_FP:
                expandBody();
                break;
            case ACTION_PLAY_PAUSE_FP:
                KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                boolean isPhoneLocked = myKM.inKeyguardRestrictedInputMode();
                if (!isPhoneLocked)
                    MSettings.webView.loadUrl("javascript:playPause();");
                else {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                        MSettings.webView.loadUrl("javascript:playPause();");
                }
                break;
            case ACTION_CLOSE_FP:
                stopService();
                break;
            case ACTION_PLAY_NEXT_FP:
                if (MSettings.similarVideosList.size()>=MSettings.CounterForSimilarVideos) {
                    if (justLeaveFromPrevious)
                        MSettings.LoadVideo();
                    MSettings.LoadVideo();
                    justLeaveFromPrevious = false;
                    justLeaveFromNext = true;
                }
                break;
            case ACTION_PLAY_PREVIOUS_FP:
                if (MSettings.CounterForSimilarVideos>=2) {
                    if (justLeaveFromNext)
                        MSettings.currentVItem = MSettings.similarVideosList.get(MSettings.CounterForSimilarVideos -= 3);
                    else MSettings.currentVItem = MSettings.similarVideosList.get(MSettings.CounterForSimilarVideos -= 2);
                    justLeaveFromNext=false;
                    justLeaveFromPrevious=true;
                    MSettings.LoadVideo();
                }
                break;
        }
    }

    protected void registerBroadcastReceiver() {
        // Try to unregister current first
        unregisterBroadcastReceiver();
        if (serviceActivity!=null)
            serviceActivity.registerReceiver(broadcastReceiver, intentFilter);
    }

    protected void unregisterBroadcastReceiver() {
        try {
            if (serviceActivity!=null)
                serviceActivity.unregisterReceiver(broadcastReceiver);
        } catch (final IllegalArgumentException unregisteredException) {
        }
    }
    static String vTitle="";
    static String vChannelTitle="";
    static Bitmap vImage;
    public static NotificationCompat.Builder createNotification(boolean isForChangePlayPause) {
        notRemoteView = new RemoteViews(BuildConfig.APPLICATION_ID, R.layout.player_notification);
        try {
            if (isForChangePlayPause) {
                notRemoteView.setTextViewText(R.id.notificationSongName, vTitle);
                notRemoteView.setTextViewText(R.id.notificationArtist, vChannelTitle);
                if (vImage != null)
                    notRemoteView.setImageViewBitmap(R.id.notificationCover, vImage);
            } else {
                if (MSettings.currentVItem.getTitle()!=null) {
                    if (MSettings.comeFromFirst) {
                        MSettings.comeFromFirst = false;
                        notRemoteView.setTextViewText(R.id.notificationArtist, vChannelTitle = MSettings.channelTitleForFirst);
                        if (MSettings.image != null)
                            notRemoteView.setImageViewBitmap(R.id.notificationCover, vImage = MSettings.image);
                    } else {
                        notRemoteView.setTextViewText(R.id.notificationSongName, vTitle = MSettings.currentVItem.getTitle());
                        notRemoteView.setTextViewText(R.id.notificationArtist, vChannelTitle = MSettings.currentVItem.getChannelTitle());
                        if (MSettings.image != null)
                            notRemoteView.setImageViewBitmap(R.id.notificationCover, vImage = MSettings.image);
                    }
                }
            }
        }
        catch (Exception e){

        }

        notRemoteView.setOnClickPendingIntent(R.id.notificationPlayPause,
                PendingIntent.getBroadcast(MSettings.activeActivity, NOTIFICATION_ID, new Intent(ACTION_PLAY_PAUSE_FP), Build.VERSION.SDK_INT < 31 ? PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_IMMUTABLE));
        notRemoteView.setOnClickPendingIntent(R.id.notificationStop,
                PendingIntent.getBroadcast(MSettings.activeActivity, NOTIFICATION_ID, new Intent(ACTION_CLOSE_FP), Build.VERSION.SDK_INT < 31 ? PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_IMMUTABLE));
        notRemoteView.setOnClickPendingIntent(R.id.notificationFRewind,
                PendingIntent.getBroadcast(MSettings.activeActivity, NOTIFICATION_ID, new Intent(ACTION_PLAY_PREVIOUS_FP), Build.VERSION.SDK_INT < 31 ? PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_IMMUTABLE));
        notRemoteView.setOnClickPendingIntent(R.id.notificationFForward,
                PendingIntent.getBroadcast(MSettings.activeActivity, NOTIFICATION_ID, new Intent(ACTION_PLAY_NEXT_FP), Build.VERSION.SDK_INT < 31 ? PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_IMMUTABLE));
        notRemoteView.setOnClickPendingIntent(R.id.notificationContent,
                PendingIntent.getBroadcast(MSettings.activeActivity, NOTIFICATION_ID, new Intent(ACTION_EXPAND_FP), Build.VERSION.SDK_INT < 31 ? PendingIntent.FLAG_UPDATE_CURRENT : PendingIntent.FLAG_IMMUTABLE));
        notRemoteView.setImageViewResource(R.id.notificationFRewind, R.drawable.exo_icon_previous);
        notRemoteView.setImageViewResource(R.id.notificationFForward, R.drawable.exo_icon_next);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MSettings.activeActivity, MSettings.activeActivity.getResources().getString(R.string.notification_channel_id))
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_newpipe_triangle_white)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContent(notRemoteView);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(NotificationCompat.PRIORITY_MAX);
        }
        return builder;
    }
    public static void updateNotification(int drawableId) {
        try {
            if (notificationManager != null) {
                if (notBuilder == null || notRemoteView == null) return;

                if (drawableId != -1)
                    notRemoteView.setImageViewResource(R.id.notificationPlayPause, drawableId);
                notificationManager.notify(NOTIFICATION_ID, notBuilder.build());
            }
        }
        catch (Exception e)
        {}
    }
    public static void resetNotification(boolean isForChangePlayPause) {
        notBuilder = createNotification(isForChangePlayPause);
    }

    public static void expandBody(){
        MSettings.FullPlayer();
        params.x = metrics.widthPixels;
        params.y = 0;
        params.flags = params.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        floaty.body.setVisibility(View.VISIBLE);
        mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
        windowManager.updateViewLayout(mLinearLayout, params);
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        MSettings.activeActivity.sendBroadcast(it);
    }

    public static class FloatHeadService extends Service {
        GestureDetectorCompat gestureDetectorCompat;
        private boolean didFling;

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            /*if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                int[] location = new int[2];
                mLinearLayout.getLocationOnScreen(location);
                floaty.oldWidth = metrics.widthPixels;
                floaty.confChange = true;
                if (floaty.getBody().getVisibility() == View.VISIBLE) {
                    floaty.oldX = clickLocation[0];
                    floaty.ratioY = (float) (clickLocation[1]) / (float) metrics.heightPixels;
                } else {
                    floaty.oldX = location[0];
                    floaty.ratioY = (float) (location[1]) / (float) metrics.heightPixels;
                }
                floaty.floatyOrientationListener.beforeOrientationChange(floaty);
                floaty.stopService();
                floaty.startService();
                floaty.floatyOrientationListener.afterOrientationChange(floaty);
            }*/
        }

        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.e(LOG_TAG, "onStartCommand");
            metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            try {
                if (floaty != null) {
                    if (notBuilder != null) {
                        startForeground(NOTIFICATION_ID, notBuilder.build());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return START_STICKY;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.e(LOG_TAG, "onCreate");
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            BroadcastReceiver mReceiver = new BroadcastPowerButton();
            try {
                unregisterReceiver(mReceiver);
            }catch (Exception e){

            }
            try{
                registerReceiver(mReceiver, filter);
                serviceActivity=this;
            }catch (Exception e){}

            if (notificationManager==null)
                notificationManager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
            mLinearLayout = new LinearLayout(getApplicationContext()) {
                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    if ((event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_HOME)&&!MSettings.isPowerSaverMode) {
                        Log.e(LOG_TAG, "dispatchKeyEvent");

                        if (MainActivity.appVers != MainActivity.versionCode) {
                            floaty.getHead().setVisibility(View.VISIBLE);
                            floaty.body.setVisibility(View.GONE);
                        }
                        MSettings.LoadSixTapAds();
                        MSettings.SmallPlayer();
                        params.x = clickLocation[0];
                        params.y = clickLocation[1] - 36;
                        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
                        windowManager.updateViewLayout(mLinearLayout, params);

                        return true;
                    }

                    return super.dispatchKeyEvent(event);
                }
            };
            gestureDetectorCompat = new GestureDetectorCompat(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onDown(MotionEvent event) {
                    try {
                        Log.d(LOG_TAG, "onDown");

                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        didFling = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {
                    floaty.head.setAlpha(0.8f);
                    Log.d(LOG_TAG, "onShowPress");
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    try {
                        params.x = (initialX + (int) ((e2.getRawX() - initialTouchX)));
                        params.y = (initialY + (int) ((e2.getRawY() - initialTouchY)));
                        windowManager.updateViewLayout(mLinearLayout, params);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return false;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    try {
                        Log.d(LOG_TAG, "onSingleTapConfirmed");
                        if(MSettings.isSmallPlayerActive&&!MSettings.isPowerSaverMode) {
                            MSettings.FullPlayer();
                            params.x = metrics.widthPixels;
                            params.y = 0;
                            params.flags = params.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                            params.width = WindowManager.LayoutParams.MATCH_PARENT;
                            params.height = WindowManager.LayoutParams.MATCH_PARENT;
                            floaty.body.setVisibility(View.VISIBLE);
                            mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
                            MSettings.isSmallPlayerActive=false;
                            windowManager.updateViewLayout(mLinearLayout, params);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    return false;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    try {
                        Log.d(LOG_TAG, "onFling");

                        didFling = true;
                        int newX = params.x;

                        if (newX > (metrics.widthPixels / 2)) {
                            params.x = metrics.widthPixels;
                        } else {
                            params.x = 0;
                        }

                        windowManager.updateViewLayout(mLinearLayout, params);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    return false;
                }
            });

            mLinearLayout.setOrientation(LinearLayout.VERTICAL);
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);

            int LAYOUT_FLAG;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                //LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;//BEN DEGISTIRDIM LOCK SCREEN PLAYERIN GORUNMESI ICIN
            }

            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            /*new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            |WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                            | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON ,
                    PixelFormat.RGBA_8888);*/

            params.gravity = Gravity.TOP | Gravity.LEFT;

            if (floaty != null) {
                if (floaty.confChange != null) {
                    if (floaty.confChange) {
                        floaty.confChange = false;
                        if (floaty.oldX < (floaty.oldWidth / 2)) {
                            params.x = 0;
                        } else {
                            params.x = metrics.widthPixels;
                        }
                        params.y = (int) (metrics.heightPixels * floaty.ratioY);
                    } else {
                        params.x = metrics.widthPixels;
                        params.y = 0;
                    }
                }


                if (floaty.body != null) {
                    floaty.body.setVisibility(View.GONE);
                }

                if (floaty.head != null) {
                    floaty.head.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            gestureDetectorCompat.onTouchEvent(event);
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                floaty.head.setAlpha(1.0f);

                                if (!didFling) {
                                    Log.e(LOG_TAG, "ACTION_UP");

                                    int newX = params.x;
                                    if (newX > (metrics.widthPixels / 2)) {
                                        params.x = metrics.widthPixels;
                                    } else {
                                        params.x = 0;
                                    }

                                    windowManager.updateViewLayout(mLinearLayout, params);
                                }
                            }
                            return true;
                        }
                    });
                }

                if (MSettings.webView != null) {
                    MSettings.webView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (MSettings.isSmallPlayerActive) {
                                gestureDetectorCompat.onTouchEvent(event);

                                if (event.getAction() == MotionEvent.ACTION_UP) {

                                    if (!didFling) {

                                        if (MSettings.isPowerSaverMode) {
                                            MSettings.floaty.params.screenBrightness = 10;
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        Thread.sleep(3100);
                                                        if (MSettings.activeActivity!=null)
                                                            MSettings.activeActivity.runOnUiThread(new Runnable() {
                                                                public void run() {
                                                                    if (MSettings.isPowerSaverMode) {
                                                                        MSettings.floaty.params.screenBrightness = 0;
                                                                        windowManager.updateViewLayout(mLinearLayout, params);
                                                                    }
                                                                }
                                                            });
                                                    } catch (Exception e) {
                                                    }
                                                }
                                            }).start();
                                        }
                                        Log.e(LOG_TAG, "ACTION_UP");

                                        int newX = params.x;
                                        if (newX > (metrics.widthPixels / 2)) {
                                            params.x = metrics.widthPixels;
                                        } else {
                                            params.x = 0;
                                        }

                                        windowManager.updateViewLayout(mLinearLayout, params);
                                    }
                                }
                                return true;
                            }
                            else
                                return false;
                        }
                    });
                }

                if (windowManager != null) {
                    windowManager.addView(mLinearLayout, params);
                }

                if (floaty.body != null) {
                    if (floaty.body.getParent() != null) {
                        ((ViewGroup) floaty.body.getParent()).removeView(floaty.body);
                    }
                }

                if (mLinearLayout != null) {
                    mLinearLayout.setFocusable(true);
                }

                LinearLayout.LayoutParams headParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                headParams.gravity = Gravity.TOP | Gravity.RIGHT;
                bodyParams.gravity = Gravity.TOP;

                if (mLinearLayout != null) {
                    mLinearLayout.addView(floaty.head, headParams);
                    mLinearLayout.addView(floaty.body, bodyParams);
                }

                if (floaty.body != null) {
                    floaty.body.setVisibility(View.VISIBLE);
                }
                if (floaty != null) {
                    floaty.head.setVisibility(View.GONE);
                }
                showBody();
            }
        }

        public void onDestroy() {
            super.onDestroy();
            Log.e(LOG_TAG, "onDestroy");

            if (mLinearLayout != null) {
                mLinearLayout.removeAllViews();
                if (windowManager != null) {
                    windowManager.removeView(mLinearLayout);
                }
            }

            stopForeground(false);
        }

        public void showBody() {
            params.x = metrics.widthPixels;
            params.y = 0;

            params.flags = params.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;

            floaty.body.setVisibility(View.VISIBLE);

            mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));

            windowManager.updateViewLayout(mLinearLayout, params);
        }
    }
}

