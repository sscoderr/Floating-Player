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
 * Created by Sabahattin on 2.03.2017.
 */

public class ConnectorForVideoId {
    private YouTube youtube;
    private YouTube.Videos.List query;

    public ConnectorForVideoId(final Context context) {
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                String packageName = context.getPackageName();
                String SHA1 = MSettings.getSHA1(packageName, context);

                request.getHeaders().set("X-Android-Package", packageName);
                request.getHeaders().set("X-Android-Cert", SHA1);
            }
        }).setApplicationName(context.getString(R.string.app_name)).build();
        try {
            query = youtube.videos().list("snippet,contentDetails,statistics");
            query.setKey(YoutubeConnector.KEY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<VideoItem> items = new ArrayList<>();

    public List<VideoItem> getVideoItems(String[][] matrix, int order, boolean isOne) {
        try {
            for (int j = 0; j < 10; j++) {
                if (order < matrix.length)
                    if (matrix[order][j] != null) {
                        query.setId(matrix[order][j]);
                        VideoListResponse response = query.execute();
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
                            e.printStackTrace();
                        }
                    } else break;
                else break;
                if (isOne) break;

            }
            return items;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}