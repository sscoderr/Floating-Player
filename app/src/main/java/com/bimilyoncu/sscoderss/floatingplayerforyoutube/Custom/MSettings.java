package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.MainActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.SearchActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.AdapterServiceSearchKey;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.AdapterServiceSearchVideo;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.CustomAdapter;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.CustomAdapterAutoComplate;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector.ServiceSearchConnector;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector.YoutubeConnector;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Database.DatabaseForSearchHistory;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Floaties.Floaty;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.Fragment.MyDateFragment;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Sabahattin on 2.04.2017.
 */

public class MSettings {
    public static Boolean CheckService = false;
    public static String youtubeWatchURL = "https://www.youtube.com/watch?v=";
    public static View body;

    public static String countryCode;


    public static Floaty floaty;
    public static View head;
    public static WebView webView;
    public static ListView mListForFloat;
    public static Handler mHandler;
    public static Activity activeActivity;
    public static CustomAdapter mAdapter;
    public static List<VideoItem> similarVideosList;
    public static Boolean isPlayedVideo = false;
    public static Integer CounterForSimilarVideos = 1;
    public static boolean checkRepeat = false;
    public static boolean checkSuffle = false;
    public static boolean videoFinishStopVideo = false;
    public static ArrayList<Integer> playedPoss = new ArrayList<>();
    private static int counterForSuffle = 0;
    public static String activeChannelId = "";
    public static String activePlaylistId = "";
    public static boolean isUserVideo = false;
    public static boolean isLaterRepeate = false;
    public static final String URL = "http://bimilyoncu.us/player.html";
    public static byte adsCounter = 0;
    public static InterstitialAd interstitial;
    public static Token token = new Token();
    public static VideoItem currentVItem;
    public static boolean similarVideosIsLoaded=false;
    public static boolean playerReady=false;
    public static boolean IsRetry = false;
    public static boolean IsonPlayerNext = false;
    public static boolean videoFinishStopVideoClicked = false;

    public static void LoadVideo() {
        activeActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!videoFinishStopVideo || videoFinishStopVideoClicked) {
                        NetControl netControl = new NetControl(activeActivity);
                        if (netControl.isOnline()) {

                            MSettings.videoFinishStopVideoClicked = false;
                            if ((IsonPlayerNext && checkRepeat) || (checkRepeat && IsRetry)) {
                                IsonPlayerNext = false;
                                webView.loadUrl(String.format("javascript:seekTo(\"%s\");", new Object[]{0}));
                            } else {
                                if (floaty.notification != null) {
                                    if (currentVItem.getTitle() != null) {
                                        String Title = currentVItem.getTitle();
                                        if (Title.length() > 25) {
                                            Title = Title.substring(0, 25) + "...";
                                        }
                                    }
                                }
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
                                    } else {
                                        floaty.startService();
                                    }
                                } else {
                                    floaty.startService();
                                }

                                mHandler = new Handler();
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activeActivity);
                                while (!playerReady);
                                if (preferences.getBoolean("isHighQuality", false)) {
                                    webView.loadUrl(String.format("javascript:loadVideoById(\"%s\",\"highres\");", new Object[]{currentVItem.getId()}));
                                } else {
                                    webView.loadUrl(String.format("javascript:loadVideoById(\"%s\",\"small\");", new Object[]{currentVItem.getId()}));
                                }

                                DatabaseForPlaylists db = new DatabaseForPlaylists(activeActivity);
                                db.addVideoHistory(currentVItem.getId(), activeActivity);
                                MyDateFragment.isHaveUpdate = true;

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            while (!similarVideosIsLoaded);
                                            if (!checkRepeat && !checkSuffle) {
                                                currentVItem=similarVideosList.get(CounterForSimilarVideos);
                                                CounterForSimilarVideos += 1;
                                            } else if (checkSuffle)
                                                currentVItem=similarVideosList.get(suffleVideo());
                                        }
                                        catch (Exception e){
                                            e.printStackTrace();

                                            if (CounterForSimilarVideos>=similarVideosList.size())
                                                CounterForSimilarVideos=0;
                                        }
                                    }
                                }).start();
                                //webView.loadUrl("https://www.youtube.com/embed/" + currentVideoId + "?autoplay=1;rel=0&amp;showinfo=0&?Version=3&loop=1&playlist=" + currentVideoId);

                                MSettings.IsRetry = true;
                            }
                        } else {
                            Toast.makeText(activeActivity, activeActivity.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static int suffleVideo() {
        try {
            Random rand = new Random();
            int randValue = rand.nextInt(similarVideosList.size());
            while (playedPoss.indexOf(randValue) != -1 && counterForSuffle < (similarVideosList.size() * 3)) {
                randValue = rand.nextInt(similarVideosList.size());
                counterForSuffle += 1;
            }
            playedPoss.add(randValue);
            counterForSuffle = 0;
            return  randValue;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static void getVideoTitle() {
        me.grantland.widget.AutofitTextView txtVideoTitle = (me.grantland.widget.AutofitTextView) MSettings.body.findViewById(R.id.txtVideoTitle);
        txtVideoTitle.setTypeface(Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "VarelaRound-Regular.ttf"));
        try {
            if (currentVItem.getTitle().length() > 45) {
                txtVideoTitle.setText(currentVItem.getTitle().substring(0, 45) + " ...");
            } else {
                txtVideoTitle.setText(currentVItem.getTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
            txtVideoTitle.setText(currentVItem.getTitle());
        }
    }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void LoadFullScreenAds() {
        adsCounter = 0;
        MobileAds.initialize(activeActivity, "ca-app-pub-5808367634056272~8476127349");
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("6EE0EC7A08848B41A3A8B3C52624F39A")
                .addTestDevice("D840C07DDBAA5E0897B010411FABE6AC")
                .addTestDevice("778ADE18482DD7E44193371217202427")
                .addTestDevice("6AFA29CB9314195950E590C9BEACC344")
                .addTestDevice("0CEA9CA5F2DAED70F0678D8F2D8669A3").build();
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

    public static String serviceSearchKey;
    public static class threadSearchKey extends AsyncTask<Void, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... params) {
            if (serviceSearchKey != null) {
                if (!serviceSearchKey.equals("")) {
                    final String template = serviceSearchKey.replace(" ", "+");
                    String myURL = "http://suggestqueries.google.com/complete/search?hl=en&ds=yt&client=firefox&hjson=t&cp=1&q=" + template + "&format=5&alt=json&callback=?";
                    final RequestFuture<JSONArray> future = RequestFuture.newFuture();
                    JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, myURL, (String) null, future, future);
                    RequestQueue requestQueue = Volley.newRequestQueue(MSettings.activeActivity);
                    future.setRequest(requestQueue.add(request));
                    try {
                        return future.get(10, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        List<VideoItem> searchResults;
        @Override
        protected void onPostExecute(JSONArray response) {
            if (!(response == null)) {
                final String[] myAutoComplateArray = new String[10];
                try {
                    JSONArray responseTwo = (JSONArray) response.get(1);
                    for (int i = 0; i < 10; i++) {
                        myAutoComplateArray[i] = responseTwo.get(i).toString();
                    }

                    final ListView searchListView = (ListView) MSettings.body.findViewById(R.id.service_search_listview);
                    final ProgressBar progressBar = (ProgressBar) MSettings.body.findViewById(R.id.service_search_progressbar);
                    final ListView listViewVideo = (ListView) MSettings.body.findViewById(R.id.service_searchvideo_listview);

                    AdapterServiceSearchKey adapterServiceSearchKey = new AdapterServiceSearchKey(MSettings.activeActivity, myAutoComplateArray);
                    searchListView.setAdapter(adapterServiceSearchKey);
                    searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                            listViewVideo.setVisibility(View.GONE);
                            searchListView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                            listViewVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    if (searchResults.get(i).getId() != null) {
                                        MSettings.CounterForSimilarVideos = 1;
                                        MSettings.currentVItem = searchResults.get(i);
                                        MainActivity mainActivity = new MainActivity();
                                        mainActivity.getSimilarVideos(String.valueOf(searchResults.get(i).getId()), false, false, false, new String[]{});
                                        MSettings.IsRetry = false;
                                        MSettings.videoFinishStopVideoClicked = true;
                                        MSettings.LoadVideo();
                                        MSettings.LoadSixTapAds();
                                    }
                                }
                            });

                            new Thread() {
                                public void run() {
                                    ServiceSearchConnector yc = new ServiceSearchConnector(MSettings.activeActivity, "relevance", "video", false, "", false, false);
                                    searchResults = yc.search(myAutoComplateArray[i], true, false, false);
                                    mHandler.post(new Runnable() {
                                        public void run() {
                                            try {
                                                AdapterServiceSearchVideo adapter = new AdapterServiceSearchVideo(MSettings.activeActivity, searchResults, "");
                                                listViewVideo.setAdapter(adapter);

                                                progressBar.setVisibility(View.GONE);
                                                searchListView.setVisibility(View.GONE);
                                                listViewVideo.setVisibility(View.VISIBLE);
                                            } catch (Exception e) {
                                                e.printStackTrace();

                                                if (!(new NetControl(MSettings.activeActivity)).isOnline()) {
                                                    Toast.makeText(MSettings.activeActivity, MSettings.activeActivity.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    });
                                }
                            }.start();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Typeface pragatinarrow, VarelaRound_Regular;
    public static Typeface getFont(){
        if (pragatinarrow == null) {
            pragatinarrow = Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "pragatinarrow.ttf");
        } else if (pragatinarrow.equals(null)) {
            pragatinarrow = Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "pragatinarrow.ttf");
        }

        return pragatinarrow;
    }

    public static Typeface getFontVarelaRound(){
        if (VarelaRound_Regular == null) {
            VarelaRound_Regular = Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "VarelaRound-Regular.ttf");
        } else if (VarelaRound_Regular.equals(null)) {
            VarelaRound_Regular = Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "VarelaRound-Regular.ttf");
        }

        return VarelaRound_Regular;
    }

    public static void MinimizePlayer(){
        try {
            if (MSettings.floaty.floaty.getBody().getVisibility() != View.GONE) {
                MSettings.floaty.floaty.getBody().setVisibility(View.GONE);
                MSettings.floaty.floaty.getHead().setVisibility(View.VISIBLE);
                MSettings.floaty.params.x = MSettings.floaty.clickLocation[0];
                MSettings.floaty.params.y = MSettings.floaty.clickLocation[1] - 36;
                MSettings.floaty.params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                MSettings.floaty.params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                MSettings.floaty.params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                MSettings.floaty.mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));

                MSettings.floaty.windowManager.updateViewLayout(MSettings.floaty.mLinearLayout, MSettings.floaty.params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}