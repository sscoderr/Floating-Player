package com.bimilyoncu.sscoderss.floatingplayerforyoutube;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
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

/*import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;*/


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.api.services.youtube.model.Video;

import java.util.ArrayList;
import java.util.List;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutube.MSettings.playedPoss;


public class TrendMusicFragment extends Fragment implements OnScrollListener  {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trend_music, container, false);
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        AdView mAdView = (AdView)v.findViewById(R.id.adViewTrendMusic);
        AdRequest adRequest = new AdRequest.Builder().build();/*778ADE18482DD7E44193371217202427 Device Id*/
        /*.addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("6EE0EC7A08848B41A3A8B3C52624F39A").addTestDevice("D840C07DDBAA5E0897B010411FABE6AC").addTestDevice("778ADE18482DD7E44193371217202427")*/
        mAdView.loadAd(adRequest);

        myView = li.inflate(R.layout.loading_result, null);
        myPg = (ProgressBar) v.findViewById(R.id.myPBForFav);
        listView = (ListView) v.findViewById(R.id.trend_music_list);
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
        ((MainActivity)getActivity()).setFragmentRefreshListener(new MainActivity.FragmentRefreshListenerForMusic() {
            @Override
            public void onRefresh() {
                loadStartData(true);
            }
        });

        return v;
    }

    private void addClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, final int pos, long id) {
                MSettings.CounterForSimilarVideos=2;
                playedPoss=new ArrayList<>();
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

    private void loadStartData(final boolean first){
        new Thread(){
            public void run(){
                ConnectorForTrends
                        connectorForTrends = new ConnectorForTrends(getContext(),true);
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
                            if (MSettings.isHaveNetworkAccesQuickly(getActivity()))
                                Toast.makeText(getActivity(), getActivity().getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }.start();
    }
    public boolean isSmallScreen(){
        int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
            if (Configuration.SCREENLAYOUT_SIZE_LARGE==screenSize||getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE)
                return false;
            else return true;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        if(absListView.getLastVisiblePosition() == i2-1 && listView.getCount() >=5 && isLoading == false&&sizeOfMoreData!=0) {
            //if (MSettings.isHaveNetworkAccesQuickly(getActivity())) {
                isLoading = true;
                Thread thread = new ThreadGetMoreData();
                thread.start();
            /*}else {
                Runnable runnable = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        while (!MSettings.isHaveNetworkAccesQuickly(getActivity())){
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                isLoading = true;
                                Thread thread = new ThreadGetMoreData();
                                thread.start();
                            }
                        });
                    }
                };
                new Thread(runnable).start();
            }*/
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
                    else
                        adapterForLargeScreen.addListItemToAdapter((ArrayList<VideoItem>) msg.obj);
                    listView.removeFooterView(myView);
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
                ConnectorForTrends
                        connectorForTrends = new ConnectorForTrends(getContext(), true);
                List<VideoItem> list = connectorForTrends.Trends(false, false);
                sizeOfMoreData = list.size();
                ArrayList<VideoItem> lstResult = (ArrayList<VideoItem>) list;
                Message msg = mHandler.obtainMessage(1, lstResult);
                mHandler.sendMessage(msg);
            } catch (Exception e) {
                if (MSettings.isHaveNetworkAccesQuickly(getActivity()))
                    Toast.makeText(getActivity(), getActivity().getString(R.string.internetConnectionMessage), Toast.LENGTH_LONG).show();
            }
        }
    }
}
