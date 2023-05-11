package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Connector;

import android.content.Context;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
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
                    myQuery = youtube.videos().list("snippet");
                    myQuery.setKey(YoutubeConnector.KEY);
                }catch(IOException e){
                    e.printStackTrace();
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
                                    item.setId(result.getId());
                                }
                                items.add(item);
                            }
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return items;

        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}