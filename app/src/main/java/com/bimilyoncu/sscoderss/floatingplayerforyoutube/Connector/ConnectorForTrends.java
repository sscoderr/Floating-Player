package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
 * Created by Sabahattin on 21.02.2017.
 */

public class ConnectorForTrends {
    private YouTube youtubeTrend;
    private YouTube.Videos.List query;
    private Context ct;

    public ConnectorForTrends(final Context context, boolean isMusic) {
        youtubeTrend = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                ct = context;
                String packageName = context.getPackageName();
                String SHA1 = MSettings.getSHA1(packageName, context);

                request.getHeaders().set("X-Android-Package", packageName);
                request.getHeaders().set("X-Android-Cert", SHA1);
            }
        }).setApplicationName(context.getString(R.string.app_name)).build();
        try {
            query = youtubeTrend.videos().list("snippet,contentDetails,statistics");
            query.setMaxResults(15L);
            query.setKey(YoutubeConnector.KEY);
            query.setRegionCode(MSettings.countryCode);
            query.setChart("mostPopular");
            if (isMusic)
                query.setVideoCategoryId("10");
        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }
    }

    List<VideoItem> items = new ArrayList<VideoItem>();

    public List<VideoItem> Trends(boolean first, boolean isMusic) {
        try {
            if (MSettings.token.getNextTokenForTrendVideos() != null || first == true) {
                if (!first)
                    if (!isMusic) {
                        query.setPageToken(MSettings.token.getNextTokenForTrendVideos());
                    } else {
                        query.setPageToken(MSettings.token.getNextTokenForTrendMusic());
                    }
                VideoListResponse response = query.execute();
                List<Video> results = response.getItems();
                for (Video result : results) {
                    try {
                        VideoItem item = new VideoItem();
                        item.setTitle(result.getSnippet().getTitle());
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ct);
                        if (preferences.getBoolean("isHighQuality", false))
                            item.setThumbnailURL(result.getSnippet().getThumbnails().getMaxres().getUrl());
                        else
                            item.setThumbnailURL(result.getSnippet().getThumbnails().getMedium().getUrl());
                        if (result.getId() != null) {
                            item.setChanelTitle(result.getSnippet().getChannelTitle(), false);
                            item.setViewCount(result.getStatistics().getViewCount().toString(), Byte.parseByte(String.valueOf("0")));
                            item.setDuration(result.getContentDetails().getDuration());
                            item.setId(result.getId());
                            item.setPublishedAt(result.getSnippet().getPublishedAt().toString());
                            item.setChannelImageURL(MSettings.getChannelImage(youtubeTrend, YoutubeConnector.KEY, result.getSnippet().getChannelId()));
                        }
                        items.add(item);
                    } catch (Exception e) {
                    }
                }
                if (!isMusic) {
                    MSettings.token.setNextTokenForTrendVideos(response.getNextPageToken());
                } else {
                    MSettings.token.setNextTokenForTrendMusic(response.getNextPageToken());
                }
            }
            return items;
        } catch (IOException e) {
            Log.e("Hata", "Sebebi: " + e);
            return null;
        }
    }
}