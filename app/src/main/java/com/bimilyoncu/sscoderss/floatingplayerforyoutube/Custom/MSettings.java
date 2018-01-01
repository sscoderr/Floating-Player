package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.CustomAdapter;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Floaties.Floaty;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Page.Fragment.MyDateFragment;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item.VideoItem;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.common.io.BaseEncoding;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Sabahattin on 2.04.2017.
 */

public class MSettings {
    public static Boolean CheckService = false;
    public static String youtubeWatchURL = "https://www.youtube.com/watch?v=";
    public static View body;

    public static String countryCode;

    public static String currentVideoId;
    public static String nextVideoId;
    public static String currentVideoTitle;
    public static String nextVideoTitle;
    public static Floaty floaty;
    public static View head;
    public static WebView webView;
    public static ListView mListForFloat;
    public static Handler mHandler;
    public static Activity activeActivity;
    public static CustomAdapter mAdapter;
    public static List<VideoItem> similarVideosList;
    public static Integer playedVideoPos;
    public static Boolean isPlayedVideo = false;
    public static Integer CounterForSimilarVideos = 2;
    public static boolean checkRepeat = false;
    public static boolean checkSuffle = false;
    public static boolean videoFinishStopVideo = false;
    public static ArrayList<Integer> playedPoss = new ArrayList<>();
    private static int counterForSuffle = 0;
    public static String activeChannelId = "";
    public static String activePlaylistId = "";
    public static boolean isUserVideo = false;
    public static boolean isLaterRepeate = false;
    public static boolean isFirstOpenApp = false;
    public static RelativeLayout clickableRelative;
    public static final String URL = "http://fsbimilyoncu.gulernet.net/player.html";
    public static byte adsCounter = 0;
    public static InterstitialAd interstitial;
    public static Token token = new Token();

    public static void LoadVideo() {
        activeActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!videoFinishStopVideo) {
                        NetControl netControl = new NetControl(activeActivity);
                        if (netControl.isOnline()) {

                            if (floaty.notification != null) {
                                if (currentVideoTitle != null) {
                                    String Title = currentVideoTitle;
                                    if (Title.length() > 25) {
                                        Title = Title.substring(0, 25) + "...";
                                    }

                                    floaty.notification.getRemoteViews().setTextViewText(R.id.textview_notificatilon_music, Title);
                                } else {
                                    floaty.notification.getRemoteViews().setTextViewText(R.id.textview_notificatilon_music, "??");
                                }
                            }

                            nextVideoId = currentVideoId;
                            nextVideoTitle = currentVideoTitle;
                            getVideoTitle();
                            webView.onResume();
                            webView.resumeTimers();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (!Settings.canDrawOverlays(activeActivity)) {
                                    Toast.makeText(activeActivity, activeActivity.getString(R.string.permissionMessage), Toast.LENGTH_LONG).show();
                                    Intent i = activeActivity.getBaseContext().getPackageManager()
                                            .getLaunchIntentForPackage(activeActivity.getBaseContext().getPackageName());
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    activeActivity.startActivity(i);
                                } else
                                    floaty.startService();
                            } else
                                floaty.startService();
                            mHandler = new Handler();
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activeActivity);
                            if (preferences.getBoolean("isHighQuality", false)) {
                                webView.loadUrl(String.format("javascript:loadVideoById(\"%s\");", new Object[]{currentVideoId}));
                            } else
                                webView.loadUrl(String.format("javascript:loadVideoById(\"%s\",\"small\");", new Object[]{currentVideoId}));

                            DatabaseForPlaylists db = new DatabaseForPlaylists(activeActivity);
                            db.addVideoHistory(currentVideoId, activeActivity);
                            MyDateFragment.isHaveUpdate = true;

                            if (!checkRepeat && !checkSuffle) {
                                if (isLaterRepeate) {
                                    CounterForSimilarVideos += 1;
                                    isLaterRepeate = false;
                                }
                                currentVideoId = similarVideosList.get(CounterForSimilarVideos).getId();
                                setVideoTitle(similarVideosList.get(CounterForSimilarVideos).getTitle());
                                CounterForSimilarVideos += 1;
                                //Toast.makeText(activeActivity,"Girdi", Toast.LENGTH_LONG).show();
                            } else if (checkSuffle)
                                suffleVideo();
                            //webView.loadUrl("https://www.youtube.com/embed/" + currentVideoId + "?autoplay=1;rel=0&amp;showinfo=0&?Version=3&loop=1&playlist=" + currentVideoId);
                        } else {
                            Toast.makeText(activeActivity, activeActivity.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    //Toast.makeText(activeActivity, "hata:"+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void suffleVideo() {
        try {
            Random rand = new Random();
            int randValue = rand.nextInt(similarVideosList.size());
            while (playedPoss.indexOf(randValue) != -1 && counterForSuffle < (similarVideosList.size() * 3)) {
                randValue = rand.nextInt(similarVideosList.size());
                counterForSuffle += 1;
            }
            currentVideoId = similarVideosList.get(randValue).getId();
            setVideoTitle(similarVideosList.get(randValue).getTitle());
            playedPoss.add(randValue);
            counterForSuffle = 0;
        } catch (Exception e) {
        }
    }

    public static void setVideoTitle(String videoTitle) {
        currentVideoTitle = videoTitle;
    }

    public static void getVideoTitle() {
        TextView txtVideoTitle = (TextView) MSettings.body.findViewById(R.id.txtVideoTitle);
        try {
            if (currentVideoTitle.length() > 35) {
                txtVideoTitle.setText(currentVideoTitle.substring(0, 35) + "...");
            } else {
                txtVideoTitle.setText(currentVideoTitle);
            }
        } catch (Exception e) {
            txtVideoTitle.setText(currentVideoTitle);
        }
    }

   /* public static boolean isHaveNetworkAccesQuickly(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }*/

    public static String getSHA1(String packageName, Context ct) {
        try {
            Signature[] signatures = ct.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
            for (Signature signature : signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA-1");
                md.update(signature.toByteArray());
                return BaseEncoding.base16().encode(md.digest());
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void LoadFullScreenAds() {
        adsCounter = 0;
        MobileAds.initialize(activeActivity, "ca-app-pub-5808367634056272~8476127349");
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("6AFA29CB9314195950E590C9BEACC344").build();/*778ADE18482DD7E44193371217202427 Device Id*/
        interstitial = new InterstitialAd(activeActivity);
        interstitial.setAdUnitId(activeActivity.getString(R.string.admob_interstitial_six_tap));
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                if (interstitial.isLoaded())
                    interstitial.show();
            }

            public void onAdClosed() {
            }
        });
    }

    public static void LoadSixTapAds() {
        if (adsCounter >= 6)
            LoadFullScreenAds();
        else adsCounter += 1;
    }

    public static String getChannelImage(YouTube youtube, String KEY, String channel_id) throws IOException {
        String img_url = null;
        YouTube.Channels.List channel_list = youtube.channels().list("snippet,contentDetails,statistics");
        if (channel_list.size() > 0) {
            channel_list.setKey(KEY);
            channel_list.setId(channel_id);

            ChannelListResponse response = channel_list.execute();
            if (response.size() > 0) {
                List<Channel> results = response.getItems();
                if (response.size() > 0) {
                    for (Channel result : results) {
                        try {
                            img_url = result.getSnippet().getThumbnails().getMedium().getUrl();
                        } catch (Exception e) {
                            e.printStackTrace();
                            img_url = null;
                        }
                    }
                } else
                    img_url = null;
            } else
                img_url = null;
        } else
            img_url = null;

        return img_url;
    }
}