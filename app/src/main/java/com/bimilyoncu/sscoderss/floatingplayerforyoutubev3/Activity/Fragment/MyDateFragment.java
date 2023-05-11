package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.Fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.PlaylistActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.AdapterSearchVideo;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.ExpandAdapter;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Connector.ConnectorForVideoId;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.NetControl;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.GetUserVideos;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.GetterSetterForExpandable;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.MainActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.DestroyOrShowBanner;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.playedPoss;


public class MyDateFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private ListView mList;
    private List<VideoItem> response;
    private boolean isLoadingForThread = false;
    private ProgressBar myPg;
    private Handler handler;
    private View myView;
    private AdapterSearchVideo adapter;
    private String tblName = "tbl_favorite";
    public static boolean isHaveUpdate = false;


    private HandlerThread mThread;
    private Handler thread_Handler;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = new GsonFactory();

    static final int REQUEST_CODE = 999;
    private String accountName;
    final private String TAG = getClass().getSimpleName();


    private YouTube.Playlists.List query;

    private ExpandAdapter expandableAdapter;
    private ExpandableListView expListView;
    private ArrayList<GetterSetterForExpandable> expandListFullData;

    String[] playlistId;
    private ProgressDialog dialogProgress;

    private RelativeLayout relative_fav;
    private RelativeLayout relative_history;
    private RelativeLayout relative_recommented;
    private RelativeLayout relative_fromyoutube;

    private TextView txtFav;
    private TextView txtHistory;
    private TextView txtFromYoutube;
    private TextView txtRecommented;
    private static int REQUEST_CODE_NOHAVEACCOUNT = 111;
    private GoogleAccountCredential credential;
    private YouTube ytBuilder;
    private Account account;
    public static final String OAUTH_KEY = "AIzaSyAdUniM-EjWQdNagzBI2tuoXUmf6gJDSl8";

    NetControl netControl;

    LinearLayout linearLayoutFragmentWarning;
    TextView textViewFragmentWarning;

    private FloatingActionButton fab_main, fabImportFromYT, fabCreatePlaylist;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    TextView textview_mail, textview_share;
    Boolean isOpen = false;

    private ArrayList<VideoItem> vidList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View vw = inflater.inflate(R.layout.fragment_my_data, container, false);

        netControl = new NetControl(getActivity());
        initializeFab(vw);

        MSettings.adViewMyData = (AdView) vw.findViewById(R.id.adViewMyData);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("5C6AACA525A3BCD06EAF02A4E2E59627")
                //.addTestDevice("C351F406A4D495796C53E9B2FD34AC62")
                //.addTestDevice("D840C07DDBAA5E0897B010411FABE6AC")
                //.addTestDevice("778ADE18482DD7E44193371217202427")
                //.addTestDevice("6AFA29CB9314195950E590C9BEACC344")
                //.addTestDevice("0CEA9CA5F2DAED70F0678D8F2D8669A3")
         .build();
        MSettings.adViewMyData.loadAd(adRequest);

        mList = (ListView) vw.findViewById(R.id.my_data_list);
        myPg = (ProgressBar) vw.findViewById(R.id.myPBForFav);
        expListView = (ExpandableListView) vw.findViewById(R.id.user_dataExpand);
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myView = li.inflate(R.layout.loading_result, null);
        handler = new Handler();
        loadData(tblName);

        linearLayoutFragmentWarning = (LinearLayout) vw.findViewById(R.id.linearLayout_fragment_warning);
        textViewFragmentWarning = (TextView) vw.findViewById(R.id.textView_mydata_warning);

        linearLayoutFragmentWarning.setVisibility(View.GONE);
        textViewFragmentWarning.setTypeface(Typeface.createFromAsset(vw.getContext().getAssets(), "VarelaRound-Regular.ttf"));

        addClickListener();
        clickListenerExpand();
        setupComponent();
        relative_fav = (RelativeLayout) vw.findViewById(R.id.rel_favorite);
        relative_history = (RelativeLayout) vw.findViewById(R.id.rel_history);
        relative_recommented = (RelativeLayout) vw.findViewById(R.id.rel_playlist);
        relative_fromyoutube = (RelativeLayout) vw.findViewById(R.id.rel_from_youtube);

        txtFav = (TextView) vw.findViewById(R.id.txtFavButton);
        txtHistory = (TextView) vw.findViewById(R.id.txtHistoryButton);
        txtFromYoutube = (TextView) vw.findViewById(R.id.txtFromYoutubeButton);
        txtRecommented = (TextView) vw.findViewById(R.id.txtPlaylistButton);

        txtFav.setTypeface(MSettings.getFont());
        txtHistory.setTypeface(MSettings.getFont());
        txtFromYoutube.setTypeface(MSettings.getFont());
        txtRecommented.setTypeface(MSettings.getFont());
        mList.setLongClickable(true);
        mList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(tblName.equals("tbl_playlist"))
                    if (playlistList.length!=0){
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setTitle("Delete Playlist");
                        alertDialog.setMessage("Are you Sure?");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes!",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseForPlaylists db=new DatabaseForPlaylists(getActivity());
                                        db.deletePlaylist(playlistList[i],getContext());
                                        Toast.makeText(getContext(), playlistList[i]+" Deleted", Toast.LENGTH_SHORT).show();
                                        tblName = "tbl_playlist";
                                        loadData(tblName);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                return true;
            }
        });
        relative_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myPg.getVisibility() == View.INVISIBLE) {
                    linearLayoutFragmentWarning.setVisibility(View.GONE);
                    changeButtonColor(R.id.rel_favorite);
                    expListView.setVisibility(View.INVISIBLE);
                    mList.setVisibility(View.VISIBLE);
                    if (netControl.isOnline()) {
                        if (!isLoadingForThread) {
                            tblName = "tbl_favorite";
                            loadData(tblName);
                        }
                    } else {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        relative_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myPg.getVisibility() == View.INVISIBLE) {
                    linearLayoutFragmentWarning.setVisibility(View.GONE);
                    changeButtonColor(R.id.rel_history);
                    expListView.setVisibility(View.INVISIBLE);
                    mList.setVisibility(View.VISIBLE);
                    if (netControl.isOnline()) {
                        if (!isLoadingForThread) {
                            tblName = "tbl_history";
                            loadData(tblName);
                        }
                    } else {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        relative_recommented.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myPg.getVisibility() == View.INVISIBLE) {
                    linearLayoutFragmentWarning.setVisibility(View.GONE);
                    changeButtonColor(R.id.rel_playlist);
                    expListView.setVisibility(View.INVISIBLE);
                    mList.setVisibility(View.VISIBLE);
                    if (netControl.isOnline()) {
                        if (!isLoadingForThread) {
                            tblName = "tbl_playlist";
                            loadData(tblName);
                        }
                    } else {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        relative_fromyoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myPg.getVisibility() == View.INVISIBLE) {
                    linearLayoutFragmentWarning.setVisibility(View.GONE);
                    tblName = "tbl_fromyoutube_videos";
                    mList.setAdapter(null);
                    expListView.setVisibility(View.VISIBLE);
                    mList.setVisibility(View.INVISIBLE);
                    changeButtonColor(R.id.rel_from_youtube);
                    if (netControl.isOnline())
                        getPlaylistHeaders();
                    else {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return vw;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            account = new Account(accountName, data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
            getPlayList();
        } else if (requestCode == REQUEST_CODE_NOHAVEACCOUNT)
            getPlayList();
    }

    private String getPlaylistHeaders() {
        expandListFullData = new ArrayList<>();
        expandableAdapter = new ExpandAdapter(getActivity(), expandListFullData, "tbl_fromyoutube_videos");
        expListView.setAdapter(expandableAdapter);
        final DatabaseForPlaylists db = new DatabaseForPlaylists(getActivity());
        final SQLiteDatabase readableDatabase = db.getReadableDatabase();
        final Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_playlist where isYoutubePlaylist=1", null);
        new Thread() {
            public void run() {
                if (cur != null) {
                    if (cur.moveToFirst()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myPg.setVisibility(View.VISIBLE);
                            }
                        });
                        do {
                            vidList = new ArrayList<>();
                            String playlistId = cur.getString(cur.getColumnIndex("id"));
                            final String playlistName = cur.getString(cur.getColumnIndex("playlistName"));
                            final String playlistVideoCount = cur.getString(cur.getColumnIndex("videoCount"));
                            Cursor curTwo = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_videos where playlistId=" + playlistId + "", null);
                            if (curTwo != null) {
                                if (curTwo.moveToFirst()) {
                                    do {
                                        if (curTwo.getString(curTwo.getColumnIndex("videoTitle")).equals("")) {
                                            String currentVId = curTwo.getString(curTwo.getColumnIndex("videoId"));
                                            ConnectorForVideoId cvId = new ConnectorForVideoId(MSettings.activeActivity);
                                            DatabaseForPlaylists database = new DatabaseForPlaylists(getActivity());
                                            SQLiteDatabase dbTwo = database.getWritableDatabase();
                                            ContentValues values = new ContentValues();
                                            for (VideoItem item : cvId.getVideoItems(new String[][]{{currentVId}}, 0, true)) {
                                                values.put("videoTitle", item.getTitle());
                                                values.put("channelTitle", item.getChannelTitle());
                                                values.put("videoImgUrl", item.getThumbnailURL());
                                                VideoItem mItem = new VideoItem();
                                                mItem.setTitle(item.getTitle());
                                                mItem.setThumbnailURL(item.getThumbnailURL());
                                                mItem.setChanelTitle(item.getChannelTitle(), false);
                                                mItem.setId(item.getId());
                                                vidList.add(mItem);
                                            }
                                            dbTwo.update("tbl_fromyoutube_videos", values, "videoId=?", new String[]{currentVId});
                                            dbTwo.close();
                                        }
                                        else {
                                            VideoItem vItem=new VideoItem();
                                            vItem.setId(curTwo.getString(curTwo.getColumnIndex("videoId")));
                                            vItem.setTitle(curTwo.getString(curTwo.getColumnIndex("videoTitle")));
                                            vItem.setChanelTitle(curTwo.getString(curTwo.getColumnIndex("channelTitle")),false);
                                            vItem.setThumbnailURL(curTwo.getString(curTwo.getColumnIndex("videoImgUrl")));
                                            vidList.add(vItem);
                                        }
                                    } while (curTwo.moveToNext());
                                }
                            }
                            GetterSetterForExpandable gs = new GetterSetterForExpandable(playlistName, vidList, playlistVideoCount);
                            expandListFullData.add(gs);

                        } while (cur.moveToNext());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                expandableAdapter = new ExpandAdapter(getActivity(), expandListFullData, "tbl_fromyoutube_videos");
                                expListView.setAdapter(expandableAdapter);
                            }
                        });
                    } else pickAccount();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myPg.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                db.close();
            }
        }.start();
        return "";
    }

    private void changeButtonColor(int id) {
        final int sdk = android.os.Build.VERSION.SDK_INT;
        switch (id) {
            case R.id.rel_favorite:
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    relative_fav.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_custom_button_selected));
                    relative_history.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_recommented.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_fromyoutube.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));
                } else {
                    relative_fav.setBackground(getResources().getDrawable(R.drawable.my_custom_button_selected));
                    relative_history.setBackground(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_recommented.setBackground(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_fromyoutube.setBackground(getResources().getDrawable(R.drawable.selector_my_custom_button));
                }
                txtFav.setTextColor(Color.parseColor("#FFFFFF"));
                txtHistory.setTextColor(Color.parseColor("#424242"));
                txtRecommented.setTextColor(Color.parseColor("#424242"));
                txtFromYoutube.setTextColor(Color.parseColor("#424242"));
                break;
            case R.id.rel_history:
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    relative_fav.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_history.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_custom_button_selected));
                    relative_recommented.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_fromyoutube.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));
                } else {
                    relative_fav.setBackground(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_history.setBackground(getResources().getDrawable(R.drawable.my_custom_button_selected));
                    relative_recommented.setBackground(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_fromyoutube.setBackground(getResources().getDrawable(R.drawable.selector_my_custom_button));
                }
                txtFav.setTextColor(Color.parseColor("#424242"));
                txtHistory.setTextColor(Color.parseColor("#FFFFFF"));
                txtRecommented.setTextColor(Color.parseColor("#424242"));
                txtFromYoutube.setTextColor(Color.parseColor("#424242"));
                break;
            case R.id.rel_playlist:
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    relative_fav.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_history.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_recommented.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_custom_button_selected));
                    relative_fromyoutube.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));
                } else {
                    relative_fav.setBackground(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_history.setBackground(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_recommented.setBackground(getResources().getDrawable(R.drawable.my_custom_button_selected));
                    relative_fromyoutube.setBackground(getResources().getDrawable(R.drawable.selector_my_custom_button));
                }
                txtFav.setTextColor(Color.parseColor("#424242"));
                txtHistory.setTextColor(Color.parseColor("#424242"));
                txtRecommented.setTextColor(Color.parseColor("#FFFFFF"));
                txtFromYoutube.setTextColor(Color.parseColor("#424242"));
                break;
            case R.id.rel_from_youtube:
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    relative_fav.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_history.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_recommented.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_fromyoutube.setBackgroundDrawable(getResources().getDrawable(R.drawable.my_custom_button_selected));
                } else {
                    relative_fav.setBackground(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_history.setBackground(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_recommented.setBackground(getResources().getDrawable(R.drawable.selector_my_custom_button));
                    relative_fromyoutube.setBackground(getResources().getDrawable(R.drawable.my_custom_button_selected));
                }
                txtFav.setTextColor(Color.parseColor("#424242"));
                txtHistory.setTextColor(Color.parseColor("#424242"));
                txtRecommented.setTextColor(Color.parseColor("#424242"));
                txtFromYoutube.setTextColor(Color.parseColor("#FFFFFF"));
                break;
        }
    }

    private void addClickListener() {
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                if (tblName.equals("tbl_playlist")) {
                    Intent intent = new Intent(getContext(), PlaylistActivity.class);
                    intent.putExtra("contentType","localPlaylist");
                    intent.putExtra("playlistName",playlistList[pos]);
                    startActivity(intent);
                }else {
                    DestroyOrShowBanner(MSettings.adViewTrendMusic, true);
                    DestroyOrShowBanner(MSettings.adViewTrendVideo, true);
                    DestroyOrShowBanner(MSettings.adViewMyData, true);
                    DestroyOrShowBanner(MSettings.adViewPlaylist,true);
                    MSettings.CounterForSimilarVideos = 1;
                    playedPoss = new ArrayList<Integer>();
                    MSettings.currentVItem = response.get(pos);
                    MSettings.activeVideo = response.get(pos);
                    MSettings.activeActivity = getActivity();
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.getSimilarVideos(String.valueOf(response.get(pos).getId()), false, false, false, new String[]{});
                    MSettings.IsRetry = false;
                    MSettings.videoFinishStopVideoClicked = true;
                    MSettings.LoadVideo();
                    MSettings.LoadSixTapAds();
                }
            }

        });
    }

    private void clickListenerExpand() {
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, final int i, final int i1, long l) {
                DestroyOrShowBanner(MSettings.adViewTrendMusic, true);
                DestroyOrShowBanner(MSettings.adViewTrendVideo, true);
                DestroyOrShowBanner(MSettings.adViewMyData, true);
                DestroyOrShowBanner(MSettings.adViewPlaylist,true);
                MSettings.CounterForSimilarVideos = 1;
                playedPoss = new ArrayList<Integer>();
                MSettings.currentVItem = expandListFullData.get(i).getvItems().get(i1);
                MSettings.activeActivity = getActivity();
                MainActivity mainActivity = new MainActivity();
                mainActivity.getSimilarVideos(vidList,true);
                MSettings.videoFinishStopVideoClicked = true;
                MSettings.LoadVideo();
                MSettings.LoadSixTapAds();
                return true;
            }
        });
    }

    // tbl_favorite
    // tbl_history
    // tbl_recommented
    private List<VideoItem> items = new ArrayList<>();
    private String[] playlistList;
    public void loadData(final String tblName) {
        mList.setAdapter(null);
        myPg.setVisibility(View.VISIBLE);
        isLoadingForThread = true;
        items = new ArrayList<>();
        new Thread() {
            public void run() {
                switch (tblName) {
                    case "tbl_favorite": {
                        DatabaseForPlaylists db = new DatabaseForPlaylists(getActivity());
                        SQLiteDatabase readableDatabase = db.getReadableDatabase();
                        Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_favorite order by id desc", null);
                        if (cur != null) {
                            if (cur.moveToFirst()) {
                                do {
                                    try {
                                        if (cur.getString(cur.getColumnIndex("videoTitle")).equals("")) {
                                            String currentVId = cur.getString(cur.getColumnIndex("videoId"));
                                            ConnectorForVideoId cvId = new ConnectorForVideoId(MSettings.activeActivity);
                                            DatabaseForPlaylists database = new DatabaseForPlaylists(getActivity());
                                            SQLiteDatabase dbTwo = database.getWritableDatabase();
                                            ContentValues values = new ContentValues();

                                            for (VideoItem item : cvId.getVideoItems(new String[][]{{currentVId}}, 0, true)) {
                                                values.put("videoTitle", item.getTitle());
                                                values.put("channelTitle", item.getChannelTitle());
                                                values.put("videoImgUrl", item.getThumbnailURL());
                                                VideoItem mItem = new VideoItem();
                                                mItem.setTitle(item.getTitle());
                                                mItem.setThumbnailURL(item.getThumbnailURL());
                                                mItem.setChanelTitle(item.getChannelTitle(), false);
                                                mItem.setId(item.getId());
                                                items.add(mItem);
                                            }
                                            dbTwo.update("tbl_favorite", values, "videoId=?", new String[]{currentVId});
                                            dbTwo.close();

                                        } else {
                                            VideoItem item = new VideoItem();
                                            item.setTitle(cur.getString(cur.getColumnIndex("videoTitle")));
                                            item.setThumbnailURL(cur.getString(cur.getColumnIndex("videoImgUrl")));
                                            item.setChanelTitle(cur.getString(cur.getColumnIndex("channelTitle")), false);
                                            item.setId(cur.getString(cur.getColumnIndex("videoId")));
                                            items.add(item);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } while (cur.moveToNext());
                            }
                        }
                        db.close();
                        response = items;
                    }
                    break;
                    case "tbl_history": {
                        DatabaseForPlaylists db = new DatabaseForPlaylists(getActivity());
                        SQLiteDatabase readableDatabase = db.getReadableDatabase();
                        Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_history order by viewCount desc,id desc", null);
                        if (cur != null) {
                            if (cur.moveToFirst()) {
                                do {
                                    try {
                                        if (cur.getString(cur.getColumnIndex("videoTitle")).equals("")) {
                                            String currentVId = cur.getString(cur.getColumnIndex("videoId"));
                                            ConnectorForVideoId cvId = new ConnectorForVideoId(MSettings.activeActivity);
                                            DatabaseForPlaylists database = new DatabaseForPlaylists(getActivity());
                                            SQLiteDatabase dbTwo = database.getWritableDatabase();
                                            ContentValues values = new ContentValues();
                                            for (VideoItem item : cvId.getVideoItems(new String[][]{{currentVId}}, 0, true)) {
                                                values.put("videoTitle", item.getTitle());
                                                values.put("channelTitle", item.getChannelTitle());
                                                values.put("videoImgUrl", item.getThumbnailURL());
                                                VideoItem mItem = new VideoItem();
                                                mItem.setTitle(item.getTitle());
                                                mItem.setThumbnailURL(item.getThumbnailURL());
                                                mItem.setChanelTitle(item.getChannelTitle(), false);
                                                mItem.setId(item.getId());
                                                items.add(mItem);
                                            }
                                            dbTwo.update("tbl_history", values, "videoId=?", new String[]{currentVId});
                                            dbTwo.close();
                                        } else {
                                            VideoItem item = new VideoItem();
                                            item.setTitle(cur.getString(cur.getColumnIndex("videoTitle")));
                                            item.setThumbnailURL(cur.getString(cur.getColumnIndex("videoImgUrl")));
                                            item.setChanelTitle(cur.getString(cur.getColumnIndex("channelTitle")), false);
                                            item.setId(cur.getString(cur.getColumnIndex("videoId")));
                                            items.add(item);
                                        }

                                    } catch (Exception e) {
                                    }
                                } while (cur.moveToNext());
                            }
                        }
                        db.close();
                        response = items;
                    }
                    break;
                    case "tbl_playlist": {
                        DatabaseForPlaylists db = new DatabaseForPlaylists(getActivity());
                        SQLiteDatabase readableDatabase = db.getReadableDatabase();
                        final Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_playlist where isYoutubePlaylist=0 order by id desc", null);
                        if (cur != null) {
                            if (cur.moveToFirst()) {
                                playlistList = new String[cur.getCount()];
                                new Thread() {
                                    public void run() {
                                        int i = 0;
                                        do {
                                            playlistList[i] = cur.getString(cur.getColumnIndex("playlistName"));
                                            i = i + 1;
                                        } while (cur.moveToNext());
                                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                                                android.R.layout.simple_list_item_1, playlistList);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mList.setAdapter(adapter);
                                                if (mList.getCount() <= 0) {
                                                    linearLayoutFragmentWarning.setVisibility(View.VISIBLE);
                                                    textViewFragmentWarning.setText(getString(R.string.playlist_list_is_empty));
                                                }
                                            }
                                        });
                                    }
                                }.start();
                            }
                        }
                    }
                    break;
                }
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            if (!tblName.equals("tbl_playlist")) {
                                adapter = new AdapterSearchVideo(getActivity(), response, tblName);
                                mList.setAdapter(adapter);
                            }
                            myPg.setVisibility(View.INVISIBLE);
                            isLoadingForThread = false;
                            if (mList.getCount() <= 0) {
                                if (String.valueOf(tblName).equals("tbl_favorite")) {
                                    linearLayoutFragmentWarning.setVisibility(View.VISIBLE);
                                    textViewFragmentWarning.setText(getString(R.string.favorites_list_is_empty));
                                } else if (String.valueOf(tblName).equals("tbl_history")) {
                                    linearLayoutFragmentWarning.setVisibility(View.VISIBLE);
                                    textViewFragmentWarning.setText(getString(R.string.history_list_is_empty));
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        }.start();
    }

    @Override
    public void setUserVisibleHint(boolean paramBoolean) {
        super.setUserVisibleHint(paramBoolean);
        if ((paramBoolean) && (isResumed()) && isHaveUpdate && (tblName.equals("tbl_favorite") || tblName.equals("tbl_history") || tblName.equals("tbl_recommented"))) {
            loadData(tblName);
            isHaveUpdate = false;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void getPlayList() {
        credential = GoogleAccountCredential.usingOAuth2(getContext(), Arrays.asList(YouTubeScopes.YOUTUBE_READONLY));
        credential.setBackOff(new ExponentialBackOff());
        credential.setSelectedAccount(account);
        ytBuilder = new YouTube.Builder(transport, jsonFactory, credential).setApplicationName(getContext().getString(R.string.app_name)).setHttpRequestInitializer(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) {
                credential.initialize(httpRequest);
                String packageName = getContext().getPackageName();
                String SHA1 = MSettings.getSHA1(packageName, getContext());

                httpRequest.getHeaders().set("X-Android-Package", packageName);
                httpRequest.getHeaders().set("X-Android-Cert", SHA1);
            }
        }).build();

        //GoogleAccountCredential.usingOAuth2(getContext(), Arrays.asList(YouTubeScopes.YOUTUBE_READONLY)).setBackOff(new ExponentialBackOff()).setSelectedAccount(account)
        //

        thread_Handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogProgress = ProgressDialog.show(getActivity(), "", getString(R.string.progressMessage), true);
                            dialogProgress.show();
                        }
                    });
                    ChannelListResponse rs = ytBuilder.channels().list("id,contentDetails").setMine(true).execute();
                    List<Channel> channelList = rs.getItems();
                    Channel channel = channelList.get(0);


                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                    TextView title = new TextView(getActivity());
                    title.setText(getActivity().getString(R.string.playlistChooseTitle));
                    title.setGravity(Gravity.CENTER);
                    title.setTextColor(Color.WHITE);
                    title.setPadding(0, 10, 0, 5);
                    title.setTextSize(21);
                    builderSingle.setCustomTitle(title);

                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
                    arrayAdapter.add(getString(R.string.playlistLikedVideos));
                    arrayAdapter.addAll(loadPlaylistName(channel.getId()) != null ? loadPlaylistName(channel.getId()) : new String[0]);

                    playlistId[0] = channel.getContentDetails().getRelatedPlaylists().getLikes();

                    builderSingle.setNegativeButton(getString(R.string.btnCancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialogProgress.dismiss();
                                }
                            });
                        }
                    });
                    dialogProgress.dismiss();
                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int pos) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialogProgress.show();
                                }
                            });
                            DatabaseForPlaylists db = new DatabaseForPlaylists(getActivity());
                            db.addUserPlaylist(arrayAdapter.getItem(pos).toString(), 0, getContext(),1);
                            saveUserDate(MyDateFragment.this.playlistId[pos]);
                            updateVideoCountForPlaylist(String.valueOf(getPlaylistId()));
                            dialogProgress.dismiss();
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    changeButtonColor(R.id.rel_from_youtube);
                                    mList.setVisibility(View.INVISIBLE);
                                    myPg.setVisibility(View.VISIBLE);
                                    expListView.setVisibility(View.VISIBLE);
                                    getPlaylistHeaders();
                                    myPg.setVisibility(View.VISIBLE);
                                }
                            });

                        }
                    });
                    builderSingle.show();
                } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
                    availabilityException.printStackTrace();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogProgress.dismiss();
                        }
                    });
                } catch (UserRecoverableAuthIOException userRecoverableException) {
                    userRecoverableException.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogProgress.dismiss();
                        }
                    });
                    startActivityForResult(userRecoverableException.getIntent(), REQUEST_CODE_NOHAVEACCOUNT);
                } catch (IOException e) {
                    e.printStackTrace();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogProgress.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogProgress.dismiss();
                        }
                    });
                }

            }
        });
    }

    private void pickAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private int saveUserDate(String playlistId) {
        int counter = 0;
        for (VideoItem video : new GetUserVideos(getActivity(), this.credential, playlistId).loadUserVideos()) {
            new DatabaseForPlaylists(getActivity()).addUserVideo(video.getId(),video.getTitle(),video.getChannelTitle(),video.getThumbnailURL(), getActivity(), String.valueOf(getPlaylistId()));
            final int finalCounter = counter;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialogProgress.setMessage(finalCounter + " Complated");
                }
            });
            counter++;
        }
        return counter;
    }

    private void setupComponent() {
        mThread = new HandlerThread("backgroundThread");
        mThread.start();
        thread_Handler = new Handler(mThread.getLooper());
    }

    private String[] loadPlaylistName(String channelId) {
        String[] nameList = null;
        ytBuilder = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName(getContext().getString(R.string.app_name)).setHttpRequestInitializer(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) {
                credential.initialize(httpRequest);
                String packageName = getContext().getPackageName();
                String SHA1 = MSettings.getSHA1(packageName, getContext());

                httpRequest.getHeaders().set("X-Android-Package", packageName);
                httpRequest.getHeaders().set("X-Android-Cert", SHA1);
            }
        }).build();
        try {
            query = ytBuilder.playlists().list("snippet,contentDetails");
            query.setMaxResults(50L);
            query.setChannelId(channelId);
            query.setKey(OAUTH_KEY);
        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }
        try {
            PlaylistListResponse playlistListResponse = query.execute();
            List<Playlist> playlistsList = playlistListResponse.getItems();
            nameList = new String[playlistsList.size()];
            playlistId = new String[playlistsList.size() + 1];
            for (int i = 0; i < playlistsList.size(); i++) {
                nameList[i] = playlistsList.get(i).getSnippet().getTitle();
                playlistId[i + 1] = playlistsList.get(i).getId();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nameList;
    }

    private int getPlaylistId() {
        DatabaseForPlaylists dbForCursor = new DatabaseForPlaylists(getActivity());
        SQLiteDatabase readableDatabase = dbForCursor.getReadableDatabase();
        String query = "SELECT id FROM tbl_fromyoutube_playlist order by id desc LIMIT 1";
        Cursor cur = readableDatabase.rawQuery(query, null);
        int count = 0;
        if (cur != null && cur.moveToFirst()) {
            count = Integer.parseInt(cur.getString(0));
            cur.close();
        }
        readableDatabase.close();
        return count;
    }

    private void updateVideoCountForPlaylist(String id) {
        DatabaseForPlaylists dbForCursor = new DatabaseForPlaylists(getActivity());
        SQLiteDatabase readableDatabase = dbForCursor.getReadableDatabase();
        String query = "SELECT * FROM tbl_fromyoutube_videos where playlistId=?";
        Cursor cur = readableDatabase.rawQuery(query, new String[]{id});
        int count = 0;
        if (cur != null && cur.moveToFirst()) {
            do {
                count += 1;
            }
            while (cur.moveToNext());
        }
        cur.close();
        DatabaseForPlaylists db = new DatabaseForPlaylists(getActivity());
        SQLiteDatabase dbTwo = db.getWritableDatabase();
        ContentValues value = new ContentValues();
        value.put("videoCount", count);
        dbTwo.update("tbl_fromyoutube_playlist", value, "id=?", new String[]{id});
        dbTwo.close();
    }
    private void initializeFab(View vw){
        fab_main = vw.findViewById(R.id.fab);
        fabImportFromYT = vw.findViewById(R.id.fab1);
        fabCreatePlaylist = vw.findViewById(R.id.fab2);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close_anim);
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open_anim);
        fab_clock = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_rotate_anticlock);
        textview_mail = (TextView) vw.findViewById(R.id.textview_import_from_yt);
        textview_share = (TextView) vw.findViewById(R.id.textview_create_playlist);
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpen) {
                    collapseFab();
                } else {
                    textview_mail.setVisibility(View.VISIBLE);
                    textview_share.setVisibility(View.VISIBLE);
                    fabCreatePlaylist.startAnimation(fab_open);
                    fabImportFromYT.startAnimation(fab_open);
                    fab_main.startAnimation(fab_clock);
                    fabCreatePlaylist.setClickable(true);
                    fabImportFromYT.setClickable(true);
                    isOpen = true;
                }

            }
        });


        fabCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapseFab();
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                final EditText edittext = new EditText(getActivity());
                edittext.setHint(getActivity().getResources().getString(R.string.alert_playlist_hint));
                alert.setTitle(getActivity().getResources().getString(R.string.create_new_playlist));
                alert.setView(edittext);
                alert.setPositiveButton(getActivity().getResources().getString(R.string.alert_create_playlist), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DatabaseForPlaylists db = new DatabaseForPlaylists(getActivity());
                        db.addUserPlaylist(edittext.getText().toString(), 0, getActivity(),0);
                        if (myPg.getVisibility() == View.INVISIBLE) {
                            linearLayoutFragmentWarning.setVisibility(View.GONE);
                            changeButtonColor(R.id.rel_playlist);
                            expListView.setVisibility(View.INVISIBLE);
                            mList.setVisibility(View.VISIBLE);
                            if (netControl.isOnline()) {
                                if (!isLoadingForThread) {
                                    tblName = "tbl_playlist";
                                    loadData(tblName);
                                }
                            } else {
                                Toast.makeText(getActivity(), getActivity().getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                alert.setNegativeButton(getActivity().getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();
            }
        });

        fabImportFromYT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapseFab();
                pickAccount();
            }
        });

    }
    private void collapseFab(){
        if (isOpen) {
            textview_mail.setVisibility(View.INVISIBLE);
            textview_share.setVisibility(View.INVISIBLE);
            fabCreatePlaylist.startAnimation(fab_close);
            fabImportFromYT.startAnimation(fab_close);
            fab_main.startAnimation(fab_anticlock);
            fabCreatePlaylist.setClickable(false);
            fabImportFromYT.setClickable(false);
            isOpen = false;
        }
    }
}
