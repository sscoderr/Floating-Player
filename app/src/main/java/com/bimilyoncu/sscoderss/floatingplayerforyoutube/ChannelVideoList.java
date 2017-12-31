package com.bimilyoncu.sscoderss.floatingplayerforyoutube;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Floaties.Floaty;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.api.services.youtube.model.Channel;

import java.util.ArrayList;
import java.util.List;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.MSettings.CounterForSimilarVideos;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.MSettings.checkRepeat;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.MSettings.checkSuffle;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.MSettings.playedPoss;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.MSettings.similarVideosList;

public class ChannelVideoList extends AppCompatActivity implements OnScrollListener {
    private List<VideoItem> searchResultsForChannelList;
    private ListView mList;
    private Handler handler;
    private View myView;
    private CustomAdapter adapter;
    public Handler mHandler;
    private boolean isLoading=false;
    private ProgressBar myPg;
    public static String channelName="";
    private InterstitialAd interstitial;
    private static final int NOTIFICATION_ID = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_video_list);
        loadFloatWindow();
        mList = (ListView)findViewById(R.id.videolist_Channel);
        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myView = li.inflate(R.layout.loading_result, null);
        mHandler = new ChannelVideoList.MyHandler();
        searchResultsForChannelList = new ArrayList<VideoItem>();
        handler = new Handler();
        mList.setOnScrollListener(this);
        mList.setAdapter(null);
        myPg=(ProgressBar)findViewById(R.id.myPBChannel);
        myPg.setVisibility(View.VISIBLE);

        addClickListener();
        new Thread(){
            public void run(){
                YoutubeConnector yc=new YoutubeConnector(ChannelVideoList.this,"date","video",true,getIntent().getStringExtra("CHANNEL_ID"),false,false);
                searchResultsForChannelList =yc.search("",true,true,false);
                handler.post(new Runnable(){
                    public void run(){
                        adapter = new CustomAdapter(ChannelVideoList.this, searchResultsForChannelList,"");
                        mList.setAdapter(adapter);
                        myPg.setVisibility(View.INVISIBLE);
                        (ChannelVideoList.this).getSupportActionBar().setTitle(channelName);
                    }
                });
            }
        }.start();
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(view.getLastVisiblePosition() == totalItemCount-1 && mList.getCount() >=10 && isLoading == false) {
            isLoading = true;
            Thread thread = new ChannelVideoList.ThreadGetMoreData();
            thread.start();
        }
    }
    private void addClickListener() {
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                MSettings.CounterForSimilarVideos=2;
                playedPoss=new ArrayList<Integer>();
                MSettings.currentVideoId = searchResultsForChannelList.get(pos).getId();
                MSettings.setVideoTitle(searchResultsForChannelList.get(pos).getTitle());
                MSettings.activeActivity = ChannelVideoList.this;
                MainActivity mainActivity = new MainActivity();
                mainActivity.getSimilarVideos(searchResultsForChannelList.get(pos).getId(),false,true,false,new String[]{});
                MSettings.LoadVideo();
                MSettings.LoadSixTapAds();
            }
        });
    }
    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mList.addFooterView(myView);
                    break;
                case 1:
                    try {
                        adapter.addListItemToAdapter((ArrayList<VideoItem>) msg.obj);
                        mList.removeFooterView(myView);
                        isLoading = false;
                    } catch (Exception e) {
                        if (MSettings.isHaveNetworkAccesQuickly(ChannelVideoList.this))
                            ChannelVideoList.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChannelVideoList.this, ChannelVideoList.this.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                                }
                            });
                    }
                    break;
                default:
                    break;
            }
        }
    }
    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
                mHandler.sendEmptyMessage(0);
                YoutubeConnector yc = new YoutubeConnector(ChannelVideoList.this, "date", "video", true, getIntent().getStringExtra("CHANNEL_ID"), false,false);
                ArrayList<VideoItem> lstResult = (ArrayList<VideoItem>) yc.search("", false, true, false);
                Message msg = mHandler.obtainMessage(1, lstResult);
                mHandler.sendMessage(msg);

        }
    }
    private void loadFloatWindow(){
        Intent intent = new Intent(this, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = Floaty.createNotification(this, "Floating Player", "", R.mipmap.play_icon_for_float,resultPendingIntent);

        if(MSettings.head==null)
            MSettings.head = LayoutInflater.from(this).inflate(R.layout.float_head, null);
        if(MSettings.body==null)
            MSettings.body = LayoutInflater.from(this).inflate(R.layout.float_body, null);
        ImageView imgForClose=(ImageView)MSettings.head.findViewById(R.id.imageViewForClose);
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
        final ImageView imgRepeat=(ImageView)MSettings.body.findViewById(R.id.img_repeat);
        final ImageView imgSuffle=(ImageView)MSettings.body.findViewById(R.id.img_suffle);
        imgRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkRepeat) {
                    checkRepeat=true;
                    Toast.makeText(ChannelVideoList.this, getString(R.string.repeatMessageOn), Toast.LENGTH_SHORT).show();
                    imgRepeat.setImageResource(R.mipmap.repeat_black_icon_for_float);
                    MSettings.currentVideoId=MSettings.nextVideoId;
                    MSettings.setVideoTitle(MSettings.nextVideoTitle);
                }else {
                    checkRepeat=false;
                    Toast.makeText(ChannelVideoList.this, getString(R.string.repeatMessageOff), Toast.LENGTH_SHORT).show();
                    MSettings.isLaterRepeate=true;
                    CounterForSimilarVideos-=1;
                    MSettings.currentVideoId = similarVideosList.get(CounterForSimilarVideos).getId();
                    MSettings.setVideoTitle(similarVideosList.get(CounterForSimilarVideos).getTitle());
                    Toast.makeText(MSettings.activeActivity, String.valueOf(CounterForSimilarVideos), Toast.LENGTH_SHORT).show();
                    imgRepeat.setImageResource(R.mipmap.repeat_icon_for_float);
                }
                checkSuffle=false;
                imgSuffle.setImageResource(R.mipmap.suffle_icon_for_float);
            }
        });
        imgSuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkSuffle) {
                    checkSuffle=true;
                    Toast.makeText(ChannelVideoList.this, getString(R.string.suffleMessageOn), Toast.LENGTH_SHORT).show();
                    imgSuffle.setImageResource(R.mipmap.suffle_black_icon_for_float);
                    MSettings.suffleVideo();
                }else {
                    checkSuffle=false;
                    Toast.makeText(ChannelVideoList.this, getString(R.string.suffleMessageOff), Toast.LENGTH_SHORT).show();
                    imgSuffle.setImageResource(R.mipmap.suffle_icon_for_float);
                }
                checkRepeat=false;
                imgRepeat.setImageResource(R.mipmap.repeat_icon_for_float);

            }
        });
        MSettings.clickableRelative=(RelativeLayout)MSettings.body.findViewById(R.id.clickRelative);
        if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 21&&!MSettings.isFirstOpenApp) {
            MSettings.clickableRelative.setVisibility(View.VISIBLE);
        }
        MSettings.clickableRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSettings.webView.loadUrl(String.format("javascript:playPause();"));
            }
        });


        MSettings.floaty = Floaty.createInstance(this, MSettings.head, MSettings.body, NOTIFICATION_ID, notification);
        MSettings.mHandler=new Handler();
        MSettings.webView = (WebView) MSettings.body.findViewById(R.id.mWebView);
        MSettings.mListForFloat=(ListView)MSettings.body.findViewById(R.id.mListForFloat);
        MSettings.activeActivity=ChannelVideoList.this;
        loadWebView(MSettings.webView);
        //getSimilarVideos();

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
        wv.addJavascriptInterface(new JSInterface(wv),"WebPlayerInterface");
        wv.loadUrl("http://sabo.azurewebsites.net/index.html");
    }
    private void LoadFullScreenAds(){
        MobileAds.initialize(ChannelVideoList.this, "ca-app-pub-5808367634056272~8476127349");
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("D840C07DDBAA5E0897B010411FABE6AC").addTestDevice("778ADE18482DD7E44193371217202427").build();/*778ADE18482DD7E44193371217202427 Device Id*/
        interstitial = new InterstitialAd(ChannelVideoList.this);
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
}
