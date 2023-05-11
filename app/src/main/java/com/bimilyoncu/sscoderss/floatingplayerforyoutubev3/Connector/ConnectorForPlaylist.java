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
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabahattin on 12/14/2016.
 */
public class ConnectorForPlaylist {
    private YouTube youtube;
    private YouTube.PlaylistItems.List query;
    private Context ct;

    public ConnectorForPlaylist(final Context context, String Id, boolean isPlayedVideo) {
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
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
            query = youtube.playlistItems().list("id,contentDetails,snippet");
            if (isPlayedVideo)
                query.setMaxResults(50L);
            else
                query.setMaxResults(12L);
            query.setKey(YoutubeConnector.KEY);
            query.setPlaylistId(Id);
            query.setFields("items(contentDetails/videoId,snippet/title,snippet/channelTitle,snippet/publishedAt,snippet/thumbnails/medium/url),nextPageToken,pageInfo");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<VideoItem> items = new ArrayList<VideoItem>();

    public List<VideoItem> search(boolean first) {
        try {
            if (MSettings.token.getNextTokenForPlayList() != null || first == true) {
                if (!first)
                    query.setPageToken(MSettings.token.getNextTokenForPlayList());
                PlaylistItemListResponse response = query.execute();
                List<PlaylistItem> results = response.getItems();
                for (PlaylistItem result : results) {
                    try {
                        VideoItem item = new VideoItem();
                        item.setId(result.getContentDetails().getVideoId().toString());
                        item.setTitle(result.getSnippet().getTitle());
                        item.setThumbnailURL(result.getSnippet().getThumbnails().getMedium().getUrl());
                        item.setChanelTitle(result.getSnippet().getChannelTitle(), false);
                        items.add(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                MSettings.token.setNextTokenForPlayList(response.getNextPageToken());
            }
        } catch (Exception sa) {
            sa.printStackTrace();
            return null;
        }
        return items;
    }
}
