package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector;

import android.content.Context;
import android.util.Log;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.ChannelVideoList;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.ChannelStatistics;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistContentDetails;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoStatistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ServiceSearchConnector {
    private YouTube youtube;
    private YouTube.Search.List query;

    public ServiceSearchConnector(final Context context, String sortBy, String contentType, boolean firstForFilter, String channelId, boolean isForFloat, boolean isForChannelPlayVideo) {
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
            if (!isForFloat) {
                query = youtube.search().list("id,snippet");
                query.setKey(YoutubeConnector.KEY);
                if (isForChannelPlayVideo)
                    query.setMaxResults(50L);
                else
                    query.setMaxResults(20L);
                if (!channelId.equals(""))
                    query.setChannelId(channelId);
                if (contentType.equals("All"))
                    query.setType("video/channel/playlist");
                else if (!contentType.equals("All") && firstForFilter)
                    query.setType(contentType);
                if (!sortBy.equals("relevance") && firstForFilter)
                    query.setOrder(sortBy);
            } else {
                query = youtube.search().list("id,snippet");
                query.setKey(YoutubeConnector.KEY);
                query.setMaxResults(20L);
                query.setRelatedToVideoId(sortBy);
                query.setType("video");
            }
            query.setFields("items(id/videoId,id/channelId,id/playlistId,snippet/title,snippet/publishedAt,snippet/channelTitle,snippet/thumbnails/medium/url),nextPageToken");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    List<VideoItem> items = new ArrayList<>();

    public List<VideoItem> search(String keywords, boolean first, boolean isChannel, boolean isSimilarVideos) {
        if (!isChannel)
            query.setQ(keywords);
        try {
            if ((!isChannel && (MSettings.token.getNextTokenSearch() != null || first == true)) || (isChannel && (MSettings.token.getNextTokenForChannel() != null || first == true)) || isSimilarVideos) {
                if (!first && isSimilarVideos) {
                    query.setPageToken(MSettings.token.getNextTokenForSimilarVideos());
                } else if (!first && !isChannel) {
                    query.setPageToken(MSettings.token.getNextTokenSearch());
                } else if (!first && isChannel) {
                    query.setPageToken(MSettings.token.getNextTokenForChannel());
                }
                Log.e("Token2: ", String.valueOf(MSettings.token.getNextTokenSearch()));
                SearchListResponse response = query.execute();
                List<SearchResult> results = response.getItems();
                for (SearchResult result : results) {
                    try {
                        if (result.getId().getVideoId() != null) {
                            VideoItem item = new VideoItem();
                            item.setTitle(result.getSnippet().getTitle());
                            item.setThumbnailURL(result.getSnippet().getThumbnails().getMedium().getUrl());
                            item.setId(result.getId().getVideoId());

                            items.add(item);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (isSimilarVideos) {
                    MSettings.token.setNextTokenForSimilarVideos(response.getNextPageToken());
                } else if (!isChannel) {
                    MSettings.token.setNextTokenSearch(response.getNextPageToken());
                } else if (isChannel) {
                    MSettings.token.setNextTokenForChannel(response.getNextPageToken());
                }
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