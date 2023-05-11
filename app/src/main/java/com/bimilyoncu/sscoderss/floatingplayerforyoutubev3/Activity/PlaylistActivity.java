package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.AdapterSearchVideo;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Connector.ConnectorForPlaylist;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.NetControl;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Result;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.ApiClient;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Playlist.ApiInterface;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.DestroyOrShowBanner;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.playedPoss;

public class PlaylistActivity extends AppCompatActivity implements OnScrollListener {
    private List<VideoItem> searchResultsForPlaylist;
    private ListView mList;
    private Handler handler;
    private View myView;
    private AdapterSearchVideo adapter;
    public Handler mHandler;
    private boolean isLoading=false;
    private ProgressBar myPg;
    private LinearLayout emptyListLinear;
    private RelativeLayout emptyListReloadBtn;

    private NetControl netControl;
    private int sizeOfMoreData=1;
    private RelativeLayout relPlayAll;
    private ImageView img;
    private ImageView headerImage;

    private ImageView backButton;
    private ImageView savePlaylist;

    private String contentType;

    private List<Video> mResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        contentType=getIntent().getStringExtra("contentType");
        MSettings.adViewPlaylist = (AdView) findViewById(R.id.adViewPlaylist);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("5C6AACA525A3BCD06EAF02A4E2E59627")
                //.addTestDevice("C351F406A4D495796C53E9B2FD34AC62")
                //.addTestDevice("C351F406A4D495796C53E9B2FD34AC62")
                //.addTestDevice("D840C07DDBAA5E0897B010411FABE6AC")
                //.addTestDevice("778ADE18482DD7E44193371217202427")
                //.addTestDevice("6AFA29CB9314195950E590C9BEACC344")
                //.addTestDevice("0CEA9CA5F2DAED70F0678D8F2D8669A3")
        .build();
        MSettings.adViewPlaylist.loadAd(adRequest);

        netControl = new NetControl(PlaylistActivity.this);
        MSettings.playlistActivity = PlaylistActivity.this;
        img=(ImageView)findViewById(R.id.img_for_bg);
        headerImage=(ImageView)findViewById(R.id.playlist_img);
        Random rdm=new Random();
        switch (rdm.nextInt(4)){
            case 0:img.setImageResource(R.drawable.bg1);
            break;
            case 1:img.setImageResource(R.drawable.bg2);
            break;
            case 2:img.setImageResource(R.drawable.bg3);
            break;
            case 3:img.setImageResource(R.drawable.bg4);
            break;
        }
        relPlayAll = (RelativeLayout) findViewById(R.id.rel_play_all);
        mList = (ListView) findViewById(R.id.videolist_Playlist);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myView = li.inflate(R.layout.loading_result, null);
        mHandler = new PlaylistActivity.MyHandler();
        searchResultsForPlaylist = new ArrayList<>();
        handler = new Handler();
        mList.setOnScrollListener(this);

        mList.setAdapter(null);
        myPg = (ProgressBar) findViewById(R.id.myPBPlaylist);
        emptyListLinear = findViewById(R.id.empty_list_warner);
        emptyListReloadBtn = findViewById(R.id.empty_list_reload_btn);
        myPg.setVisibility(View.VISIBLE);
        backButton=(ImageView)findViewById(R.id.img_back_btn);
        savePlaylist=(ImageView)findViewById(R.id.img_save_playlist);
        if (!contentType.equals("playlist"))
            savePlaylist.setVisibility(View.GONE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        savePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseForPlaylists db = new DatabaseForPlaylists(PlaylistActivity.this);
                SQLiteDatabase readableDatabase = db.getReadableDatabase();
                String playlistName = getIntent().getStringExtra("PLAYLIST_NAME");
                db.addUserPlaylist(playlistName, 1, PlaylistActivity.this,0);
                Cursor mCursor = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_playlist where playlistName=?", new String[]{playlistName});
                if (mCursor != null && mCursor.moveToFirst()) {
                    String playlistId = mCursor.getString(mCursor.getColumnIndex("id"));
                    for (int position=searchResultsForPlaylist.size()-1;position>=0;position--) {
                        db.addUserVideo(searchResultsForPlaylist.get(position).getId(), searchResultsForPlaylist.get(position).getTitle(), searchResultsForPlaylist.get(position).getChannelTitle(), searchResultsForPlaylist.get(position).getThumbnailURL(), PlaylistActivity.this, playlistId);
                    }
                    Toast.makeText(PlaylistActivity.this, PlaylistActivity.this.getResources().getString(R.string.toast_video_added_playlist)+" "+playlistName, Toast.LENGTH_SHORT).show();
                }
                db.close();
                readableDatabase.close();
            }
        });

        mResult = new ArrayList<>();
        addClickListener();

        loadList();

        relPlayAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    netControl = new NetControl(PlaylistActivity.this);
                    if (netControl.isOnline()&&!searchResultsForPlaylist.isEmpty()) {
                        DestroyOrShowBanner(MSettings.adViewTrendMusic, true);
                        DestroyOrShowBanner(MSettings.adViewTrendVideo, true);
                        DestroyOrShowBanner(MSettings.adViewMyData, true);
                        DestroyOrShowBanner(MSettings.adViewPlaylist,true);
                        MSettings.CounterForSimilarVideos = 1;
                        playedPoss = new ArrayList<Integer>();
                        MSettings.currentVItem = searchResultsForPlaylist.get(0);
                        MSettings.activeVideo = searchResultsForPlaylist.get(0);
                        MSettings.activeActivity = PlaylistActivity.this;
                        MainActivity mainActivity = new MainActivity();
                        if (contentType.equals("playlist"))
                            mainActivity.getSimilarVideos(searchResultsForPlaylist,false);
                        else mainActivity.getSimilarVideos(searchResultsForPlaylist,true);
                        MSettings.IsRetry = false;
                        MSettings.videoFinishStopVideoClicked = true;
                        MSettings.LoadVideo();
                        MSettings.LoadSixTapAds();
                    }
                }catch (Exception e){

                }
            }
        });
    }

    private void loadList(){
        if (contentType.equals("playlist")) {
            searchResultsForPlaylist = new ArrayList<>();
            new Thread() {
                public void run() {
                    try {
                        /*ConnectorForPlaylist pc = new ConnectorForPlaylist(PlaylistActivity.this, getIntent().getStringExtra("PLAYLIST_ID"), false);
                        searchResultsForPlaylist = pc.search(true);
                        handler.post(new Runnable() {
                            public void run() {
                                adapter = new AdapterSearchVideo(PlaylistActivity.this, searchResultsForPlaylist, "");
                                mList.setAdapter(adapter);
                                myPg.setVisibility(View.INVISIBLE);
                            }
                        });*/
                        ApiInterface apiService = ApiClient.getClient().create(com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Playlist.ApiInterface.class);
                        Call<Result> call = apiService.getResult(getIntent().getStringExtra("PLAYLIST_ID"));
                        call.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                try {
                                    mResult = response.body().getVideo();
                                    for (com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video vd : mResult) {
                                        VideoItem itm = new VideoItem();
                                        itm.setId(vd.getId());
                                        itm.setTitle(vd.getTitle());
                                        itm.setChanelTitle(vd.getUsername(), false);
                                        itm.setThumbnailURL(vd.getThumbnailSrc());
                                        searchResultsForPlaylist.add(itm);
                                    }
                                    adapter = new AdapterSearchVideo(PlaylistActivity.this, searchResultsForPlaylist, "");
                                    mList.setAdapter(adapter);
                                    myPg.setVisibility(View.INVISIBLE);
                                }catch (Exception e){
                                    PlaylistActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            emptyListLinear.setVisibility(View.VISIBLE);
                                            myPg.setVisibility(View.GONE);
                                            emptyListReloadBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    loadList();
                                                    emptyListLinear.setVisibility(View.GONE);
                                                    myPg.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                Log.e("Error", t.toString());
                            }
                        });

                    }catch (Exception e){
                        PlaylistActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                emptyListLinear.setVisibility(View.VISIBLE);
                                myPg.setVisibility(View.GONE);
                                emptyListReloadBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        loadList();
                                        emptyListLinear.setVisibility(View.GONE);
                                        myPg.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        });
                    }

                }
            }.start();
        }
        else if (contentType.equals("countryTrendVid")){
            searchResultsForPlaylist = new ArrayList<>();
            Picasso.with(PlaylistActivity.this).load(getIntent().getStringExtra("countryFlagUrl")).into(headerImage);
            MSettings.countryCode = getIntent().getStringExtra("countryCode");
            String countryName = getIntent().getStringExtra("countryName");
            new Thread() {
                public void run() {
                    try {
                        /*ConnectorForTrends connectorForTrends = new ConnectorForTrends(PlaylistActivity.this,true);
                        searchResultsForPlaylist=connectorForTrends.Trends(true,false);*/

                        com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface apiService = ApiClient.getClient().create(com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface.class);
                        Call<Result> call = apiService.getResult(countryName+" music","video");
                        call.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                try {
                                    mResult = response.body().getVideo();
                                    for (com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video vd: mResult) {
                                        if (vd.getId().length()<=15) {
                                            VideoItem itm = new VideoItem();
                                            itm.setId(vd.getId());
                                            itm.setTitle(vd.getTitle());
                                            itm.setChanelTitle(vd.getUsername(), false);
                                            itm.setThumbnailURL(vd.getThumbnailSrc());
                                            searchResultsForPlaylist.add(itm);
                                        }
                                    }
                                    adapter = new AdapterSearchVideo(PlaylistActivity.this, searchResultsForPlaylist, "");
                                    mList.setAdapter(adapter);
                                    myPg.setVisibility(View.INVISIBLE);
                                }catch (Exception e){
                                    PlaylistActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            emptyListLinear.setVisibility(View.VISIBLE);
                                            myPg.setVisibility(View.GONE);
                                            emptyListReloadBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    loadList();
                                                    emptyListLinear.setVisibility(View.GONE);
                                                    myPg.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                Log.e("Error", t.toString());
                            }
                        });

                        /*PlaylistActivity.this.runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                adapter = new AdapterSearchVideo(PlaylistActivity.this, searchResultsForPlaylist, "");
                                mList.setAdapter(adapter);
                                myPg.setVisibility(View.INVISIBLE);
                            }
                        });*/
                    }catch (Exception e){
                        PlaylistActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                emptyListLinear.setVisibility(View.VISIBLE);
                                myPg.setVisibility(View.GONE);
                                emptyListReloadBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        loadList();
                                        emptyListLinear.setVisibility(View.GONE);
                                        myPg.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        });
                    }
                }
            }.start();
        }
        else if (contentType.equals("localPlaylist")) {
            new Thread() {
                public void run() {
                    searchResultsForPlaylist = new ArrayList<>();
                    DatabaseForPlaylists db = new DatabaseForPlaylists(PlaylistActivity.this);
                    SQLiteDatabase readableDatabase = db.getReadableDatabase();

                    Cursor cursor = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_playlist where playlistName=?", new String[]{getIntent().getStringExtra("playlistName")});
                    String playlistId="";
                    if (cursor != null && cursor.moveToFirst()) {
                        playlistId = cursor.getString(cursor.getColumnIndex("id"));
                    }
                    Cursor curTwo = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_videos where playlistId=? order by id desc", new String[]{playlistId});
                    if (curTwo != null) {
                        if (curTwo.moveToFirst()) {
                            do {
                                VideoItem vItem = new VideoItem();
                                vItem.setId(curTwo.getString(curTwo.getColumnIndex("videoId")));
                                vItem.setTitle(curTwo.getString(curTwo.getColumnIndex("videoTitle")));
                                vItem.setChanelTitle(curTwo.getString(curTwo.getColumnIndex("channelTitle")), false);
                                vItem.setThumbnailURL(curTwo.getString(curTwo.getColumnIndex("videoImgUrl")));
                                searchResultsForPlaylist.add(vItem);
                            } while (curTwo.moveToNext());
                        }
                    }
                    PlaylistActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                adapter = new AdapterSearchVideo(PlaylistActivity.this, searchResultsForPlaylist, "");
                                mList.setAdapter(adapter);
                                myPg.setVisibility(View.INVISIBLE);
                            }catch (Exception e){
                                if (!netControl.isOnline())
                                    Toast.makeText(PlaylistActivity.this, PlaylistActivity.this.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }.start();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(view.getLastVisiblePosition() == totalItemCount-1 && mList.getCount() >=10 && isLoading == false&&sizeOfMoreData!=0) {
            isLoading = true;
            Thread thread = new PlaylistActivity.ThreadGetMoreData();
            thread.start();
        }
    }
    private void addClickListener() {
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                if (getIntent().getStringExtra("contentType").equals("playlist")) {
                    MSettings.CounterForSimilarVideos = pos + 1;
                    playedPoss = new ArrayList<Integer>();
                    MSettings.currentVItem = searchResultsForPlaylist.get(pos);
                    MSettings.activeVideo = searchResultsForPlaylist.get(pos);
                    MSettings.activeActivity = PlaylistActivity.this;
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.getSimilarVideos(String.valueOf(searchResultsForPlaylist.get(pos).getId()), true, false, false, new String[]{});
                    MSettings.IsRetry = false;
                    MSettings.videoFinishStopVideoClicked = true;
                    MSettings.LoadVideo();
                    MSettings.LoadSixTapAds();
                }
                else if (getIntent().getStringExtra("contentType").equals("countryTrendVid")){
                    DestroyOrShowBanner(MSettings.adViewTrendMusic,true);
                    DestroyOrShowBanner(MSettings.adViewTrendVideo,true);
                    DestroyOrShowBanner(MSettings.adViewMyData,true);
                    DestroyOrShowBanner(MSettings.adViewPlaylist,true);
                    MSettings.CounterForSimilarVideos=1;
                    playedPoss=new ArrayList<>();
                    MSettings.currentVItem = searchResultsForPlaylist.get(pos);
                    MSettings.activeVideo = searchResultsForPlaylist.get(pos);
                    MSettings.activeActivity = PlaylistActivity.this;
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.getSimilarVideos(String.valueOf(searchResultsForPlaylist.get(pos).getId()),false,false,false,new String[]{});
                    MSettings.IsRetry = false;
                    MSettings.videoFinishStopVideoClicked = true;
                    MSettings.LoadVideo();
                    MSettings.LoadSixTapAds();
                }
                else if (getIntent().getStringExtra("contentType").equals("localPlaylist")){
                    DestroyOrShowBanner(MSettings.adViewTrendMusic,true);
                    DestroyOrShowBanner(MSettings.adViewTrendVideo,true);
                    DestroyOrShowBanner(MSettings.adViewMyData,true);
                    DestroyOrShowBanner(MSettings.adViewPlaylist,true);
                    MSettings.CounterForSimilarVideos=1;
                    playedPoss=new ArrayList<>();
                    MSettings.currentVItem = searchResultsForPlaylist.get(pos);
                    MSettings.activeVideo = searchResultsForPlaylist.get(pos);
                    MSettings.activeActivity = PlaylistActivity.this;
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.getSimilarVideos(String.valueOf(searchResultsForPlaylist.get(pos).getId()),false,false,false,new String[]{});
                    MSettings.IsRetry = false;
                    MSettings.videoFinishStopVideoClicked = true;
                    MSettings.LoadVideo();
                    MSettings.LoadSixTapAds();
                }

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

                        if (!netControl.isOnline())
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
