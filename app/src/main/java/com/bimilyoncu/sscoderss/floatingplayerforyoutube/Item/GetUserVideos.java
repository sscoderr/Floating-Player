package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item;

import android.content.Context;
import android.util.Log;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.Fragment.MyDateFragment;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
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
 * Created by Sabahattin on 12.03.2017.
 */

public class GetUserVideos {
    private YouTube youtube;
    private YouTube.PlaylistItems.List query;
    public GetUserVideos(final Context context, final GoogleAccountCredential credential, String Id){
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName(context.getString(R.string.app_name)).setHttpRequestInitializer(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) {
                credential.initialize(httpRequest);
                String packageName = context.getPackageName();
                String SHA1 = MSettings.getSHA1(packageName,context);

                httpRequest.getHeaders().set("X-Android-Package", packageName);
                httpRequest.getHeaders().set("X-Android-Cert",SHA1);
            }
        }).build();
        try {
            query = youtube.playlistItems().list("contentDetails");
            query.setMaxResults(50L);
            query.setKey(MyDateFragment.OAUTH_KEY);
            query.setPlaylistId(Id);
            query.setFields("items(contentDetails/videoId,contentDetails/videoPublishedAt),nextPageToken");
        } catch (IOException e) {
            Log.d("YC", "Could not initialize: " + e);
        }
    }

    List<String> items = new ArrayList<>();
    public List<String> loadUserVideos() {
        String nextToken = "";
        try {
            do {
                query.setPageToken(nextToken);
                PlaylistItemListResponse response = query.execute();
                List<PlaylistItem> results = response.getItems();
                for (PlaylistItem playlists:results){
                    if (playlists.getContentDetails().size()==2)
                        items.add(playlists.getContentDetails().getVideoId());
                }
                nextToken = response.getNextPageToken();
            }while (nextToken!=null);
        } catch (Exception sa) {
            return null;
        }
        return items;
    }

}
