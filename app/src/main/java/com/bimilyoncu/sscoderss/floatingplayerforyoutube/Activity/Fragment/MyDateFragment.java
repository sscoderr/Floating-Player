package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.Fragment;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


//import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.CustomAdapter;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.ExpandAdapter;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector.ConnectorForUserVideo;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector.ConnectorForVideoId;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.NetControl;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item.GetUserVideos;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item.GetterSetterForExpandable;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.MainActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings.playedPoss;


public class MyDateFragment extends Fragment implements OnScrollListener,GoogleApiClient.OnConnectionFailedListener {
    private ListView mList;
    private List<VideoItem> response;
    public Handler mHandler;
    private boolean isLoading = false;
    private boolean isLoadingForThread = false;
    private ProgressBar myPg;
    private Handler handler;
    private View myView;
    private CustomAdapter adapter;
    private int order = 0;
    private int sizeOfMoreData = 1;
    private String tblName = "tbl_favorite";
    private String[][] matrix;
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
    public static final String OAUTH_KEY = "AIzaSyAMggEqksgNG5s-ZS0gWj_G9l-IVonYplM";

    NetControl netControl;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View vw = inflater.inflate(R.layout.fragment_my_data, container, false);

        netControl = new NetControl(getActivity());

        MobileAds.initialize(getContext(), "ca-app-pub-5808367634056272~8476127349");
        AdView mAdView = (AdView) vw.findViewById(R.id.adViewMyData);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("6AFA29CB9314195950E590C9BEACC344").build();
        mAdView.loadAd(adRequest);

        mList = (ListView) vw.findViewById(R.id.my_data_list);
        myPg = (ProgressBar) vw.findViewById(R.id.myPBForFav);
        expListView = (ExpandableListView) vw.findViewById(R.id.user_dataExpand);
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        myView = li.inflate(R.layout.loading_result, null);
        mList.setOnScrollListener(this);
        mHandler = new MyHandler();
        handler = new Handler();
        //if (MSettings.isHaveNetworkAccesFull())
        loadData(tblName);
        /*else {
            Runnable runnable = new Runnable()
            {
                @Override
                public void run()
                {
                    while (!MSettings.isHaveNetworkAccesFull()){
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            loadData(tblName);
                        }
                    });
                }
            };
            new Thread(runnable).start();
        }*/
        addClickListener();
        clickListenerExpand();
        setupComponent();
        relative_fav = (RelativeLayout) vw.findViewById(R.id.rel_favorite);
        relative_history = (RelativeLayout) vw.findViewById(R.id.rel_history);
        relative_recommented = (RelativeLayout) vw.findViewById(R.id.rel_recommented);
        relative_fromyoutube = (RelativeLayout) vw.findViewById(R.id.rel_from_youtube);

        txtFav = (TextView) vw.findViewById(R.id.txtFavButton);
        txtHistory = (TextView) vw.findViewById(R.id.txtHistoryButton);
        txtFromYoutube = (TextView) vw.findViewById(R.id.txtFromYoutubeButton);
        txtRecommented = (TextView) vw.findViewById(R.id.txtRecommentedButton);

        txtFav.setTypeface(MSettings.getFont());
        txtHistory.setTypeface(MSettings.getFont());
        txtFromYoutube.setTypeface(MSettings.getFont());
        txtRecommented.setTypeface(MSettings.getFont());


        relative_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myPg.getVisibility() == View.INVISIBLE) {
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
                    changeButtonColor(R.id.rel_recommented);
                    expListView.setVisibility(View.INVISIBLE);
                    mList.setVisibility(View.VISIBLE);
                    if (netControl.isOnline()) {
                        if (!isLoadingForThread) {
                            tblName = "tbl_recommented";
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
        FloatingActionButton fab = (FloatingActionButton) vw.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickAccount();
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
        final DatabaseForPlaylists db = new DatabaseForPlaylists(getContext());
        final SQLiteDatabase readableDatabase = db.getReadableDatabase();
        final Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_playlist", null);
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
                            String playlistId = cur.getString(cur.getColumnIndex("id"));
                            final String playlistName = cur.getString(cur.getColumnIndex("playlistName"));
                            final String playlistVideoCount = cur.getString(cur.getColumnIndex("videoCount"));
                            Cursor curTwo = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_videos where playlistId=" + playlistId + "", null);
                            final String[] array = new String[curTwo.getCount()];
                            int i = 0;
                            if (curTwo != null) {
                                if (curTwo.moveToFirst()) {
                                    do {
                                        array[i] = curTwo.getString(curTwo.getColumnIndex("videoId"));
                                        i++;
                                    } while (curTwo.moveToNext());
                                }
                            }
                            ConnectorForUserVideo userVideo = new ConnectorForUserVideo(getActivity());
                            GetterSetterForExpandable gs = new GetterSetterForExpandable(playlistName, userVideo.getVideoItems(array), playlistVideoCount);
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
            case R.id.rel_recommented:
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
                MSettings.CounterForSimilarVideos = 2;
                playedPoss = new ArrayList<Integer>();
                MSettings.currentVideoId = response.get(pos).getId();
                MSettings.setVideoTitle(response.get(pos).getTitle());
                MSettings.activeActivity = getActivity();
                MainActivity mainActivity = new MainActivity();
                mainActivity.getSimilarVideos(String.valueOf(response.get(pos).getId()), false, false, false, new String[]{});
                MSettings.LoadVideo();
                MSettings.LoadSixTapAds();
            }

        });
    }

    private void clickListenerExpand() {
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, final int i, final int i1, long l) {
                MSettings.CounterForSimilarVideos = 2;
                playedPoss = new ArrayList<Integer>();
                MSettings.currentVideoId = expandListFullData.get(i).getvItems().get(i1).getId();
                MSettings.setVideoTitle(expandListFullData.get(i).getvItems().get(i1).getTitle());
                MSettings.activeActivity = getActivity();
                /*MainActivity mainActivity = new MainActivity();
                mainActivity.getSimilarVideos(String.valueOf(expandListFullData.get(i).getvItems().get(i1).getId()),false,false);*/
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {*/
                String[] userYoutubeVideosId = new String[expandListFullData.get(i).getvItems().size()];
                for (int j = 0; j < expandListFullData.get(i).getvItems().size(); j++) {
                    userYoutubeVideosId[j] = expandListFullData.get(i).getvItems().get(j).getId();
                }
                MainActivity mainActivity = new MainActivity();
                mainActivity.getSimilarVideos("", false, false, true, userYoutubeVideosId);
                    /*}
                });*/
                MSettings.LoadVideo();
                MSettings.LoadSixTapAds();
                return true;
            }
        });
    }

    public void loadData(final String tblName) {
        mList.setAdapter(null);
        order = 0;
        sizeOfMoreData = 1;
        myPg.setVisibility(View.VISIBLE);
        loadVideoId(tblName);
        isLoadingForThread = true;
        new Thread() {
            public void run() {
                ConnectorForVideoId cn = new ConnectorForVideoId(getActivity());
                response = cn.getVideoItems(matrix, order, false);
                order++;
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            adapter = new CustomAdapter(getActivity(), response, tblName);
                            mList.setAdapter(adapter);
                            myPg.setVisibility(View.INVISIBLE);
                            isLoadingForThread = false;
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

    private void loadVideoId(String tblName) {
        DatabaseForPlaylists db = new DatabaseForPlaylists(getContext());
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        Cursor cur = readableDatabase.rawQuery("SELECT * FROM " + tblName + " order by id desc", null);
        String temp = String.valueOf(Math.ceil(cur.getCount() / 10.0f));
        matrix = new String[Integer.parseInt(temp.substring(0, temp.length() - 2))][10];
        int counterOne = 0, counterTwo = 0;
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    matrix[counterOne][counterTwo] = cur.getString(cur.getColumnIndex("videoId"));
                    if (counterTwo == 9) {
                        counterOne++;
                        counterTwo = 0;
                    } else counterTwo++;
                } while (cur.moveToNext());
            }
        }
        db.close();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        if (absListView.getLastVisiblePosition() == i2 - 1 && mList.getCount() >= 5 && isLoading == false && sizeOfMoreData != 0) {
            isLoading = true;
            Thread thread = new ThreadGetMoreData();
            thread.start();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mList.addFooterView(myView);
                    break;
                case 1:
                    adapter.addListItemToAdapter((ArrayList<VideoItem>) msg.obj);
                    mList.removeFooterView(myView);
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
                ConnectorForVideoId cn = new ConnectorForVideoId(getActivity());
                List<VideoItem> list = cn.getVideoItems(matrix, order, false);
                sizeOfMoreData = list.size();
                ArrayList<VideoItem> lstResult = (ArrayList<VideoItem>) list;
                order++;
                Message msg = mHandler.obtainMessage(1, lstResult);
                mHandler.sendMessage(msg);
            } catch (Exception e) {
                if (netControl.isOnline()) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                }
            }
        }
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
                            db.addUserPlaylist(arrayAdapter.getItem(pos).toString(), 0, getContext());
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogProgress.dismiss();
                        }
                    });
                } catch (UserRecoverableAuthIOException userRecoverableException) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialogProgress.dismiss();
                        }
                    });
                    startActivityForResult(userRecoverableException.getIntent(), REQUEST_CODE_NOHAVEACCOUNT);
                } catch (IOException e) {
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
        for (String video : new GetUserVideos(getActivity(), this.credential, playlistId).loadUserVideos()) {
            new DatabaseForPlaylists(getActivity()).addUserVideo(video, getActivity(), String.valueOf(getPlaylistId()));
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
}