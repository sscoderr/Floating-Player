package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AbsListView.OnScrollListener;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.AdapterSearchVideo;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.CustomAdapterForTrends;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Connector.ConnectorForTrends;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.NetControl;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.MainActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.DestroyOrShowBanner;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.playedPoss;


public class TrendVideoFragment extends Fragment implements OnScrollListener  {
    public Handler mHandler;
    private boolean isLoading=false;
    private ProgressBar myPg;
    private View myView;
    private CustomAdapterForTrends adapterForSmallScreen;
    private AdapterSearchVideo adapterForLargeScreen;
    private List<VideoItem> searchResults;
    private Handler handler;
    private ListView listView;
    private int sizeOfMoreData=1;
    private TextView txtInfo;

    private NetControl netControl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_trend_video,container,false);
        LayoutInflater li = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        netControl = new NetControl(getActivity());

        MSettings.adViewTrendVideo = (AdView)v.findViewById(R.id.adViewTrendVideo);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("5C6AACA525A3BCD06EAF02A4E2E59627")
                //.addTestDevice("C351F406A4D495796C53E9B2FD34AC62")
                //.addTestDevice("D840C07DDBAA5E0897B010411FABE6AC")
                //.addTestDevice("778ADE18482DD7E44193371217202427")
                //.addTestDevice("6AFA29CB9314195950E590C9BEACC344")
                //.addTestDevice("0CEA9CA5F2DAED70F0678D8F2D8669A3")
        .build();
        MSettings.adViewTrendVideo.loadAd(adRequest);
        myView = li.inflate(R.layout.loading_result, null);
        myPg=(ProgressBar)v.findViewById(R.id.myPBForFav);
        listView = (ListView) v.findViewById(R.id.trend_video_list);
        txtInfo = (TextView) v.findViewById(R.id.textview_trend_video_info);
        txtInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebsite("https://play.google.com/store/apps/details?id=com.bimilyoncu.sscoderss.floatingplayerforyoutubev3", MSettings.activeActivity);
            }
        });
        mHandler = new MyHandler();
        searchResults = new ArrayList<>();
        addClickListener();
        handler = new Handler();
        listView.setOnScrollListener(this);
        myPg.setVisibility(View.VISIBLE);
        //if (MSettings.isHaveNetworkAccesQuickly(getActivity()))
        loadStartData(true);
        /*else {
            Runnable runnable = new Runnable()
            {
                @Override
                public void run()
                {
                    while (!MSettings.isHaveNetworkAccesQuickly(getActivity())){
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            loadStartData(true);
                        }
                    });
                }
            };
            new Thread(runnable).start();
        }*/
        return  v;
    }
    private void openWebsite(String url, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
    private void addClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, final int pos, long id) {
                DestroyOrShowBanner(MSettings.adViewTrendMusic,true);
                DestroyOrShowBanner(MSettings.adViewTrendVideo,true);
                DestroyOrShowBanner(MSettings.adViewMyData,true);
                DestroyOrShowBanner(MSettings.adViewPlaylist,true);
                MSettings.CounterForSimilarVideos=1;
                playedPoss=new ArrayList<Integer>();
                MSettings.currentVItem = searchResults.get(pos);
                MSettings.activeVideo = searchResults.get(pos);
                MSettings.activeActivity = getActivity();
                MainActivity mainActivity = new MainActivity();
                mainActivity.getSimilarVideos(String.valueOf(searchResults.get(pos).getId()),false,false,false,new String[]{});
                MSettings.IsRetry = false;
                MSettings.videoFinishStopVideoClicked = true;
                MSettings.LoadVideo();
                MSettings.LoadSixTapAds();
            }
        });
    }

    public boolean isSmallScreen(){
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
        if (Configuration.SCREENLAYOUT_SIZE_LARGE==screenSize||getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
            return false;
        else return true;
    }

    private void loadStartData(final boolean first){
        new Thread(){
            public void run(){

                /*List<VideoItem> items=new ArrayList<VideoItem>();
                try{
                    VideoItem item = new VideoItem();
                    item.setTitle("dsfsd");
                    item.setThumbnailURL("https://i.ytimg.com/vi/ZxT0576FTsk/mqdefault.jpg");
                    item.setChanelTitle("dsf",false);
                    item.setViewCount("55",Byte.parseByte(String.valueOf("0")));
                    item.setDuration("2:15");
                    item.setId("ZxT0576FTsk");
                    item.setPublishedAt("2011-10-20T19:59:56.000Z");
                    items.add(item);
                }catch (Exception e){
                e.printStackTrace();}
                Results=items;*/

                ConnectorForTrends connectorForTrends = new ConnectorForTrends(getContext(),false);
                searchResults=connectorForTrends.Trends(first,false);
                handler.post(new Runnable(){
                    public void run() {
                        try {
                            if (isSmallScreen()) {
                                adapterForSmallScreen = new CustomAdapterForTrends(getActivity(), searchResults);
                                listView.setAdapter(adapterForSmallScreen);
                            } else {
                                adapterForLargeScreen = new AdapterSearchVideo(getActivity(), searchResults, "");
                                listView.setAdapter(adapterForLargeScreen);
                            }
                            myPg.setVisibility(View.INVISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (!netControl.isOnline()) {
                                try {
                                    Toast.makeText(getActivity(), getActivity().getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                                } catch (Exception s) {
                                    s.printStackTrace();
                                }
                            }
                        }
                    }
                });
            }
        }.start();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        if(absListView.getLastVisiblePosition() == i2-1 && listView.getCount() >=5 && isLoading == false&&sizeOfMoreData!=0) {
            isLoading = true;
            Thread thread = new TrendVideoFragment.ThreadGetMoreData();
            thread.start();
        }
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    listView.addFooterView(myView);
                    break;
                case 1:
                    if (isSmallScreen())
                        adapterForSmallScreen.addListItemToAdapter((ArrayList<VideoItem>) msg.obj);
                    else  adapterForLargeScreen.addListItemToAdapter((ArrayList<VideoItem>) msg.obj);
                    listView.removeFooterView(myView);
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
                ConnectorForTrends
                        connectorForTrends = new ConnectorForTrends(getContext(), false);
                List<VideoItem> list = connectorForTrends.Trends(false, false);
                sizeOfMoreData = list.size();
                ArrayList<VideoItem> lstResult = (ArrayList<VideoItem>) list;
                Message msg = mHandler.obtainMessage(1, lstResult);
                mHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                if (!netControl.isOnline())
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                        }
                    });
            }
        }
    }
}
