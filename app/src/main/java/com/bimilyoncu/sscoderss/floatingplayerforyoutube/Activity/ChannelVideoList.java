package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.CustomAdapter;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector.YoutubeConnector;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Floaties.Floaty;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Floaties.JSInterface;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings.CounterForSimilarVideos;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings.checkRepeat;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings.checkSuffle;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings.playedPoss;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings.similarVideosList;

public class ChannelVideoList extends AppCompatActivity implements OnScrollListener {
    private List<VideoItem> searchResultsForChannelList;
    private ListView mList;
    private Handler handler;
    private View myView;
    private CustomAdapter adapter;
    public Handler mHandler;
    private boolean isLoading = false;
    private ProgressBar myPg;
    public static String channelName = "";
    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_video_list);
        mList = (ListView) findViewById(R.id.videolist_Channel);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myView = li.inflate(R.layout.loading_result, null);
        mHandler = new ChannelVideoList.MyHandler();
        searchResultsForChannelList = new ArrayList<VideoItem>();
        handler = new Handler();
        mList.setOnScrollListener(this);
        mList.setAdapter(null);
        myPg = (ProgressBar) findViewById(R.id.myPBChannel);
        myPg.setVisibility(View.VISIBLE);

        addClickListener();
        new Thread() {
            public void run() {
                YoutubeConnector yc = new YoutubeConnector(ChannelVideoList.this, "date", "video", true, getIntent().getStringExtra("CHANNEL_ID"), false, false);
                searchResultsForChannelList = yc.search("", true, true, false);
                handler.post(new Runnable() {
                    public void run() {
                        adapter = new CustomAdapter(ChannelVideoList.this, searchResultsForChannelList, "");
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
        if (view.getLastVisiblePosition() == totalItemCount - 1 && mList.getCount() >= 10 && isLoading == false) {
            isLoading = true;
            Thread thread = new ChannelVideoList.ThreadGetMoreData();
            thread.start();
        }
    }

    private void addClickListener() {
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                MSettings.CounterForSimilarVideos = 2;
                playedPoss = new ArrayList<Integer>();
                MSettings.currentVideoId = searchResultsForChannelList.get(pos).getId();
                MSettings.setVideoTitle(searchResultsForChannelList.get(pos).getTitle());
                MSettings.activeActivity = ChannelVideoList.this;
                MainActivity mainActivity = new MainActivity();
                mainActivity.getSimilarVideos(searchResultsForChannelList.get(pos).getId(), false, true, false, new String[]{});
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
                        e.printStackTrace();
                        /*ChannelVideoList.this.runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ChannelVideoList.this, ChannelVideoList.this.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                                    }
                                }
                        );*/
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
            YoutubeConnector yc = new YoutubeConnector(ChannelVideoList.this, "date", "video", true, getIntent().getStringExtra("CHANNEL_ID"), false, false);
            ArrayList<VideoItem> lstResult = (ArrayList<VideoItem>) yc.search("", false, true, false);
            Message msg = mHandler.obtainMessage(1, lstResult);
            mHandler.sendMessage(msg);

        }
    }
    private void LoadFullScreenAds() {
        MobileAds.initialize(ChannelVideoList.this, "ca-app-pub-5808367634056272~8476127349");
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("6EE0EC7A08848B41A3A8B3C52624F39A")
                .addTestDevice("D840C07DDBAA5E0897B010411FABE6AC")
                .addTestDevice("778ADE18482DD7E44193371217202427")
                .addTestDevice("6AFA29CB9314195950E590C9BEACC344")
                .addTestDevice("0CEA9CA5F2DAED70F0678D8F2D8669A3").build();
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

    @Override
    protected void onUserLeaveHint() {
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
        super.onUserLeaveHint();
    }
}