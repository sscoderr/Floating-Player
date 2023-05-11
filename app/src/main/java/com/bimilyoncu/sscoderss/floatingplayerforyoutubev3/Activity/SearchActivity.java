package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.AdapterForSearchVideo;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.AdapterSearchKey;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Connector.YoutubeConnector;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.NetControl;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database.DatabaseForSearchHistory;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.PlaylistList;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Result;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.ResultForPlaylist;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.ApiClient;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.playedPoss;


public class SearchActivity extends AppCompatActivity implements OnScrollListener {

    private ListView mList;
    private ListView historyList;
    private Handler handler;
    private List<VideoItem> searchResults;
    private List<VideoItem> historyComplateResults;
    private String searchText = "";
    private View myView;
    private LinearLayout emptyListLinear;
    private RelativeLayout emptyListReloadBtn;
    private AdapterForSearchVideo adapter;
    private AdapterSearchKey adapterAutoComplate;
    public Handler mHandler;
    private boolean isLoading = false;
    private boolean isLoadingForMore = false;
    private ProgressBar myPg;
    private String sortBy = "relevance", contentType = "video";
    public static Context myCt;
    private LinearLayout linearAutoComplate;
    private SearchView searchView;
    private View viewForTitle;
    private String filterText = "";
    private String myURL;
    private String[] myAutoComplateArray;
    private Spinner spinner;
    private Spinner spinnerTwo;
    private int sizeOfMoreData = 1;

    private NetControl netControl;
    private int rowCountForToken=0;
    final int MAXROWFORSEARCH=50;
    private int pageToken=1;

    private static List<Video> mSearchResults;
    private static List<PlaylistList> mSearchResultsForPlaylist;

    //private WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        pageToken=1;
        MSettings.searchActivity = SearchActivity.this;
        MSettings.activeActivity=SearchActivity.this;
        myCt = getApplicationContext();
        mList = (ListView) findViewById(R.id.videos_found);
        historyList = (ListView) findViewById(R.id.search_history);
        myPg = (ProgressBar) findViewById(R.id.myPBForFav);
        emptyListLinear = findViewById(R.id.empty_list_warner);
        emptyListReloadBtn = findViewById(R.id.empty_list_reload_btn);
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myView = li.inflate(R.layout.loading_result, null);
        mHandler = new MyHandler();
        searchResults = new ArrayList<>();
        historyComplateResults = new ArrayList<>();
        handler = new Handler();
        mList.setOnScrollListener(this);

        netControl = new NetControl(this);

        historyList.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                dismissKeyboard();
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        addClickListener();

        (SearchActivity.this).getSupportActionBar().setDisplayShowCustomEnabled(true);
        (SearchActivity.this).getSupportActionBar().setDisplayShowTitleEnabled(false);
        (SearchActivity.this).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadSomeData();

        linearAutoComplate = (LinearLayout) findViewById(R.id.auto_complate_linear);
        linearAutoComplate.setVisibility(View.VISIBLE);

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                VideoItem name = (VideoItem) adapterView.getItemAtPosition(position);
                setQueryForResult(name.getText());
            }
        });
        historyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long l) {
                DatabaseForSearchHistory db = new DatabaseForSearchHistory(getApplicationContext());
                SQLiteDatabase readableDatabase = db.getReadableDatabase();
                Integer count = (readableDatabase.rawQuery("SELECT * FROM textHistory where searchText like '"+
                        ((VideoItem) adapterView.getItemAtPosition(position)).getText()+"' order by id desc", null)).getCount();

                if (count > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
                    builder.setMessage("Delete This History");
                    builder.setTitle("Confirim Delete");
                    builder.setCancelable(true);

                    builder.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DatabaseForSearchHistory db=new DatabaseForSearchHistory(SearchActivity.this);
                                    VideoItem name =  (VideoItem) adapterView.getItemAtPosition(position);
                                    db.textDelete(name.getText());
                                    filterData(filterText);
                                }
                            });

                    builder.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                }
                return true;
            }
        });
    }

    public void loadSomeData() {
        DatabaseForSearchHistory db = new DatabaseForSearchHistory(getApplicationContext());
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM textHistory order by id desc", null);
        historyComplateResults.clear();
        historyList.setAdapter(null);
        historyComplateResults = new ArrayList<>();
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    VideoItem item = new VideoItem();
                    item.setText(cur.getString(cur.getColumnIndex("searchText")));
                    historyComplateResults.add(item);
                } while (cur.moveToNext());
            }
        }
        db.close();
        adapterAutoComplate = new AdapterSearchKey(SearchActivity.this, historyComplateResults, 0);
        historyList.setAdapter(adapterAutoComplate);
    }

    public void filterData(String filterText) {
        DatabaseForSearchHistory db = new DatabaseForSearchHistory(getApplicationContext());
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM textHistory where searchText like '%" + filterText + "%' order by id desc", null);
        historyComplateResults.clear();
        historyList.setAdapter(null);
        historyComplateResults = new ArrayList<>();
        int count = 0;
        if (filterText.equals("")) {
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        VideoItem item = new VideoItem();
                        item.setText(cur.getString(cur.getColumnIndex("searchText")));
                        historyComplateResults.add(item);
                    } while (cur.moveToNext());
                }
            }
            db.close();
        } else {
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        VideoItem item = new VideoItem();
                        item.setText(cur.getString(cur.getColumnIndex("searchText")));
                        historyComplateResults.add(item);
                    } while (cur.moveToNext());
                    count = historyComplateResults.size();
                }
            }
            db.close();
            boolean isTrue;
            for (String videoItem : myAutoComplateArray) {
                VideoItem item = new VideoItem();
                if (!(videoItem == null)) {
                    isTrue = false;
                    for (int i = 0; i < historyComplateResults.size(); i++) {
                        if (historyComplateResults.get(i).getText().equals(videoItem)) {
                            isTrue = true;
                            break;
                        }
                    }
                    if (!isTrue) {
                        item.setText(videoItem);
                        historyComplateResults.add(item);
                    }
                }
            }
        }
        if (!filterText.equals(""))
            count = historyComplateResults.size() - count;
        else count = 0;
        adapterAutoComplate = new AdapterSearchKey(SearchActivity.this, historyComplateResults, count);
        historyList.setAdapter(adapterAutoComplate);
    }

    private void addClickListener() {
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                Intent intent = null;
                if (searchResults.get(pos).getId().length() == 11) {
                    MSettings.CounterForSimilarVideos = 1;
                    playedPoss = new ArrayList<Integer>();
                    MSettings.currentVItem = searchResults.get(pos);
                    MSettings.activeVideo = searchResults.get(pos);
                    MSettings.activeActivity = SearchActivity.this;
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.getSimilarVideos(String.valueOf(searchResults.get(pos).getId()), false, false, false, new String[]{});
                    MSettings.IsRetry = false;
                    MSettings.videoFinishStopVideoClicked = true;
                    MSettings.LoadVideo();
                    MSettings.LoadSixTapAds();
                } else if (searchResults.get(pos).getId().length() == 24) {
                    intent = new Intent(SearchActivity.this, ChannelVideoList.class);
                    MSettings.activeChannelId = searchResults.get(pos).getId();
                    intent.putExtra("CHANNEL_ID", searchResults.get(pos).getId());
                    startActivity(intent);
                } else {
                    intent = new Intent(SearchActivity.this, PlaylistActivity.class);
                    MSettings.activePlaylistId = searchResults.get(pos).getId();
                    intent.putExtra("PLAYLIST_ID", searchResults.get(pos).getId());
                    intent.putExtra("PLAYLIST_NAME", searchResults.get(pos).getTitle());
                    intent.putExtra("contentType", "playlist");
                    startActivity(intent);
                }
            }

        });
    }

    private void searchOnYoutube(final String keywords, final boolean first, final boolean firstForFilter,String a) {
        new Thread() {
            public void run() {
                searchResults=new ArrayList<>();
                DatabaseForPlaylists db = new DatabaseForPlaylists(SearchActivity.this);
                SQLiteDatabase readableDatabase = db.getReadableDatabase();
                Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_discover_playlists where searchWord=?", new String[]{keywords});
                boolean value;
                if (value=(cur.moveToFirst()&&cur != null)) {
                    try {
                        do {
                            VideoItem item = new VideoItem();
                            item.setId(cur.getString(cur.getColumnIndex("playlistId")));
                            item.setTitle(cur.getString(cur.getColumnIndex("playlistName")));
                            item.setThumbnailURL(cur.getString(cur.getColumnIndex("thumbnailUrl")));
                            item.setViewCount(cur.getString(cur.getColumnIndex("videoCount")), Byte.parseByte(("2")));
                            searchResults.add(item);
                        } while (cur.moveToNext());
                    } catch (Exception e) {
                    }
                }else {
                    YoutubeConnector yc = new YoutubeConnector(SearchActivity.this, sortBy, contentType, firstForFilter, "", false, false);
                    searchResults = yc.search(keywords, first, false, false, true);
                }
                db.close();
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            adapter = new AdapterForSearchVideo(SearchActivity.this, searchResults, "");
                            mList.setAdapter(adapter);
                            myPg.setVisibility(View.INVISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();

                            if (!netControl.isOnline()) {
                                Toast.makeText(SearchActivity.this, SearchActivity.this.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                /*try {
                    if (!value&&getRowCountForToken(keywords)<MAXROWFORSEARCH){
                        for (VideoItem item : searchResults) {
                            DatabaseForPlaylists mdb=new DatabaseForPlaylists(SearchActivity.this);
                            mdb.addDiscoverPlaylists(keywords,item.getId(),item.getTitle(),item.getThumbnailURL(),item.getViewCount().substring(0,item.getViewCount().indexOf(" ")),SearchActivity.this);
                        }
                    }
                }catch (Exception e){}*/
            }
        }.start();
    }
    private void searchOnYoutube(final String keywords, final boolean first, final boolean firstForFilter) {
        new Thread() {
            public void run() {
                try {
                searchResults=new ArrayList<>();
                    if (contentType.equals("video")) {
                        com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface apiService = ApiClient.getClient().create(com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface.class);
                        Call<Result> call = apiService.getResult(keywords, String.valueOf(pageToken), contentType);
                        call.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                try {
                                    mSearchResults = response.body().getVideo();
                                    for (com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video vd : mSearchResults) {
                                            VideoItem itm = new VideoItem();
                                            itm.setId(vd.getId());
                                            itm.setTitle(vd.getTitle());
                                            itm.setChanelTitle(vd.getUsername(), false);
                                            itm.setThumbnailURL(vd.getThumbnailSrc());
                                            itm.setPublishedAt("");
                                            itm.setDurationForNewApi(vd.getDuration());
                                            itm.setViewsForNewApi(vd.getViews());
                                            searchResults.add(itm);
                                    }
                                    adapter = new AdapterForSearchVideo(SearchActivity.this, searchResults, "");
                                    mList.setAdapter(adapter);
                                    myPg.setVisibility(View.INVISIBLE);
                                }catch (Exception e){
                                    SearchActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            emptyListLinear.setVisibility(View.VISIBLE);
                                            myPg.setVisibility(View.GONE);
                                            emptyListReloadBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    searchOnYoutube(keywords,false,false);
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
                    }else if (contentType.equals("playlist")){
                        com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface apiService = ApiClient.getClient().create(com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface.class);
                        Call<ResultForPlaylist> call = apiService.getResultForPlaylist(keywords, String.valueOf(pageToken), contentType);
                        call.enqueue(new Callback<ResultForPlaylist>() {
                            @Override
                            public void onResponse(Call<ResultForPlaylist> call, Response<ResultForPlaylist> response) {
                                try {
                                    mSearchResultsForPlaylist = response.body().getVideo();
                                    for (com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.PlaylistList vd : mSearchResultsForPlaylist) {
                                            VideoItem itm = new VideoItem();
                                            itm.setId(vd.getId());
                                            itm.setTitle(vd.getTitle());
                                            itm.setChanelTitle(vd.getUsername(), false);
                                            itm.setThumbnailURL(vd.getThumbnailSrc());
                                            itm.setViewCount(vd.getVideo_count(),Byte.parseByte(("2")));
                                            searchResults.add(itm);
                                    }
                                    adapter = new AdapterForSearchVideo(SearchActivity.this, searchResults, "");
                                    mList.setAdapter(adapter);
                                    myPg.setVisibility(View.INVISIBLE);
                                }catch (Exception e){
                                    SearchActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            emptyListLinear.setVisibility(View.VISIBLE);
                                            myPg.setVisibility(View.GONE);
                                            emptyListReloadBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    searchOnYoutube(keywords,false,false);
                                                    emptyListLinear.setVisibility(View.GONE);
                                                    myPg.setVisibility(View.VISIBLE);
                                                }
                                            });
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultForPlaylist> call, Throwable t) {
                                Log.e("Error", t.toString());
                            }
                        });

                    }
                //}
                }catch (Exception e){
                    SearchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            emptyListLinear.setVisibility(View.VISIBLE);
                            myPg.setVisibility(View.GONE);
                            emptyListReloadBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    searchOnYoutube(keywords,false,false);
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

    public void showToast(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(SearchActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view.getLastVisiblePosition() == totalItemCount - 1 && mList.getCount() >= 10 && isLoading == false && sizeOfMoreData != 0 && contentType.equals("video")) {
            //if (contentType.equals("playlist")?getRowCountForToken(searchText) < MAXROWFORSEARCH:getSearchRowCountForToken(searchText) < MAXROWFORSEARCH) {
                isLoading = true;
                Thread thread = new ThreadGetMoreData();
                thread.start();
            //}
        }
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mList.addFooterView(myView);
                    isLoadingForMore=true;
                    break;
                case 1:
                    adapter.addListItemToAdapter((ArrayList<VideoItem>) msg.obj);
                    mList.removeFooterView(myView);
                    isLoading = false;
                    isLoadingForMore=false;
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
                /*mHandler.sendEmptyMessage(0);
                YoutubeConnector yc = new YoutubeConnector(SearchActivity.this,
                        sortBy, contentType, true, "", false, false);
                List<VideoItem> list = yc.search(searchText, false, false, false,true);*/
                /*if (contentType.equals("playlist")) {
                    if (getRowCountForToken(searchText) < MAXROWFORSEARCH) {
                        for (VideoItem item : list) {
                            DatabaseForPlaylists mdb = new DatabaseForPlaylists(SearchActivity.this);
                            mdb.addDiscoverPlaylists(searchText, item.getId(), item.getTitle(), item.getThumbnailURL(), item.getViewCount().substring(0, item.getViewCount().indexOf(" ")), SearchActivity.this);
                        }
                    }
                }
                else{
                    if (getSearchRowCountForToken(searchText) < MAXROWFORSEARCH) {
                        for (VideoItem item : list) {
                            DatabaseForPlaylists mdb = new DatabaseForPlaylists(SearchActivity.this);
                            mdb.addSearchHistory(searchText, item.getId(), item.getTitle(), item.getThumbnailURL(), item.getViewCountNotModified(), item.getChannelTitle(), item.getPublishedAtNotModified(), item.getDurationNotModified(), SearchActivity.this);
                        }
                    }
                }*/
                mHandler.sendEmptyMessage(0);
                pageToken++;
                List<VideoItem> list = new ArrayList<>();
                com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface apiService = ApiClient.getClient().create(com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Rest.Search.ApiInterface.class);
                Call<Result> call = apiService.getResult(searchText,String.valueOf(pageToken),contentType);
                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        mSearchResults = response.body().getVideo();
                        for (com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video vd : mSearchResults) {
                            VideoItem itm = new VideoItem();
                            itm.setId(vd.getId());
                            itm.setTitle(vd.getTitle());
                            itm.setChanelTitle(vd.getUsername(), false);
                            itm.setThumbnailURL(vd.getThumbnailSrc());
                            itm.setPublishedAt("");
                            itm.setDurationForNewApi(vd.getDuration());
                            itm.setViewsForNewApi(vd.getViews());
                            list.add(itm);
                        }

                        sizeOfMoreData = list.size();
                        ArrayList<VideoItem> lstResult = (ArrayList<VideoItem>) list;
                        Message msg = mHandler.obtainMessage(1, lstResult);
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Log.e("Error", t.toString());
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();

                if (!netControl.isOnline())
                    SearchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchActivity.this, SearchActivity.this.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                        }
                    });
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);

        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.onActionViewExpanded();

        LayoutInflater inflator = LayoutInflater.from(this);
        viewForTitle = inflator.inflate(R.layout.title_view, null);

        (SearchActivity.this).getSupportActionBar().setCustomView(viewForTitle);
        (viewForTitle.findViewById(R.id.title)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.onActionViewExpanded();
                searchView.setQuery(searchText, false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setQueryForResult(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (linearAutoComplate.getVisibility() == View.INVISIBLE) {
                    linearAutoComplate.setVisibility(View.VISIBLE);
                    mList.setVisibility(View.INVISIBLE);
                }
                filterText = newText;
                new ThreadA().execute();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void setQueryForResult(String query) {
        myPg.setVisibility(View.VISIBLE);
        sortBy = "relevance";
        contentType = "video";
        try {
            spinner.setSelection(0);
            spinnerTwo.setSelection(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mList.setAdapter(null);
            historyList.setAdapter(null);
            myPg = (ProgressBar) findViewById(R.id.myPBForFav);
            myPg.setVisibility(View.VISIBLE);
            searchOnYoutube(searchText = query, true, false);
            ((TextView) viewForTitle.findViewById(R.id.title)).setText(query);
            searchView.onActionViewCollapsed();
            DatabaseForSearchHistory db = new DatabaseForSearchHistory(getApplicationContext());
            db.textAdd(query, SearchActivity.this);
            linearAutoComplate.setVisibility(View.INVISIBLE);
            mList.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();

            if (!netControl.isOnline())
                Toast.makeText(SearchActivity.this, SearchActivity.this.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuFilter:
                LayoutInflater li = LayoutInflater.from(SearchActivity.this);
                View view = li.inflate(R.layout.filter_dialog, null);

                final Dialog save = new Dialog(SearchActivity.this);
                save.requestWindowFeature(Window.FEATURE_NO_TITLE);
                save.setContentView(view);

                TextView textViewHead = (TextView) save.findViewById(R.id.filterdialog_textview_head);
                TextView textViewSiralama = (TextView) save.findViewById(R.id.filterdialog_textview_siralama);
                TextView textViewIcerikTuru = (TextView) save.findViewById(R.id.filterdialog_textview_icerikTuru);
                Button btnCancel = (Button) save.findViewById(R.id.btn_Cancel);
                Button btnApply = (Button) save.findViewById(R.id.btn_Apply);

                textViewHead.setTypeface(MSettings.getFontVarelaRound());
                textViewSiralama.setTypeface(MSettings.getFontVarelaRound());
                textViewIcerikTuru.setTypeface(MSettings.getFontVarelaRound());
                btnCancel.setTypeface(MSettings.getFontVarelaRound());
                btnApply.setTypeface(MSettings.getFontVarelaRound());

                spinner = (Spinner) save.findViewById(R.id.spinnerSortBy);

                spinner.setOnItemSelectedListener(new OnSpinnerItemClicked());
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sortby_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                switch (sortBy) {
                    case "relevance":
                        spinner.setSelection(0);
                        break;
                    case "viewCount":
                        spinner.setSelection(1);
                        break;
                    case "date":
                        spinner.setSelection(2);
                        break;
                    default:
                        spinner.setSelection(0);
                        break;
                }

                spinnerTwo = (Spinner) save.findViewById(R.id.spinnerContenType);
                spinnerTwo.setOnItemSelectedListener(new OnSpinnerItemClicked());
                ArrayAdapter<CharSequence> adapterTwo = ArrayAdapter.createFromResource(this, R.array.sortby_arrayTwo, android.R.layout.simple_spinner_item);
                adapterTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerTwo.setAdapter(adapterTwo);
                switch (contentType) {
                    case "video":
                        spinnerTwo.setSelection(0);
                        break;
                    case "playlist":
                        spinnerTwo.setSelection(1);
                        break;
                    default:
                        spinnerTwo.setSelection(0);
                        break;
                }
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        save.dismiss();
                    }
                });
                btnApply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pageToken=1;
                        mList.setAdapter(null);
                        searchOnYoutube(searchText, true, true);
                        save.dismiss();
                        myPg = (ProgressBar) findViewById(R.id.myPBForFav);
                        myPg.setVisibility(View.VISIBLE);
                    }
                });
                save.show();
                return super.onOptionsItemSelected(item);
            case android.R.id.home:
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
    }

    public class OnSpinnerItemClicked implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            switch (parent.getId()) {
                case R.id.spinnerSortBy:
                    switch (pos) {
                        case 0:
                            sortBy = "relevance";
                            break;
                        case 1:
                            sortBy = "viewCount";
                            break;
                        case 2:
                            sortBy = "date";
                            break;
                    }
                    break;
                case R.id.spinnerContenType:
                    switch (pos) {
                        case 0:
                            contentType = "video";
                            break;
                        case 1:
                            contentType = "playlist";
                            break;
                    }
                    break;
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    private void dismissKeyboard() {/*Hide Keyword*/
        InputMethodManager imm = (InputMethodManager) SearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
    }

    protected class ThreadA extends AsyncTask<Void, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Void... params) {
            if (!filterText.equals("")) {
                final String template = filterText.replace(" ", "+");
                myURL = "http://suggestqueries.google.com/complete/search?hl=en&ds=yt&client=firefox&hjson=t&cp=1&q=" + template + "&format=5&alt=json&callback=?";
                final RequestFuture<JSONArray> future = RequestFuture.newFuture();
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, myURL, (String) null, future, future);
                RequestQueue requestQueue = Volley.newRequestQueue(SearchActivity.this);
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
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray response) {
            if (!(response == null)) {
                myAutoComplateArray = new String[10];
                try {
                    JSONArray responseTwo = (JSONArray) response.get(1);
                    for (int i = 0; i < 10; i++)
                        myAutoComplateArray[i] = responseTwo.get(i).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try {
                filterData(filterText);
            } catch (Exception e) {
                e.printStackTrace();
                if (!netControl.isOnline()) {
                    Toast.makeText(SearchActivity.this, SearchActivity.this.getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                }
            }

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

    /*private int getRowCountForToken(String word){
        DatabaseForPlaylists db = new DatabaseForPlaylists(SearchActivity.this);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("SELECT COUNT(*) AS count FROM tbl_discover_playlists where searchWord=?", new String[]{word});
        if (cursor != null&&cursor.moveToFirst())
            rowCountForToken=cursor.getInt(cursor.getColumnIndex("count"));
        else
            rowCountForToken=0;
        cursor.close();
        db.close();
        return rowCountForToken;
    }
    private int getSearchRowCountForToken(String word){
        DatabaseForPlaylists db = new DatabaseForPlaylists(SearchActivity.this);
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("SELECT COUNT(*) AS count FROM tbl_search_history where searchWord=?", new String[]{word});
        if (cursor != null&&cursor.moveToFirst())
            rowCountForToken=cursor.getInt(cursor.getColumnIndex("count"));
        else
            rowCountForToken=0;
        cursor.close();
        db.close();
        return rowCountForToken;
    }*/

}