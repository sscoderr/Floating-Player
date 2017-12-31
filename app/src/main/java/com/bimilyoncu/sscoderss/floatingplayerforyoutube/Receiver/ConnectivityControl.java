package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Receiver;

import android.app.Application;

/**
 * Created by Sabahattin on 26.04.2017.
 */

public class ConnectivityControl extends Application {
    private static ConnectivityControl mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized ConnectivityControl getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
