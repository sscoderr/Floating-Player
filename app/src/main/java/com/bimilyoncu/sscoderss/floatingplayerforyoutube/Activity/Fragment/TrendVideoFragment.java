package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.Fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;


import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.CustomAdapter;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.CustomAdapterForTrends;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector.ConnectorForTrends;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.NetControl;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.MainActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings.playedPoss;


public class TrendVideoFragment extends Fragment implements OnScrollListener  {
    public Handler mHandler;
    private boolean isLoading=false;
    private ProgressBar myPg;
    private View myView;
    private CustomAdapterForTrends adapterForSmallScreen;
    private CustomAdapter adapterForLargeScreen;
    private List<VideoItem> searchResults;
    private Handler handler;
    private ListView listView;
    private int sizeOfMoreData=1;

    private NetControl netControl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_trend_video,container,false);
        LayoutInflater li = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        netControl = new NetControl(getActivity());

        AdView mAdView = (AdView)v.findViewById(R.id.adViewTrendVideo);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("6EE0EC7A08848B41A3A8B3C52624F39A").addTestDevice("D840C07DDBAA5E0897B010411FABE6AC").addTestDevice("778ADE18482DD7E44193371217202427").build();
        mAdView.loadAd(adRequest);

        myView = li.inflate(R.layout.loading_result, null);
        myPg=(ProgressBar)v.findViewById(R.id.myPBForFav);
        listView = (ListView) v.findViewById(R.id.trend_video_list);

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
        ((MainActivity)getActivity()).setFragmentRefreshListenerForVideo(new MainActivity.FragmentRefreshListenerForVideo() {
            @Override
            public void onRefresh() {
                loadStartData(true);
            }
        });
        return  v;
    }

    private void addClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                MSettings.CounterForSimilarVideos=2;
                playedPoss=new ArrayList<Integer>();
                MSettings.currentVideoId = searchResults.get(pos).getId();
                MSettings.playedVideoPos=pos;
                MSettings.setVideoTitle(searchResults.get(pos).getTitle());
                MSettings.activeActivity = getActivity();
                MainActivity mainActivity = new MainActivity();
                mainActivity.getSimilarVideos(String.valueOf(searchResults.get(pos).getId()),false,false,false,new String[]{});
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
                }catch (Exception e){}
                searchResults=items;*/

                ConnectorForTrends connectorForTrends = new ConnectorForTrends(getContext(),false);
                searchResults=connectorForTrends.Trends(first,false);
                handler.post(new Runnable(){
                    public void run() {
                        try {
                            if (isSmallScreen()) {
                                adapterForSmallScreen = new CustomAdapterForTrends(getActivity(), searchResults);
                                listView.setAdapter(adapterForSmallScreen);
                            } else {
                                adapterForLargeScreen = new CustomAdapter(getActivity(), searchResults, "");
                                listView.setAdapter(adapterForLargeScreen);
                            }
                            myPg.setVisibility(View.INVISIBLE);
                        } catch (Exception e) {
                            if (!netControl.isOnline())
                                Toast.makeText(getActivity(), getActivity().getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
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
