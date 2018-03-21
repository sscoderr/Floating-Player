package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Floaties;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.app.NotificationCompat;
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

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import static android.content.Context.NOTIFICATION_SERVICE;

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
    private final Context context;
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
     * @param head           The head View, upon clicking it the body is to be opened
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
        this.notification = notification;
        this.notificationId = notificationId;
        this.floatyOrientationListener = floatyOrientationListener;
    }

    /**
     * Starts the player and adds it to the screen
     */
    public void startService() {
        Log.e(LOG_TAG, "startService");
        MSettings.CheckService = true;

        Intent intent = new Intent(context, FloatHeadService.class);
        context.startService(intent);
    }

    /**
     * Stops the player and removes it from the screen
     */
    public void stopService() {
        Log.e(LOG_TAG, "stopService");
        MSettings.CheckService = false;

        Intent intent = new Intent(context, FloatHeadService.class);
        context.stopService(intent);
    }

    public static Notification notification;

    /**
     * Helper method for notification creation.
     *
     * @param context //     * @param contentTitle
     *                //     * @param contentText
     *                //     * @param notificationIcon
     *                //     * @param contentIntent
     * @return Notification for the Service
     */
    public static Notification createNotification(Context context, String contentTitle, String contentText, int notificationIcon, PendingIntent contentIntent) {
           return new NotificationCompat.Builder(context)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(notificationIcon)
                .setContentIntent(contentIntent).build();
    }

    public static class FloatHeadService extends Service {
        GestureDetectorCompat gestureDetectorCompat;
        DisplayMetrics metrics;
        private boolean didFling;

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

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
            }
        }

        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.e(LOG_TAG, "onStartCommand");

            metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            startForeground(floaty.notificationId, notification);

            return START_STICKY;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.e(LOG_TAG, "onCreate");

            mLinearLayout = new LinearLayout(getApplicationContext()) {
                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
                        Log.e(LOG_TAG, "dispatchKeyEvent");

                        floaty.body.setVisibility(View.GONE);
                        params.x = clickLocation[0];
                        params.y = clickLocation[1] - 36;
                        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
                        windowManager.updateViewLayout(mLinearLayout, params);

                        floaty.getHead().setVisibility(View.VISIBLE);
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
                    try {
                        Log.d(LOG_TAG, "onShowPress");

                        floaty.head.setAlpha(0.8f);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    try {
                        if (floaty.body.getVisibility() == View.VISIBLE) {
                            floaty.body.setVisibility(View.GONE);
                            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
                        }

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
                        if (floaty.body.getVisibility() == View.GONE) {
                            params.x = metrics.widthPixels;
                            params.y = 0;
                            params.flags = params.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                            params.width = WindowManager.LayoutParams.MATCH_PARENT;
                            params.height = WindowManager.LayoutParams.MATCH_PARENT;
                            floaty.head.getLocationOnScreen(clickLocation);
                            floaty.body.setVisibility(View.VISIBLE);
                            mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));

                            floaty.head.setVisibility(View.GONE);
                        } else {
                            floaty.body.setVisibility(View.GONE);
                            params.x = clickLocation[0];
                            params.y = clickLocation[1] - 36;
                            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                            mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));

                            floaty.head.setVisibility(View.VISIBLE);
                        }

                        windowManager.updateViewLayout(mLinearLayout, params);
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

            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
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
                LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

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
                windowManager.removeView(mLinearLayout);
            }

            stopForeground(false);
        }

        private void showBody() {
            params.x = metrics.widthPixels;
            params.y = 0;

            params.flags = params.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;

            floaty.head.getLocationOnScreen(clickLocation);
            floaty.body.setVisibility(View.VISIBLE);

            mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));

            windowManager.updateViewLayout(mLinearLayout, params);
        }
    }
}
