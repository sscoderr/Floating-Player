package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;


import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.CountryActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.SliderAdapters.FirstPageCountriesSliderAdapter;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.SliderAdapters.FirstPageHorizontalSliderAdapter;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.SliderAdapters.FirstPageMainSliderAdapter;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Connector.ConnectorForVideoId;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.CountryGridItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.FirstPageHorizontalSliderItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;


import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.similarVideosList;

public class TrendMusicFragment extends Fragment {
    private RecyclerView rc;
    private RecyclerView rcRecommented;
    private RecyclerView rcCountries;
    private LinearLayout mostAndLastLinear;
    private LinearLayout recommendationLinear;

    private List<Video> results;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trend_music, container, false);

        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MSettings.adViewTrendMusic = (AdView) v.findViewById(R.id.adViewTrendMusic);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("5C6AACA525A3BCD06EAF02A4E2E59627")
                //.addTestDevice("C351F406A4D495796C53E9B2FD34AC62")
                //.addTestDevice("D840C07DDBAA5E0897B010411FABE6AC")
                //.addTestDevice("778ADE18482DD7E44193371217202427")
                //.addTestDevice("6AFA29CB9314195950E590C9BEACC344")
                //.addTestDevice("0CEA9CA5F2DAED70F0678D8F2D8669A3")
                .build();
        MSettings.adViewTrendMusic.loadAd(adRequest);

        SliderView sliderView = v.findViewById(R.id.imageSlider);

        FirstPageMainSliderAdapter adapter = new FirstPageMainSliderAdapter(getContext());
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(12); //set scroll delay in seconds :
        sliderView.startAutoCycle();
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManagerForRec= new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rc = (RecyclerView)v.findViewById(R.id.recycle_last_and_most_listening);
        rcRecommented = (RecyclerView)v.findViewById(R.id.recycle_recommendation);
        rcCountries = (RecyclerView)v.findViewById(R.id.recycle_country);
        mostAndLastLinear = (LinearLayout)v.findViewById(R.id.most_last_rel);
        recommendationLinear = (LinearLayout)v.findViewById(R.id.recommendation_rel);
        rc.setLayoutManager(layoutManager);
        rcRecommented.setLayoutManager(layoutManagerForRec);
        loadMostAndLastWatched();
        loadCountriesForSlider();
        loadRecommendation();
        RelativeLayout relSeeAll=v.findViewById(R.id.rel_see_all);
        relSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CountryActivity.class);
                startActivity(intent);
            }
        });
        RelativeLayout relRefreshRec=v.findViewById(R.id.rel_refresh_rec);
        relRefreshRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadRecommendation();
                MSettings.LoadSixTapAds();
            }
        });
        return v;
    }
    private List<FirstPageHorizontalSliderItem> items = new ArrayList<>();
    private void loadMostAndLastWatched(){
        new Thread() {
            public void run() {
                items = new ArrayList<>();
                DatabaseForPlaylists db = new DatabaseForPlaylists(getActivity());
                SQLiteDatabase readableDatabase = db.getReadableDatabase();
                Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_history order by viewCount desc,id desc LIMIT 15", null);
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
                                        FirstPageHorizontalSliderItem mItem = new FirstPageHorizontalSliderItem();
                                        mItem.setVidTitle(item.getTitle());
                                        mItem.setVidthumbnialUrl(item.getThumbnailURL());
                                        mItem.setId(item.getId());
                                        items.add(mItem);
                                    }
                                    dbTwo.update("tbl_history", values, "videoId=?", new String[]{currentVId});
                                    dbTwo.close();
                                } else {
                                    FirstPageHorizontalSliderItem item = new FirstPageHorizontalSliderItem();
                                    item.setVidTitle(cur.getString(cur.getColumnIndex("videoTitle")));
                                    item.setVidthumbnialUrl(cur.getString(cur.getColumnIndex("videoImgUrl")));
                                    item.setChannelTitle(cur.getString(cur.getColumnIndex("channelTitle")));
                                    item.setId(cur.getString(cur.getColumnIndex("videoId")));
                                    items.add(item);
                                }

                            } catch (Exception e) {
                            }
                        } while (cur.moveToNext());
                    }
                }
                db.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FirstPageHorizontalSliderAdapter rcAdapter = new FirstPageHorizontalSliderAdapter(getActivity(), items);
                        rc.setAdapter(rcAdapter);
                        if (rcAdapter.getItemCount()<=0)
                            mostAndLastLinear.setVisibility(View.GONE);
                    }});
            }
        }.start();
    }
    private List<FirstPageHorizontalSliderItem> recommendationItems = new ArrayList<>();
    private void loadRecommendation(){
        new Thread() {
            public void run() {
                try {
                    recommendationItems = new ArrayList<>();
                    DatabaseForPlaylists db = new DatabaseForPlaylists(getActivity());
                    SQLiteDatabase readableDatabase = db.getReadableDatabase();
                    Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_history order by RANDOM() LIMIT 1", null);
                    if (cur != null) {
                        if (cur.moveToFirst()) {
                            do {
                                Cursor curForChectVidId = readableDatabase.rawQuery("SELECT * FROM tbl_recommented where vidIdForSimilarVids=?", new String[]{cur.getString(cur.getColumnIndex("videoId"))});
                                try {
                                    if (curForChectVidId != null && curForChectVidId.moveToNext()) {
                                        do {
                                            FirstPageHorizontalSliderItem item = new FirstPageHorizontalSliderItem();
                                            item.setVidTitle(curForChectVidId.getString(curForChectVidId.getColumnIndex("videoTitle")));
                                            item.setVidthumbnialUrl(curForChectVidId.getString(curForChectVidId.getColumnIndex("videoImgUrl")));
                                            item.setId(curForChectVidId.getString(curForChectVidId.getColumnIndex("videoId")));
                                            item.setChannelTitle(curForChectVidId.getString(curForChectVidId.getColumnIndex("channelTitle")));
                                            recommendationItems.add(item);
                                        } while (curForChectVidId.moveToNext());
                                    } else {
                                        String currentVId = cur.getString(cur.getColumnIndex("videoId"));
                                        List<VideoItem> similarVideos = loadSimilar(currentVId);
                                        DatabaseForPlaylists database = new DatabaseForPlaylists(getActivity());
                                        for (VideoItem item : similarVideos) {
                                            database.addVideoRecommented(currentVId,item.getId(),item.getTitle(),item.getChannelTitle(),item.getThumbnailURL(),getContext());
                                            FirstPageHorizontalSliderItem mItem = new FirstPageHorizontalSliderItem();
                                            mItem.setVidTitle(item.getTitle());
                                            mItem.setVidthumbnialUrl(item.getThumbnailURL());
                                            mItem.setChannelTitle(item.getChannelTitle());
                                            mItem.setId(item.getId());
                                            recommendationItems.add(mItem);
                                        }
                                    }

                                } catch (Exception e) {
                                }
                            } while (cur.moveToNext());
                        }
                    }
                    db.close();
                }catch (Exception e){}
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        FirstPageHorizontalSliderAdapter rcAdapter = new FirstPageHorizontalSliderAdapter(getActivity(), recommendationItems);
                        rcRecommented.setAdapter(rcAdapter);
                        if (rcAdapter.getItemCount()<=0)
                            recommendationLinear.setVisibility(View.GONE);
                    }});

            }
        }.start();
    }
    private JsonObject getResAsJson(String response) {
        return new JsonParser().parse(response).getAsJsonObject();
    }
    private List<VideoItem> loadSimilar(String vId){
        List<VideoItem> list = new ArrayList<>();
        try {
            String info;
            JsonObject obj = null;
            Connection.Response response= Jsoup.connect(MSettings.youtubeWatchURL+vId)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .timeout(20000)
                    .followRedirects(true)
                    .execute();
            Document dc = response.parse();

            Elements element = dc.getElementsByTag("script");
            for (int i = 0; i < element.size(); i++){
                Element e = element.get(i);
                if (e.toString().contains("ytInitialData")) {
                    info = e.toString();
                    info = info.substring(info.indexOf("ytInitialData")+17,info.indexOf("ytInitialPlayerResponse")-14).trim();
                    obj = getResAsJson(info);
                    break;
                }
            }
            JsonObject mjsonobj1 = (JsonObject) obj.get("contents");
            JsonObject mjsonobj2 = (JsonObject) mjsonobj1.get("twoColumnWatchNextResults");
            JsonObject mjsonobj3 = (JsonObject) mjsonobj2.get("secondaryResults");
            JsonObject mjsonobj4 = (JsonObject) mjsonobj3.get("secondaryResults");
            JsonArray jsonArray = (JsonArray) mjsonobj4.get("results");
            for (int i = 0; i<jsonArray.size(); i++){
                JsonObject jsonobj_1 = (JsonObject) jsonArray.get(i);
                if(jsonobj_1.get("compactVideoRenderer")!=null) {
                    VideoItem item = new VideoItem();
                    JsonObject jsonobj_2 = (JsonObject) jsonobj_1.get("compactVideoRenderer");
                    item.setId(jsonobj_2.get("videoId").getAsString());
                    JsonObject jsonobj_3 = (JsonObject) jsonobj_2.get("title");
                    item.setTitle(jsonobj_3.get("simpleText").getAsString());
                    JsonObject jsonobj_4 = (JsonObject) jsonobj_2.get("thumbnail");
                    JsonArray thumbarr = (JsonArray) jsonobj_4.get("thumbnails");
                    JsonObject jsonobj5 = (JsonObject) thumbarr.get(1);
                    item.setThumbnailURL(jsonobj5.get("url").getAsString());
                    JsonObject jsonobj6 = (JsonObject) jsonobj_2.get("lengthText");
                    item.setDurationForNewApi(jsonobj6.get("simpleText").getAsString());
                    JsonObject jsonobj7 = (JsonObject) jsonobj_2.get("viewCountText");
                    item.setViewsForNewApi(jsonobj7.get("simpleText").getAsString());
                    JsonObject jsonobj8 = (JsonObject) jsonobj_2.get("longBylineText");
                    JsonArray  channelNameArr = (JsonArray) jsonobj8.get("runs");
                    JsonObject jsonobj9 = (JsonObject) channelNameArr.get(0);
                    Log.e("asdfsd:"+jsonobj9.get("text").getAsString(),"asd");
                    item.setChanelTitle(jsonobj9.get("text").getAsString(),false);
                    list.add(item);
                }
            }
        }  catch (Exception e) {
        }
        return list;
    }
    private List<CountryGridItem> itemsCountries = new ArrayList<>();
    private void loadCountriesForSlider(){
        new Thread() {
            public void run() {
                itemsCountries = new ArrayList<>();
                DatabaseForPlaylists db = new DatabaseForPlaylists(getActivity());
                SQLiteDatabase readableDatabase = db.getReadableDatabase();
                try{
                    TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(getContext().TELEPHONY_SERVICE);
                    Cursor curCurrentCountry = readableDatabase.rawQuery("SELECT * FROM tbl_countries WHERE countryCode='"+tm.getNetworkCountryIso().toUpperCase()+"'",null);
                    if (curCurrentCountry != null) {
                        if (curCurrentCountry.moveToFirst()) {
                            CountryGridItem mItem = new CountryGridItem();
                            mItem.setCounrtyFlagUrl(curCurrentCountry.getString(curCurrentCountry.getColumnIndex("flagUrl")));
                            mItem.setCountryName(curCurrentCountry.getString(curCurrentCountry.getColumnIndex("countryName")));
                            mItem.setCountryCode(curCurrentCountry.getString(curCurrentCountry.getColumnIndex("countryCode")));
                            itemsCountries.add(mItem);
                        }
                    }
                }catch (Exception e){

                }
                Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_countries order by RANDOM() LIMIT 11", null);
                if (cur != null) {
                    if (cur.moveToFirst()) {
                        do {
                            try {
                                CountryGridItem mItem = new CountryGridItem();
                                mItem.setCounrtyFlagUrl(cur.getString(cur.getColumnIndex("flagUrl")));
                                mItem.setCountryName(cur.getString(cur.getColumnIndex("countryName")));
                                mItem.setCountryCode(cur.getString(cur.getColumnIndex("countryCode")));
                                itemsCountries.add(mItem);
                            } catch (Exception e) {
                            }
                        } while (cur.moveToNext());
                    }
                }
                db.close();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{//Cokme Ihtimali Olan Yer
                            LinearLayoutManager layoutManager= new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                            rcCountries.setLayoutManager(layoutManager);
                            FirstPageCountriesSliderAdapter rcAdapter = new FirstPageCountriesSliderAdapter(getActivity(), itemsCountries);
                            rcCountries.setAdapter(rcAdapter);
                        }catch (Exception e){

                        }
                    }});
            }
        }.start();
    }
}
