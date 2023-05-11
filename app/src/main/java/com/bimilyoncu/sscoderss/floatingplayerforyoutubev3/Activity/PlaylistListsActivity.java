package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import android.widget.AbsListView.OnScrollListener;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.PlaylistListsAdapter;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Connector.YoutubeConnector;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.PlaylistListGridItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Result;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.ResultForPlaylist;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.ApiClient;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistContentDetails;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistListsActivity extends AppCompatActivity implements OnScrollListener {

    private ProgressBar myPg;
    private GridViewWithHeaderAndFooter myGrid;
    private List<PlaylistListGridItem> itemsList;

    private PlaylistListsAdapter mAdapter;
    private String category;

    public Handler mHandler;
    private View myView;
    private boolean isLoading=false;
    private int sizeOfMoreData=1;
    private int rowCountForToken=0;
    private static List<com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.PlaylistList> searchPlaylistsResults;
    private int pageToken=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_lists);
        MSettings.activeActivity=PlaylistListsActivity.this;
        category=getIntent().getStringExtra("category");
        myPg = (ProgressBar) findViewById(R.id.mypg_for_playlist_list);
        (PlaylistListsActivity.this).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        (PlaylistListsActivity.this).getSupportActionBar().setTitle(category+" "+PlaylistListsActivity.this.getResources().getString(R.string.title_activity_playlist)+"s");
        myGrid = (GridViewWithHeaderAndFooter ) findViewById(R.id.gridview_playlist);
        myGrid.setOnScrollListener(this);
        LayoutInflater li = (LayoutInflater)PlaylistListsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myView = li.inflate(R.layout.loading_result, null);
        myGrid.addFooterView(myView);
        myView.setVisibility(View.GONE);
        fillPlaylists();
        myGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(PlaylistListsActivity.this, PlaylistActivity.class);
                MSettings.activePlaylistId = itemsList.get(position).getId();
                intent.putExtra("PLAYLIST_ID", itemsList.get(position).getId());
                intent.putExtra("PLAYLIST_NAME", itemsList.get(position).getPlaylistTitle());
                intent.putExtra("contentType", "playlist");
                startActivity(intent);
            }
        });
        myPg.setVisibility(View.VISIBLE);
        mHandler = new PlaylistListsActivity.MyHandler();
        pageToken=1;

    }
    private void fillPlaylists() {
        new Thread() {
            public void run() {
                DatabaseForPlaylists db = new DatabaseForPlaylists(PlaylistListsActivity.this);
                SQLiteDatabase readableDatabase = db.getReadableDatabase();
                Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_discover_playlists where searchWord=?", new String[]{category});
                itemsList = new ArrayList<>();
                boolean value;
                if (value = (cur.moveToFirst() && cur != null)) {
                    try {
                        do {
                            PlaylistListGridItem item = new PlaylistListGridItem();
                            item.setId(cur.getString(cur.getColumnIndex("playlistId")));
                            item.setPlaylistTitle(cur.getString(cur.getColumnIndex("playlistName")));
                            item.setThumbnailUrl(cur.getString(cur.getColumnIndex("thumbnailUrl")));
                            item.setVideoCount(cur.getString(cur.getColumnIndex("videoCount")));
                            itemsList.add(item);
                        } while (cur.moveToNext());
                    } catch (Exception e) {
                    }
                } else {
                    try {
                        /*PlaylistListConnector cnn = new PlaylistListConnector(PlaylistListsActivity.this);
                        itemsList = cnn.LoadItems(category, true);*/

                        com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface apiService = ApiClient.getClient().create(com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface.class);
                        Call<ResultForPlaylist> call = apiService.getResultForPlaylist(category, "1","playlist");
                        call.enqueue(new Callback<ResultForPlaylist>() {
                            @Override
                            public void onResponse(Call<ResultForPlaylist> call, Response<ResultForPlaylist> response) {
                                searchPlaylistsResults = response.body().getVideo();
                                for (com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.PlaylistList vd : searchPlaylistsResults) {
                                    PlaylistListGridItem itm = new PlaylistListGridItem();
                                    itm.setId(vd.getId());
                                    itm.setPlaylistTitle(vd.getTitle());
                                    itm.setVideoCount(vd.getVideo_count());
                                    itm.setThumbnailUrl(vd.getThumbnailSrc());
                                    itemsList.add(itm);
                                }
                                mAdapter = new PlaylistListsAdapter(PlaylistListsActivity.this, itemsList);
                                myGrid.setAdapter(mAdapter);
                                myPg.setVisibility(View.INVISIBLE);

                                try {
                                    if (getRowCountForToken() <= 120) {
                                        for (PlaylistListGridItem item : itemsList) {
                                            DatabaseForPlaylists mdb = new DatabaseForPlaylists(PlaylistListsActivity.this);
                                            mdb.addDiscoverPlaylists(category, item.getId(), item.getPlaylistTitle(), item.getThumbnailUrl(), item.getVideoCount(), PlaylistListsActivity.this);
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultForPlaylist> call, Throwable t) {
                                Log.e("Error", t.toString());
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                db.close();
                if (value) {
                    PlaylistListsActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                mAdapter = new PlaylistListsAdapter(PlaylistListsActivity.this, itemsList);
                                myGrid.setAdapter(mAdapter);
                                myPg.setVisibility(View.INVISIBLE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }.start();
    }
    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    myView.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    mAdapter.addListItemToAdapter((ArrayList<PlaylistListGridItem>) msg.obj);
                    myView.setVisibility(View.GONE);
                    isLoading=false;
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
                pageToken++;
                /*PlaylistListConnector cnn = new PlaylistListConnector(PlaylistListsActivity.this);
                List<PlaylistListGridItem> list = cnn.LoadItems(category,false);
                if (getRowCountForToken()<120){
                    for (PlaylistListGridItem item : list) {
                        DatabaseForPlaylists mdb=new DatabaseForPlaylists(PlaylistListsActivity.this);
                        mdb.addDiscoverPlaylists(category,item.getId(),item.getPlaylistTitle(),item.getThumbnailUrl(),item.getVideoCount(),PlaylistListsActivity.this);
                    }
                }*/
                List<PlaylistListGridItem> list = new ArrayList<>();
                com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface apiService = ApiClient.getClient().create(com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface.class);
                Call<ResultForPlaylist> call = apiService.getResultForPlaylist(category,String.valueOf(pageToken),"playlist");
                call.enqueue(new Callback<ResultForPlaylist>() {
                    @Override
                    public void onResponse(Call<ResultForPlaylist> call, Response<ResultForPlaylist> response) {
                        myView.setVisibility(View.VISIBLE);
                        searchPlaylistsResults = response.body().getVideo();
                        for (com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.PlaylistList vd:searchPlaylistsResults) {
                            PlaylistListGridItem itm = new PlaylistListGridItem();
                            itm.setId(vd.getId());
                            itm.setPlaylistTitle(vd.getTitle());
                            itm.setVideoCount(vd.getVideo_count());
                            itm.setThumbnailUrl(vd.getThumbnailSrc());
                            list.add(itm);
                        }

                        if (getRowCountForToken()<120){
                            for (PlaylistListGridItem item : list) {
                                DatabaseForPlaylists mdb=new DatabaseForPlaylists(PlaylistListsActivity.this);
                                mdb.addDiscoverPlaylists(category,item.getId(),item.getPlaylistTitle(),item.getThumbnailUrl(),item.getVideoCount(),PlaylistListsActivity.this);
                            }
                        }

                        sizeOfMoreData = list.size();
                        ArrayList<PlaylistListGridItem> lstResult = (ArrayList<PlaylistListGridItem>) list;
                        Message msg = mHandler.obtainMessage(1, lstResult);
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(Call<ResultForPlaylist> call, Throwable t) {
                        Log.e("Error", t.toString());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        if(absListView.getLastVisiblePosition() == i2-1 && myGrid.getCount() >=5 && isLoading == false&&sizeOfMoreData!=0) {
            if (getRowCountForToken()<120) {
                isLoading = true;
                Thread thread = new PlaylistListsActivity.ThreadGetMoreData();
                thread.start();
            }
            else myView.setVisibility(View.GONE);
        }
    }
    private int getRowCountForToken(){
        DatabaseForPlaylists db = new DatabaseForPlaylists(PlaylistListsActivity.this);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("SELECT COUNT(*) AS count FROM tbl_discover_playlists where searchWord=?", new String[]{category});
        if (cursor != null&&cursor.moveToFirst())
            rowCountForToken=cursor.getInt(cursor.getColumnIndex("count"));
        else
            rowCountForToken=0;
        cursor.close();
        db.close();
        return rowCountForToken;
    }
    private String getToken(){
        SharedPreferences preferences = getSharedPreferences("discoverPlaylistTokens", MODE_PRIVATE);
        return preferences.getString(category,"");
    }
    private void setToken(String token){
        SharedPreferences preferences = getSharedPreferences("discoverPlaylistTokens", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(category,token);
        editor.commit();
    }

    public class PlaylistListConnector {
        private YouTube youtube;
        private YouTube.Search.List query;
        private List<PlaylistListGridItem> items;

        public PlaylistListConnector(final Context context) {
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) throws IOException {
                    String packageName = context.getPackageName();
                    String SHA1 = MSettings.getSHA1(packageName, context);
                    request.getHeaders().set("X-Android-Package", packageName);
                    request.getHeaders().set("X-Android-Cert", SHA1);
                }
            }).setApplicationName(context.getString(R.string.app_name)).build();
            try {
                query = youtube.search().list("id,snippet");
                query.setKey(YoutubeConnector.KEY);
                query.setMaxResults(15L);
                query.setType("playlist");
                query.setFields("items(id/playlistId,snippet/title,snippet/thumbnails/medium/url),nextPageToken");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public List<PlaylistListGridItem> LoadItems(String keywords,boolean isFirst) {
            items=new ArrayList<>();
            query.setQ(keywords);
            if (!isFirst)
                query.setPageToken(getToken());
            try {
                    SearchListResponse response = query.execute();
                    List<SearchResult> results = response.getItems();
                    for (SearchResult result : results) {
                        try {
                            PlaylistListGridItem item = new PlaylistListGridItem();
                            item.setPlaylistTitle(result.getSnippet().getTitle());
                            item.setThumbnailUrl(result.getSnippet().getThumbnails().getMedium().getUrl());
                            if (result.getId().getPlaylistId() != null) {
                                try {
                                    YouTube.Playlists.List playlistRequest = youtube.playlists().list("contentDetails");
                                    playlistRequest.setId(result.getId().getPlaylistId());
                                    playlistRequest.setKey(YoutubeConnector.KEY);
                                    PlaylistListResponse channelResult = playlistRequest.execute();
                                    List<Playlist> playlistsList = channelResult.getItems();
                                    if (playlistsList != null) {
                                        String videoCount;
                                        Playlist ch = playlistsList.get(0);
                                        PlaylistContentDetails cs = ch.getContentDetails();
                                        videoCount = cs.getItemCount().toString();
                                        item.setVideoCount(videoCount);
                                        item.setId(result.getId().getPlaylistId());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            items.add(item);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    setToken(response.getNextPageToken());
                return items;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
