package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.MainActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;

import java.util.ArrayList;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings.playedPoss;

/**
 * Created by Furkan on 21.10.2017.
 */

public class ButtonNextMusic extends BroadcastReceiver {
    private static String LOG_TAG = ".ButtonNextMusic.";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (MSettings.similarVideosList != null) {
            if (MSettings.similarVideosList.size() > 0) {
                MSettings.CounterForSimilarVideos=2;
                playedPoss=new ArrayList<>();
                MSettings.currentVideoId = MSettings.similarVideosList.get(1).getId();
                MSettings.playedVideoPos=1;
                MSettings.setVideoTitle(MSettings.similarVideosList.get(1).getTitle());
                MainActivity mainActivity = new MainActivity();
                mainActivity.getSimilarVideos(String.valueOf(MSettings.currentVideoId),false,false,false,new String[]{});
                MSettings.LoadVideo();
                MSettings.LoadSixTapAds();
            }
        }
    }
}
