package com.bimilyoncu.sscoderss.floatingplayerforyoutube;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;



import java.util.ArrayList;
import java.util.List;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.MSettings.playedPoss;

public class PlaylistActivity extends AppCompatActivity implements OnScrollListener {
    private List<VideoItem> searchResultsForPlaylist;
    private ListView mList;
    private Handler handler;
    private View myView;
    private CustomAdapter adapter;
    public Handler mHandler;
    private boolean isLoading=false;
    private ProgressBar myPg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MSettings.currentVideoId = searchResultsForPlaylist.get(0).getId();
                MSettings.setVideoTitle(searchResultsForPlaylist.get(0).getTitle());
                MSettings.activeActivity = PlaylistActivity.this;
                MSettings.LoadVideo();
            }
        });

        mList = (ListView)findViewById(R.id.videolist_Playlist);
        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myView = li.inflate(R.layout.loading_result, null);
        mHandler = new PlaylistActivity.MyHandler();
        searchResultsForPlaylist = new ArrayList<>();
        handler = new Handler();
        mList.setOnScrollListener(this);

        mList.setAdapter(null);
        myPg=(ProgressBar)findViewById(R.id.myPBPlaylist);
        myPg.setVisibility(View.VISIBLE);

        addClickListener();
        new Thread(){
            public void run(){
                ConnectorForPlaylist pc = new ConnectorForPlaylist(PlaylistActivity.this,getIntent().getStringExtra("PLAYLIST_ID"),false);
                searchResultsForPlaylist=pc.search(true);
                handler.post(new Runnable(){
                    public void run(){
                        adapter = new CustomAdapter(PlaylistActivity.this,searchResultsForPlaylist,"");
                        mList.setAdapter(adapter);
                        myPg.setVisibility(View.INVISIBLE);
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
            Thread thread = new PlaylistActivity.ThreadGetMoreData();
            thread.start();
        }
    }
    private void addClickListener() {
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                MSettings.CounterForSimilarVideos=2;
                playedPoss=new ArrayList<Integer>();
                MSettings.currentVideoId = searchResultsForPlaylist.get(pos).getId();
                MSettings.setVideoTitle(searchResultsForPlaylist.get(pos).getTitle());
                MSettings.activeActivity = PlaylistActivity.this;
                MainActivity mainActivity = new MainActivity();
                mainActivity.getSimilarVideos(String.valueOf(searchResultsForPlaylist.get(pos).getId()),true,false,false,new String[]{});
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
                        if (MSettings.isHaveNetworkAccesQuickly(PlaylistActivity.this))
                            PlaylistActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PlaylistActivity.this, PlaylistActivity.this.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
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
            ConnectorForPlaylist pc = new ConnectorForPlaylist(PlaylistActivity.this,getIntent().getStringExtra("PLAYLIST_ID"),false);
            ArrayList<VideoItem> lstResult = (ArrayList<VideoItem>) pc.search(false);
            Message msg = mHandler.obtainMessage(1, lstResult);
            mHandler.sendMessage(msg);
        }
    }
}
