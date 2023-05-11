package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.core.app.NotificationManagerCompat;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.ChannelVideoList;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.MainActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.PlaylistActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.SearchActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.AdapterServiceSearchKey;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.AdapterSearchVideo;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Floaties.Floaty;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.Fragment.MyDateFragment;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Adapters.AdapterForSimilarVideos;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Result;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.ApiClient;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.common.io.BaseEncoding;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Sabahattin on 2.04.2017.
 */

public class MSettings {
    public static Boolean CheckService = false;
    public static String youtubeWatchURL = "https://www.youtube.com/watch?v=";
    public static View body;
    public static View head;
    public static String countryCode;

    public static Activity mainActivity;
    public static Activity searchActivity;
    public static Activity channelActivity;
    public static Activity playlistActivity;

    public static Floaty floaty;
    public static WebView webView;
    public static ListView mListForFloat;
    public static Handler mHandler;
    public static Activity activeActivity;
    public static AdapterSearchVideo mAdapter;
    public static List<VideoItem> similarVideosList;
    public static Boolean isPlayedVideo = false;
    public static Integer CounterForSimilarVideos = 1;
    public static boolean checkRepeat = false;
    public static boolean checkSuffle = false;
    public static boolean videoFinishStopVideo = false;
    public static ArrayList<Integer> playedPoss = new ArrayList<>();
    public static Set<String> keysHashList;
    private static int counterForSuffle = 0;
    public static String activeChannelId = "";
    public static String activePlaylistId = "";
    public static boolean isUserVideo = false;
    public static boolean isLaterRepeate = false;
    public static final String URL = "http://sscoderr.com/player/playerss.html";
    public static byte adsCounter = 0;
    public static Token token = new Token();
    public static VideoItem currentVItem;
    public static VideoItem activeVideo;
    public static boolean similarVideosIsLoaded=false;
    public static boolean IsRetry = false;
    public static boolean IsonPlayerNext = false;
    public static boolean videoFinishStopVideoClicked = false;

    public static AdView adViewTrendMusic;
    public static AdView adViewTrendVideo;
    public static AdView adViewMyData;
    public static AdView adViewPlaylist;
    private static InterstitialAd interstitial;

    public static RelativeLayout relOne;
    public static RelativeLayout relPowerSaver;
    public static LinearLayout linearOne;
    public static LinearLayout linearTopControls;
    public static LinearLayout linearTwo;
    public static LinearLayout linearCizgi;
    public static LinearLayout linearControls;
    //***********////************////
    public static boolean isSmallPlayerActive=false;
    public static boolean isPowerSaverMode=false;
    public static ImageView imagePlayPause;
    public static ImageView imagePlayPauseMainPlayer;
    public static Bitmap image;
    public static boolean comeFromFirst=false;
    public static String channelTitleForFirst = "";

    public static ImageView imageViewMinimizeFP;

    private static me.grantland.widget.AutofitTextView txtVideoTitle;
    private static List<com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video> serviceSearchResults;
    public static void LoadVideo() {
        NetControl netControl = new NetControl(activeActivity);
        if (netControl.isOnline()) {
            loadNotification();
            activeActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!videoFinishStopVideo || videoFinishStopVideoClicked) {
                            if (netControl.isOnline()) {
                                MSettings.videoFinishStopVideoClicked = false;
                                if ((IsonPlayerNext && checkRepeat) || (checkRepeat && IsRetry)) {
                                    IsonPlayerNext = false;
                                    webView.loadUrl(String.format("javascript:seekTo(\"%s\");", new Object[]{0}));
                                } else {

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
                                    if (preferences.getBoolean("isHighQuality", false)) {
                                        webView.loadUrl(String.format("javascript:loadVideoById(\"%s\",\"highres\");", new Object[]{currentVItem.getId()}));
                                    } else {
                                        webView.loadUrl(String.format("javascript:loadVideoById(\"%s\",\"small\");", new Object[]{currentVItem.getId()}));
                                    }
                                    MSettings.activeVideo = currentVItem;
                                    DatabaseForPlaylists db = new DatabaseForPlaylists(activeActivity);
                                    db.addVideoHistory(currentVItem.getId(), currentVItem.getTitle(), currentVItem.getChannelTitle(), currentVItem.getThumbnailURL(), activeActivity);
                                    MyDateFragment.isHaveUpdate = true;

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                while (!similarVideosIsLoaded){Log.e("","");};
                                                if (!checkRepeat && !checkSuffle) {
                                                    currentVItem = similarVideosList.get(CounterForSimilarVideos);
                                                    CounterForSimilarVideos += 1;
                                                } else if (checkSuffle)
                                                    currentVItem = similarVideosList.get(suffleVideo());
                                            } catch (Exception e) {
                                                e.printStackTrace();

                                                if (CounterForSimilarVideos >= similarVideosList.size())
                                                    CounterForSimilarVideos = 0;
                                            }
                                        }
                                    }).start();

                                    /*new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Log.e("Track","Msg:0::"+similarVideosIsLoaded);
                                                while (!similarVideosIsLoaded){Log.e("","");};
                                                Log.e("Track","Msg:1");

                                                if (currentVItem.getTitle()==null) {
                                                    if (txtVideoTitle.getText().toString().equals("title")) {
                                                        activeActivity.runOnUiThread(new Runnable() {
                                                            public void run() {
                                                                currentVItem=similarVideosList.get(0);
                                                                loadNotification();
                                                                if (similarVideosList.get(0).getTitle().length() > 45) {
                                                                    txtVideoTitle.setText(similarVideosList.get(0).getTitle().substring(0, 45) + " ...");
                                                                } else {
                                                                    txtVideoTitle.setText(similarVideosList.get(0).getTitle());
                                                                }
                                                            }
                                                        });
                                                    }
                                                    DatabaseForPlaylists db = new DatabaseForPlaylists(activeActivity);
                                                    db.addVideoHistory(similarVideosList.get(0).getId(), similarVideosList.get(0).getTitle(), similarVideosList.get(0).getChannelTitle(), similarVideosList.get(0).getThumbnailURL(), activeActivity);
                                                    MyDateFragment.isHaveUpdate = true;
                                                    currentVItem=null;
                                                }
                                                if (!checkRepeat && !checkSuffle) {
                                                    currentVItem = similarVideosList.get(CounterForSimilarVideos);
                                                    CounterForSimilarVideos += 1;
                                                } else if (checkSuffle)
                                                    currentVItem = similarVideosList.get(suffleVideo());
                                            } catch (Exception e) {
                                                e.printStackTrace();

                                                if (CounterForSimilarVideos >= similarVideosList.size())
                                                    CounterForSimilarVideos = 0;
                                            }
                                        }
                                    }).start();*/


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
        else Toast.makeText(activeActivity, activeActivity.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
    }
    public static void loadNotification() {
        Picasso.with(activeActivity).load(MSettings.currentVItem.getThumbnailURL()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                MSettings.image = bitmap;
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        if (NotificationManagerCompat.from(MSettings.activeActivity).areNotificationsEnabled()) {
            Floaty.resetNotification(false);
            Floaty.updateNotification(-1);
        }
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
        txtVideoTitle = (me.grantland.widget.AutofitTextView) MSettings.body.findViewById(R.id.txtVideoTitle);
        txtVideoTitle.setTypeface(Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "VarelaRound-Regular.ttf"));
        try {
            if (currentVItem.getTitle().length() > 45) {
                txtVideoTitle.setText(currentVItem.getTitle().substring(0, 45) + " ...");
            } else {
                txtVideoTitle.setText(currentVItem.getTitle());
            }
        } catch (Exception e) {
            e.printStackTrace();
            txtVideoTitle.setText("title");
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
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("5C6AACA525A3BCD06EAF02A4E2E59627")
                //.addTestDevice("C351F406A4D495796C53E9B2FD34AC62")
//                .addTestDevice("C351F406A4D495796C53E9B2FD34AC62")
//                .addTestDevice("D840C07DDBAA5E0897B010411FABE6AC")
//                .addTestDevice("778ADE18482DD7E44193371217202427")
//                .addTestDevice("6AFA29CB9314195950E590C9BEACC344")
//                .addTestDevice("0CEA9CA5F2DAED70F0678D8F2D8669A3")
                .build();
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
    public static void DestroyOrShowBanner(AdView adView, boolean isDestroyBanner){
        if (adView!=null){
        AdRequest adRequest = new AdRequest.Builder().build();
        if(!isDestroyBanner){
            if (adView.getVisibility()==View.GONE) {
                adView.loadAd(adRequest);
                adView.setVisibility(View.VISIBLE);
            }
        }else {
            if (adView.getVisibility()==View.VISIBLE) {
                adView.destroy();
                adView.setVisibility(View.GONE);
            }
        }
        }
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

                            com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface apiService = ApiClient.getClient().create(com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface.class);
                            Call<Result> call = apiService.getResult(myAutoComplateArray[i],"video");
                            call.enqueue(new Callback<Result>() {
                                @Override
                                public void onResponse(Call<Result> call, Response<Result> response) {
                                    serviceSearchResults = response.body().getVideo();
                                    searchResults=new ArrayList<>();
                                    for (com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video vd:serviceSearchResults) {
                                        if (vd.getId().length()<=15) {
                                            VideoItem itm = new VideoItem();
                                            itm.setId(vd.getId());
                                            itm.setTitle(vd.getTitle());
                                            itm.setChanelTitle(vd.getUsername(), false);
                                            itm.setThumbnailURL(vd.getThumbnailSrc());
                                            searchResults.add(itm);
                                        }
                                    }
                                    AdapterForSimilarVideos mAdapter = new AdapterForSimilarVideos(MSettings.activeActivity,serviceSearchResults);
                                    listViewVideo.setAdapter(null);
                                    listViewVideo.setAdapter(mAdapter);

                                    progressBar.setVisibility(View.GONE);
                                    searchListView.setVisibility(View.GONE);
                                    listViewVideo.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onFailure(Call<Result> call, Throwable t) {
                                    Log.e("Error", t.toString());
                                }
                            });

                           /*new Thread() {
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
                            }.start();*/
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

    public static Activity activeActivityMet() {
        if (MainActivity.active) {
            return mainActivity;
        } else if (SearchActivity.active) {
            return searchActivity;
        } else if (PlaylistActivity.active) {
            return playlistActivity;
        } else if (ChannelVideoList.active) {
            return channelActivity;
        }

        return null;
    }

    public static void MinimizePlayer() {
        try {
            if (!MSettings.isPowerSaverMode) {
                SmallPlayer();
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

    public static void SmallPlayer(){
        final float defaultSize = MSettings.activeActivity.getResources().getDimension(R.dimen.popup_default_width);
        MSettings.isSmallPlayerActive=true;
        MSettings.relOne.setLayoutParams(new LinearLayout.LayoutParams((int)defaultSize,(int) getMinimumVideoHeight(defaultSize)));
        MSettings.webView.setLayoutParams(new LinearLayout.LayoutParams((int) defaultSize, (int) getMinimumVideoHeight(defaultSize)));
        MSettings.linearOne.setVisibility(View.GONE);
        MSettings.linearTwo.setVisibility(View.GONE);
        MSettings.linearCizgi.setVisibility(View.GONE);
        MSettings.linearControls.setVisibility(View.GONE);
        SetPlayerContent();
    }
    public static void FullPlayer(){
        final float heightSize = MSettings.activeActivity.getResources().getDimension(R.dimen.popup_default_height);
        MSettings.floaty.getHead().setVisibility(View.GONE);
        MSettings.isSmallPlayerActive=false;
        MSettings.relOne.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, (int) heightSize));
        MSettings.webView.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,(int) heightSize));
        MSettings.linearOne.setVisibility(View.VISIBLE);
        MSettings.linearTwo.setVisibility(View.VISIBLE);
        MSettings.linearCizgi.setVisibility(View.VISIBLE);
        MSettings.linearControls.setVisibility(View.VISIBLE);
        SetPlayerContent();
    }

    public static void SetPlayerContent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MSettings.activeActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MSettings.webView.loadUrl("javascript:resize();");
                        }
                    });

                }
                catch (Exception e){
                }
            }
        }).start();
    }
    private static float getMinimumVideoHeight(float width) {
        //if (DEBUG) Log.d(TAG, "getMinimumVideoHeight() called with: width = [" + width + "], returned: " + height);
        return width / (16.0f / 9.0f); // Respect the 16:9 ratio that most videos have
    }
    public static void MinimizePlayerForPowerSaver() {
        try {
            final float heightSize = MSettings.activeActivity.getResources().getDimension(R.dimen.popup_default_height);
            MSettings.isSmallPlayerActive=true;
            MSettings.relOne.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, (int) heightSize));
            MSettings.webView.setLayoutParams(new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,(int) heightSize));
            MSettings.linearTopControls.setVisibility(View.GONE);
            MSettings.linearTwo.setVisibility(View.GONE);
            MSettings.linearCizgi.setVisibility(View.GONE);
            MSettings.linearControls.setVisibility(View.GONE);
            MSettings.relPowerSaver.setVisibility(View.VISIBLE);
            MSettings.floaty.params.x = MSettings.floaty.metrics.widthPixels;
            MSettings.floaty.params.y = 0;
            MSettings.floaty.params.screenBrightness=0;
            MSettings.floaty.params.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            MSettings.floaty.params.width = WindowManager.LayoutParams.MATCH_PARENT;
            MSettings.floaty.params.height = WindowManager.LayoutParams.MATCH_PARENT;
            MSettings.floaty.mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
            MSettings.floaty.windowManager.updateViewLayout(MSettings.floaty.mLinearLayout, MSettings.floaty.params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void MaximizePlayerForPowerSaver() {
        try {
            MSettings.isSmallPlayerActive=false;
            MSettings.linearTopControls.setVisibility(View.VISIBLE);
            MSettings.linearTwo.setVisibility(View.VISIBLE);
            MSettings.linearCizgi.setVisibility(View.VISIBLE);
            MSettings.linearControls.setVisibility(View.VISIBLE);
            MSettings.relPowerSaver.setVisibility(View.GONE);
            MSettings.floaty.params.x = MSettings.floaty.metrics.widthPixels;
            MSettings.floaty.params.y = 0;
            try {
                if (MSettings.activeActivity!=null) {
                    float curBrightnessValue = android.provider.Settings.System.getInt(
                            activeActivity.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
                    MSettings.floaty.params.screenBrightness = curBrightnessValue;
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            MSettings.floaty.params.flags=WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            MSettings.floaty.params.flags = MSettings.floaty.params.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            MSettings.floaty.params.width = WindowManager.LayoutParams.MATCH_PARENT;
            MSettings.floaty.params.height = WindowManager.LayoutParams.MATCH_PARENT;
            MSettings.floaty.mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
            MSettings.floaty.windowManager.updateViewLayout(MSettings.floaty.mLinearLayout, MSettings.floaty.params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}