package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector;

import android.content.Context;
import android.util.Log;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item.VideoItem;
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


    private ArrayList<VideoItem> items = new ArrayList<>();

    public ArrayList<VideoItem> getVideoItems(final ArrayList<String> videoIds,final Context ct) {
        try {
            for(String ids : videoIds){

                youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) throws IOException {
                        String packageName = ct.getPackageName();
                        String SHA1 = MSettings.getSHA1(packageName, ct);

                        request.getHeaders().set("X-Android-Package", packageName);
                        request.getHeaders().set("X-Android-Cert", SHA1);
                    }
                }).setApplicationName(ct.getString(R.string.app_name)).build();
                try{
                    myQuery = youtube.videos().list("snippet,contentDetails,statistics");
                    myQuery.setKey(YoutubeConnector.KEY);
                }catch(IOException e){
                    Log.d("YC", "Could not initialize: "+e);
                }

                if (ids != null) {
                    myQuery.setId(ids.toString());
                    VideoListResponse response = myQuery.execute();
                    List<Video> results = response.getItems();
                    for (Video result : results) {
                        try {
                            if (result.getId()!= null) {
                                VideoItem item = new VideoItem();
                                item.setTitle(result.getSnippet().getTitle());
                                item.setThumbnailURL(result.getSnippet().getThumbnails().getMedium().getUrl());
                                if (result.getId() != null) {
                                    item.setChanelTitle(result.getSnippet().getChannelTitle(), false);
                                    item.setViewCount(result.getStatistics().getViewCount().toString(), Byte.parseByte(String.valueOf("0")));
                                    item.setDuration(result.getContentDetails().getDuration());
                                    item.setId(result.getId());
                                    item.setPublishedAt(result.getSnippet().getPublishedAt().toString());
                                }
                                items.add(item);
                            }
                        } catch (final Exception e) {
                        }
                    }
                }
            }
            return items;

        } catch (final IOException e) {
            return null;
        }
    }
}