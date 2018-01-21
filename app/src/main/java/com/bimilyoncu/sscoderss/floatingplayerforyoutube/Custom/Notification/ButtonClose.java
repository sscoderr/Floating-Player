package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

/**
 * Created by Furkan on 21.10.2017.
 */

public class ButtonClose extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            MobileAds.initialize(MSettings.activeActivity, "ca-app-pub-5808367634056272~8476127349");
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("6AFA29CB9314195950E590C9BEACC344")
                    .addTestDevice("0CEA9CA5F2DAED70F0678D8F2D8669A3").build();/*778ADE18482DD7E44193371217202427 Device Id*/
            final InterstitialAd interstitial = new InterstitialAd(MSettings.activeActivity);
            interstitial.setAdUnitId(MSettings.activeActivity.getString(R.string.admob_interstitial_id_close_service));
            interstitial.loadAd(adRequest);
            interstitial.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    if (interstitial.isLoaded())
                        interstitial.show();
                }

                public void onAdClosed() {

                }
            });

            MSettings.floaty.stopService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}