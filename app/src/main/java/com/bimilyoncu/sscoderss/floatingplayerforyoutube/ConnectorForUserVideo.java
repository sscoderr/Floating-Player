package com.bimilyoncu.sscoderss.floatingplayerforyoutube;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sabahattin on 14.03.2017.
 */


public class ConnectorForUserVideo {

    private YouTube youtube;
    private YouTube.Videos.List myQuery;
    private Context ct;


    public ConnectorForUserVideo(final Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                ct=context;
                String packageName = context.getPackageName();
                String SHA1 = MSettings.getSHA1(packageName,context);

                request.getHeaders().set("X-Android-Package", packageName);
                request.getHeaders().set("X-Android-Cert",SHA1);
            }
        }).setApplicationName(context.getString(R.string.app_name)).build();
        try{
            myQuery = youtube.videos().list("snippet,contentDetails,statistics");
            myQuery.setKey(YoutubeConnector.KEY);
        }catch(IOException e){
            Log.d("YC", "Could not initialize: "+e);
        }
    }
    private ArrayList<VideoItem> items=new ArrayList<>();
    public ArrayList<VideoItem> getVideoItems(String[] videoIds){
        try{
            for (int j=0;j<videoIds.length;j++)
            {
                if (videoIds[j]!=null) {
                    myQuery.setId(videoIds[j].toString());
                    VideoListResponse response = myQuery.execute();
                    List<Video> results = response.getItems();
                    try {
                        VideoItem item = new VideoItem();
                        item.setTitle(results.get(0).getSnippet().getTitle());
                        item.setThumbnailURL(results.get(0).getSnippet().getThumbnails().getMedium().getUrl());
                        if (results.get(0).getId() != null) {
                            item.setChanelTitle(results.get(0).getSnippet().getChannelTitle(), false);
                            item.setViewCount(results.get(0).getStatistics().getViewCount().toString(), Byte.parseByte(String.valueOf("0")));
                            item.setDuration(results.get(0).getContentDetails().getDuration());
                            item.setId(results.get(0).getId());
                            item.setPublishedAt(results.get(0).getSnippet().getPublishedAt().toString());
                        }
                        items.add(item);
                    } catch (Exception e) {
                    }
                }
                else break;
            }
            return items;
        }catch(IOException e){
            return null;
        }
    }
}
