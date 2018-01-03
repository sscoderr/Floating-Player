package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;


import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.AdapterServiceSearchVideo;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.CustomAdapter;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.CustomAdapterForFragments;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector.ConnectorForPlaylist;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector.ConnectorForVideoId;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector.ServiceSearchConnector;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector.YoutubeConnector;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.NetControl;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Floaties.Floaty;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Floaties.JSInterface;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings.CheckService;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings.CounterForSimilarVideos;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings.checkRepeat;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings.checkSuffle;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings.similarVideosList;


public class MainActivity extends AppCompatActivity implements OnScrollListener {
    private static final int NOTIFICATION_ID = 1500;
    private static final int PERMISSION_REQUEST_CODE = 16;

    private FragmentRefreshListenerForMusic fragmentRefreshListener;
    private FragmentRefreshListenerForVideo fragmentRefreshListenerForVideo;

    private ProgressBar myPg;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View myView;
    private Handler mHandler;

    private int[] tabIcons = {R.mipmap.trend_music_icon, R.mipmap.trend_video_icon, R.mipmap.favorite_video_icon};
    private int sizeOfMoreData = 1;
    private boolean isLoading = false;


    private YouTube.Videos.List queryTwo;
    private List<VideoItem> items = new ArrayList<>();
    private YouTube youtube;

    private NetControl netControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MSettings.activeActivity = MainActivity.this;
        YoutubeConnector.KEY = YoutubeConnector.myApiKeys[(new Random()).nextInt(YoutubeConnector.myApiKeys.length)];

        netControl = new NetControl(this);
        (MainActivity.this).getSupportActionBar().setElevation(0);

        AlertgetKeys();
    }

    private void AlertgetKeys() {
        if (!netControl.isOnline()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.internetConnectionMessage))
                    .setNegativeButton(MainActivity.this.getString(R.string.internetConnectionQuit), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).setPositiveButton(MainActivity.this.getString(R.string.internetConnectionTryAgain), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    AlertgetKeys();
                }
            }).setCancelable(false).create().show();
        } else {
            mHandler = new MyHandler();

            TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
            MSettings.countryCode = tm.getNetworkCountryIso();

            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myView = li.inflate(R.layout.loading_result, null);
            connectiveStart();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_mainactivity, menu);
        MenuItem item = menu.findItem(R.id.checkHighQuality);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (preferences.getBoolean("isHighQuality", false))
            item.setChecked(true);
        else if (!preferences.getBoolean("isHighQuality", false))
            item.setChecked(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search_button) {
            Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(myIntent);
        } else if (item.getItemId() == R.id.checkHighQuality) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            if (item.isChecked()) {
                editor.putBoolean("isHighQuality", false);
                item.setChecked(false);
            } else {
                item.setChecked(true);
                editor.putBoolean("isHighQuality", true);
            }
            editor.commit();
            if (getFragmentRefreshListener() != null && getFragmentRefreshListenerForVideo() != null) {
                getFragmentRefreshListener().onRefresh();
                getFragmentRefreshListenerForVideo().onRefresh();
            }
        } else if (item.getItemId() == R.id.rate_button) {
            Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
            }
        } else if (item.getItemId() == R.id.share_button) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void connectiveStart() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new CustomAdapterForFragments(getSupportFragmentManager()));
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + MainActivity.this.getPackageName()));
                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
            }
        if (!MSettings.CheckService) {
            loadFloatWindow();
        }

        MSettings.mListForFloat.setOnScrollListener(this);
        addClickListener();
    }

    private void loadFloatWindow() {
        Intent intent = new Intent(this, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.Notification.Notification notification =
                Floaty.createNotification(this, NOTIFICATION_ID/*, "Floating Player", "", R.mipmap.play_icon_for_float,resultPendingIntent*/);

        if (MSettings.head == null)
            MSettings.head = LayoutInflater.from(this).inflate(R.layout.float_head, null);
        if (MSettings.body == null)
            MSettings.body = LayoutInflater.from(this).inflate(R.layout.float_body, null);
        ImageView imgForClose = (ImageView) MSettings.head.findViewById(R.id.imageViewForClose);
        imgForClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //MSettings.webView.loadUrl("about:blank");
                LoadFullScreenAds();
                MSettings.webView.loadUrl("javascript:stopVideo();");
                MSettings.webView.onPause();
                //MSettings.webView.pauseTimers();
                MSettings.floaty.stopService();
            }
        });

        final ImageView imgRepeat = (ImageView) MSettings.body.findViewById(R.id.img_repeat);
        final ImageView imgSuffle = (ImageView) MSettings.body.findViewById(R.id.img_suffle);
        final ImageView imageViewStopFinishVideo = (ImageView) MSettings.body.findViewById(R.id.img_stopingfinishvideo);
        imgRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkRepeat) {
                    checkRepeat = true;
                    imgRepeat.setImageResource(R.mipmap.repeat_black_icon_for_float);
                    MSettings.currentVideoId = MSettings.nextVideoId;
                    MSettings.setVideoTitle(MSettings.nextVideoTitle);
                    Toast.makeText(MainActivity.this, getString(R.string.repeatMessageOff), Toast.LENGTH_SHORT).show();
                } else {
                    checkRepeat = false;
                    MSettings.isLaterRepeate = true;
                    CounterForSimilarVideos -= 1;
                    MSettings.currentVideoId = similarVideosList.get(CounterForSimilarVideos).getId();
                    MSettings.setVideoTitle(similarVideosList.get(CounterForSimilarVideos).getTitle());
                    imgRepeat.setImageResource(R.mipmap.repeat_icon_for_float);
                    Toast.makeText(MainActivity.this, getString(R.string.repeatMessageOn), Toast.LENGTH_SHORT).show();
                }
                checkSuffle = false;
                imgSuffle.setImageResource(R.mipmap.suffle_icon_for_float);
            }
        });
        imgSuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkSuffle) {
                    checkSuffle = true;
                    imgSuffle.setImageResource(R.mipmap.suffle_black_icon_for_float);
                    MSettings.suffleVideo();
                    Toast.makeText(MainActivity.this, getString(R.string.suffleMessageOff), Toast.LENGTH_SHORT).show();
                } else {
                    checkSuffle = false;
                    imgSuffle.setImageResource(R.mipmap.suffle_icon_for_float);
                    Toast.makeText(MainActivity.this, getString(R.string.suffleMessageOn), Toast.LENGTH_SHORT).show();
                }
                checkRepeat = false;
                imgRepeat.setImageResource(R.mipmap.repeat_icon_for_float);
            }
        });
        imageViewStopFinishVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MSettings.videoFinishStopVideo) {
                    MSettings.videoFinishStopVideo = true;
                    imageViewStopFinishVideo.setImageResource(R.mipmap.autoplay_on);
                    Toast.makeText(MainActivity.this, getString(R.string.stopfinishvideoOff), Toast.LENGTH_SHORT).show();
                } else {
                    MSettings.videoFinishStopVideo = false;
                    imageViewStopFinishVideo.setImageResource(R.mipmap.autoplay_off);
                    Toast.makeText(MainActivity.this, getString(R.string.stopfinishvideoOn), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button Similar = (Button) MSettings.body.findViewById(R.id.btn_service_similar);
        Button Search = (Button) MSettings.body.findViewById(R.id.btn_service_search);
        final RelativeLayout rlSimilar = (RelativeLayout) MSettings.body.findViewById(R.id.relativesimilar);
        final LinearLayout rlSearch = (LinearLayout) MSettings.body.findViewById(R.id.relativesearch);

        Similar.setTypeface(Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "VarelaRound-Regular.ttf"));
        Search.setTypeface(Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "VarelaRound-Regular.ttf"));

        Similar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlSimilar.setVisibility(View.VISIBLE);
                rlSearch.setVisibility(View.GONE);
            }
        });

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlSimilar.setVisibility(View.GONE);
                rlSearch.setVisibility(View.VISIBLE);
            }
        });

        ServiceSearch();


        MSettings.clickableRelative = (RelativeLayout) MSettings.body.findViewById(R.id.clickRelative);
        if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 21 && !MSettings.isFirstOpenApp) {
            MSettings.clickableRelative.setVisibility(View.VISIBLE);
        }
        MSettings.clickableRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSettings.webView.loadUrl(String.format("javascript:playPause();"));
            }
        });


        MSettings.floaty = Floaty.createInstance(this, MSettings.head, MSettings.body, NOTIFICATION_ID, notification);
        MSettings.mHandler = new Handler();
        MSettings.webView = (WebView) MSettings.body.findViewById(R.id.mWebView);
        MSettings.mListForFloat = (ListView) MSettings.body.findViewById(R.id.mListForFloat);
        MSettings.activeActivity = MainActivity.this;
        loadWebView(MSettings.webView);
        //getSimilarVideos();

    }

    List<VideoItem> searchResults;
    private void ServiceSearch(){
        final EditText searchText = (EditText) MSettings.body.findViewById(R.id.editText_searchservice);
        final ListView listViewKey = (ListView) MSettings.body.findViewById(R.id.service_search_listview);
        final ListView listViewVideo = (ListView) MSettings.body.findViewById(R.id.service_searchvideo_listview);
        final ProgressBar progressBar = (ProgressBar) MSettings.body.findViewById(R.id.service_search_progressbar);

        searchText.setTypeface(Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "VarelaRound-Regular.ttf"));
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (CheckService != null) {
                    if (!String.valueOf(charSequence).equals("")) {
                        MSettings.serviceSearchKey = String.valueOf(charSequence);
                        new MSettings.threadSearchKey().execute();

                        listViewKey.setVisibility(View.VISIBLE);
                        listViewVideo.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        ImageButton imageButtonSearch = (ImageButton) MSettings.body.findViewById(R.id.imageButtonServiceSearch);
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckService != null) {
                    if (MSettings.serviceSearchKey != null) {
                        if (!MSettings.serviceSearchKey.equals("")) {
                            listViewVideo.setVisibility(View.GONE);
                            listViewKey.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);

                            listViewVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    if (searchResults.get(i).getId() != null) {
                                        MSettings.CounterForSimilarVideos = 2;
                                        MSettings.currentVideoId = searchResults.get(i).getId();
                                        MSettings.setVideoTitle(searchResults.get(i).getTitle());
                                        MainActivity mainActivity = new MainActivity();
                                        mainActivity.getSimilarVideos(String.valueOf(searchResults.get(i).getId()), false, false, false, new String[]{});
                                        MSettings.LoadVideo();
                                        MSettings.LoadSixTapAds();
                                    }
                                }
                            });

                            new Thread() {
                                public void run() {
                                    ServiceSearchConnector yc = new ServiceSearchConnector(MSettings.activeActivity, "relevance", "video", false, "", false, false);
                                    searchResults = yc.search(MSettings.serviceSearchKey, true, false, false);
                                    mHandler.post(new Runnable() {
                                        public void run() {
                                            try {
                                                AdapterServiceSearchVideo adapter = new AdapterServiceSearchVideo(MSettings.activeActivity, searchResults, "");
                                                listViewVideo.setAdapter(adapter);

                                                progressBar.setVisibility(View.GONE);
                                                listViewKey.setVisibility(View.GONE);
                                                listViewVideo.setVisibility(View.VISIBLE);
                                            } catch (Exception e) {
                                                if (!(new NetControl(MSettings.activeActivity)).isOnline()) {
                                                    Toast.makeText(MSettings.activeActivity, MSettings.activeActivity.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                    });
                                }
                            }.start();
                        }
                    }
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void startFloatyForAboveAndroidL() {
        if (!Settings.canDrawOverlays(MSettings.activeActivity)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + MSettings.activeActivity.getPackageName()));
            startActivityForResult(intent, PERMISSION_REQUEST_CODE);
        } else {
            MSettings.floaty.startService();
        }
    }

    public void loadWebView(final WebView wv) {
                /*if (Build.VERSION.SDK_INT >= PERMISSION_REQUEST_CODE) {
            wv.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= 17) {
            wv.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }*/
        /*wv.setClickable(false);
        wv.setFocusable(false);
        wv.clearCache(true);
        WebSettings settings = wv.getSettings();
        if (Build.VERSION.SDK_INT >= 17)
            settings.setMediaPlaybackRequiresUserGesture(false);
        if (Build.VERSION.SDK_INT >= 19)/*Kapatılabibilr
            wv.setWebContentsDebuggingEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setDomStorageEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        settings.setBuiltInZoomControls(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDisplayZoomControls(false);
        settings.setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36");*/
        /*****************************/
        /*if (Build.VERSION.SDK_INT >= 17)
            wv.getSettings().setMediaPlaybackRequiresUserGesture(false);
        if (Build.VERSION.SDK_INT >= 19)
            wv.setLayerType(2,null);
        else wv.setLayerType(1,null);*/
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebChromeClient(new WebChromeClient());
        wv.getSettings().setSupportZoom(false);
        wv.setHorizontalScrollBarEnabled(false);
        wv.setVerticalScrollBarEnabled(false);
        wv.setWebViewClient(new WebViewClient());
        //wv.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 4.4; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36");
        wv.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.117 Safari/537.36");
        wv.addJavascriptInterface(new JSInterface(wv), "WebPlayerInterface");
        wv.loadUrl(MSettings.URL);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                MSettings.floaty.startService();
            } else {
                Spanned message = Html.fromHtml("Please allow this permission, so <b>Floaties</b> could be drawn.");
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }*/
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view.getLastVisiblePosition() == totalItemCount - 1 && MSettings.mListForFloat.getCount() >= 4 && isLoading == false && sizeOfMoreData != 0 && !MSettings.isUserVideo) {
            isLoading = true;
            Thread thread = new MainActivity.ThreadGetMoreData();
            thread.start();
        }
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    MSettings.mListForFloat.addFooterView(myView);
                    break;
                case 1:
                    MSettings.mAdapter.addListItemToAdapter((ArrayList<VideoItem>) msg.obj);
                    MSettings.mListForFloat.removeFooterView(myView);
                    isLoading = false;
                    break;
                default:
                    break;
            }
        }
    }

    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
            try {
                mHandler.sendEmptyMessage(0);
                YoutubeConnector yc = new YoutubeConnector(MSettings.activeActivity, MSettings.currentVideoId, "", false, "", true, false);
                List<VideoItem> list = yc.search("", false, false, true);
                sizeOfMoreData = list.size();
                ArrayList<VideoItem> lstResult = (ArrayList<VideoItem>) list;
                Message msg = mHandler.obtainMessage(1, lstResult);
                mHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();

                if (!netControl.isOnline())
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                        }
                    });
            }
        }
    }

    public void getSimilarVideos(final String vId, final boolean isPlaylist, final boolean isChannel, final boolean isYoutubeUserVideo, final String[] userYoutubeVideosId) {
        MSettings.isUserVideo = false;
        if (MSettings.mListForFloat != null) {
            MSettings.mListForFloat.setAdapter(null);
        }

        myPg = (ProgressBar) MSettings.body.findViewById(R.id.myPBForPlayedVideosList);
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myPg.setVisibility(View.VISIBLE);
                    }
                });
                MSettings.isPlayedVideo = true;
                similarVideosList = new ArrayList<>();
                ConnectorForVideoId cvId = new ConnectorForVideoId(MSettings.activeActivity);
                if (!isPlaylist && !isChannel && !isYoutubeUserVideo)
                    if (vId != null) {
                        similarVideosList.addAll(cvId.getVideoItems(new String[][]{{vId}}, 0, true));
                    }
                if (isPlaylist) {
                    ConnectorForPlaylist pc = new ConnectorForPlaylist(MSettings.activeActivity, MSettings.activePlaylistId, true);
                    similarVideosList.addAll(pc.search(true));
                } else if (isChannel) {
                    YoutubeConnector yc = new YoutubeConnector(MSettings.activeActivity, "date", "video", true, MSettings.activeChannelId, false, true);
                    similarVideosList.addAll(yc.search("", true, true, false));
                } else if (isYoutubeUserVideo) {
                    MSettings.isUserVideo = true;
                    similarVideosList.addAll(getVideoItems(userYoutubeVideosId));
                } else {
                    YoutubeConnector yc = new YoutubeConnector(MSettings.activeActivity, MSettings.currentVideoId, "", false, "", true, true);
                    similarVideosList.addAll(yc.search("", true, false, true));
                }
                MSettings.mHandler.post(new Runnable() {
                    public void run() {
                        MSettings.mAdapter = new CustomAdapter(MSettings.activeActivity, similarVideosList, "");
                        //-----------MSettings.isPlayedVideo=false;
                        MSettings.mListForFloat.setAdapter(null);
                        MSettings.mListForFloat.setAdapter(MSettings.mAdapter);
                        MSettings.nextVideoId = MSettings.currentVideoId;
                        MSettings.nextVideoTitle = MSettings.currentVideoTitle;
                        if (similarVideosList.size() > 1) {
                            MSettings.currentVideoId = similarVideosList.get(1).getId();
                            MSettings.setVideoTitle(MSettings.similarVideosList.get(1).getTitle());
                        }
                        /*if (checkSuffle){
                            suffleVideo();
                        }
                        else if (!checkRepeat&&!checkSuffle) {
                            MSettings.currentVideoId = MSettings.similarVideosList.get(MSettings.CounterForSimilarVideos).getId();
                            MSettings.setVideoTitle(MSettings.similarVideosList.get(MSettings.CounterForSimilarVideos).getTitle());
                        }*/
                        myPg.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }.start();
    }

    public List<VideoItem> getVideoItems(String[] matrix) {
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                String packageName = MSettings.activeActivity.getPackageName();
                String SHA1 = MSettings.getSHA1(packageName, MSettings.activeActivity);

                request.getHeaders().set("X-Android-Package", packageName);
                request.getHeaders().set("X-Android-Cert", SHA1);
            }
        }).setApplicationName(MSettings.activeActivity.getString(R.string.app_name)).build();
        try {
            queryTwo = youtube.videos().list("snippet,contentDetails,statistics");
            queryTwo.setKey(YoutubeConnector.KEY);
        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }

        try {
            for (int i = 0; i < matrix.length; i++) {
                if (matrix[i] != null) {
                    queryTwo.setId(matrix[i]);
                    VideoListResponse response = queryTwo.execute();
                    List<Video> results = response.getItems();
                    try {
                        VideoItem item = new VideoItem();
                        item.setTitle(results.get(0).getSnippet().getTitle());
                        item.setThumbnailURL(results.get(0).getSnippet().getThumbnails().getMedium().getUrl());
                        if (results.get(0).getId() != null) {
                            item.setChanelTitle(results.get(0).getSnippet().getChannelTitle(), false);
                            item.setViewCount(results.get(0).getStatistics().getViewCount().toString(), Byte.parseByte(String.valueOf("0")));
                            item.setDuration(results.get(0).getContentDetails().getDuration());
                            item.setId(results.get(0).getId());
                            item.setPublishedAt(results.get(0).getSnippet().getPublishedAt().toString());
                        }
                        items.add(item);
                    } catch (Exception e) {
                    }
                }
            }
            return items;
        } catch (IOException e) {
            return null;
        }
    }

    private void addClickListener() {
        MSettings.mListForFloat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                if (similarVideosList.get(pos).getId().length() == 11) {
                    MSettings.currentVideoId = similarVideosList.get(pos).getId();
                    MSettings.setVideoTitle(similarVideosList.get(pos).getTitle());
                    MSettings.getVideoTitle();
                    MSettings.LoadVideo();
                    MSettings.activeActivity = MainActivity.this;
                } else
                    Toast.makeText(MSettings.activeActivity, "Getting an Error", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public FragmentRefreshListenerForMusic getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListenerForMusic fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    public interface FragmentRefreshListenerForMusic {
        void onRefresh();
    }

    public FragmentRefreshListenerForVideo getFragmentRefreshListenerForVideo() {
        return fragmentRefreshListenerForVideo;
    }

    public void setFragmentRefreshListenerForVideo(FragmentRefreshListenerForVideo fragmentRefreshListener) {
        this.fragmentRefreshListenerForVideo = fragmentRefreshListener;
    }

    public interface FragmentRefreshListenerForVideo {
        void onRefresh();
    }

    private void LoadFullScreenAds() {
        MobileAds.initialize(MainActivity.this, "ca-app-pub-5808367634056272~8476127349");
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("6AFA29CB9314195950E590C9BEACC344").build();/*778ADE18482DD7E44193371217202427 Device Id*/
        final InterstitialAd interstitial = new InterstitialAd(MainActivity.this);
        interstitial.setAdUnitId(getString(R.string.admob_interstitial_id_close_service));
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MSettings.floaty.stopService();
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        if (this.doubleBackToExitPressedOnce) {
            finish();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onUserLeaveHint() {
        try {
            if (MSettings.floaty.floaty.getBody().getVisibility() != View.GONE) {
                MSettings.floaty.floaty.getBody().setVisibility(View.GONE);
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
        super.onUserLeaveHint();
    }
}