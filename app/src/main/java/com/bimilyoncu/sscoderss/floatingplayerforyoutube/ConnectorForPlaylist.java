package com.bimilyoncu.sscoderss.floatingplayerforyoutube;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.io.BaseEncoding;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sabahattin on 12/14/2016.
 */
public class ConnectorForPlaylist {
    private YouTube youtube;
    private YouTube.PlaylistItems.List query;
    private Context ct;

    public ConnectorForPlaylist(final Context context, String Id,boolean isPlayedVideo) {
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
            Log.d("YC", "Could not initialize: " + e);
        }
    }

    TokenForPlaylist tk = new TokenForPlaylist();
    List<VideoItem> items = new ArrayList<VideoItem>();
    public List<VideoItem> search(boolean first) {
        try {
            if (tk.getNextToken() != null || first == true) {
                if (!first)
                    query.setPageToken(tk.getNextToken());
                PlaylistItemListResponse response = query.execute();
                List<PlaylistItem> results = response.getItems();
                for (PlaylistItem result : results) {
                    try {
                        VideoItem item = new VideoItem();
                        item.setId(result.getContentDetails().getVideoId().toString());
                        item.setTitle(result.getSnippet().getTitle());
                        item.setThumbnailURL(result.getSnippet().getThumbnails().getMedium().getUrl());
                        item.setChanelTitle(result.getSnippet().getChannelTitle(), false);
                        YouTube.Videos.List videoRequest = youtube.videos().list("contentDetails,statistics");
                        videoRequest.setId(result.getContentDetails().getVideoId().toString());
                        videoRequest.setKey(YoutubeConnector.KEY);
                        VideoListResponse responseTwo = videoRequest.execute();
                        List<Video> videosList = responseTwo.getItems();
                        String duration = "";
                        if (videosList != null && videosList.size() > 0) {
                            Video video = videosList.get(0);
                            VideoContentDetails contentDetails = video.getContentDetails();
                            duration = contentDetails.getDuration().toString();
                        }
                        item.setDuration(duration);
                        items.add(item);
                    }
                    catch (Exception e){}

                    }
                tk.setNextToken(response.getNextPageToken());
            }
        } catch (Exception sa) {
            return null;
        }
        return items;
    }
}
