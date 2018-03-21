package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by Furkann on 27.11.2016.
 */
public class NetControl {
    static Activity activity;

    public NetControl(Activity activity) {
        this.activity = activity;
    }

    public boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();

            if (netInfo != null && netInfo.isConnectedOrConnecting())
                return true;

            Log.e("NetControl!", "27.. isOnline return false");
            return false;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
}