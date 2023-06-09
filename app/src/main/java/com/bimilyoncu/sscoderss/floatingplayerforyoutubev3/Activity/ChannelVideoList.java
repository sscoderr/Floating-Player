package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.AdapterSearchVideo;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Connector.YoutubeConnector;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;

import java.util.ArrayList;
import java.util.List;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.playedPoss;

public class ChannelVideoList extends AppCompatActivity implements OnScrollListener {
    private List<VideoItem> searchResultsForChannelList;
    private ListView mList;
    private Handler handler;
    private View myView;
    private AdapterSearchVideo adapter;
    public Handler mHandler;
    private boolean isLoading = false;
    private ProgressBar myPg;
    public static String channelName = "";
    private int sizeOfMoreData=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_video_list);

        MSettings.channelActivity = ChannelVideoList.this;

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
                        adapter = new AdapterSearchVideo(ChannelVideoList.this, searchResultsForChannelList, "");
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
                MSettings.CounterForSimilarVideos = pos+1;
                playedPoss = new ArrayList<Integer>();
                MSettings.currentVItem = searchResultsForChannelList.get(pos);
                MSettings.activeVideo = searchResultsForChannelList.get(pos);
                MSettings.activeActivity = ChannelVideoList.this;
                MainActivity mainActivity = new MainActivity();
                mainActivity.getSimilarVideos(searchResultsForChannelList.get(pos).getId(), false, true, false, new String[]{});
                MSettings.IsRetry = false;
                MSettings.videoFinishStopVideoClicked = true;
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
            if (lstResult.isEmpty())
                sizeOfMoreData=0;
            Message msg = mHandler.obtainMessage(1, lstResult);
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onUserLeaveHint() {
        if (MSettings.CheckService)
        try {
            if (MSettings.floaty.floaty.getBody().getVisibility() != View.GONE) {
                MSettings.SmallPlayer();
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

    public static boolean active = false;
    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
}