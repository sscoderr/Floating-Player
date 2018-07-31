package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Connector;

import android.content.Context;
import android.util.Log;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.ChannelVideoList;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item.VideoItem;
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

public class YoutubeConnector {
    private YouTube youtube;
    private YouTube.Search.List query;
    public static String KEY = "AIzaSyB1kAAfsJx_bc1lchJ64Y5VQ1jQckI7MlI";


    public YoutubeConnector(final Context context, String sortBy, String contentType, boolean firstForFilter, String channelId, boolean isForFloat, boolean isForChannelPlayVideo) {
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
                query.setKey(KEY);
                if (isForChannelPlayVideo)
                    query.setMaxResults(50L);
                else
                    query.setMaxResults(12L);
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
                query.setKey(KEY);
                query.setMaxResults(12L);
                query.setRelatedToVideoId(sortBy);
                query.setType("video");
            }
            query.setFields("items(id/videoId,id/channelId,id/playlistId,snippet/title,snippet/publishedAt,snippet/channelTitle,snippet/thumbnails/medium/url),nextPageToken");
        } catch (IOException e) {
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
                        VideoItem item = new VideoItem();
                        item.setTitle(result.getSnippet().getTitle());

                        item.setThumbnailURL(result.getSnippet().getThumbnails().getMedium().getUrl());
                        if (result.getId().getVideoId() != null) {
                            item.setChanelTitle(result.getSnippet().getChannelTitle(), false);
                            ChannelVideoList.channelName = result.getSnippet().getChannelTitle();
                            YouTube.Videos.List videoRequest = youtube.videos().list("contentDetails,statistics");
                            videoRequest.setId(result.getId().getVideoId().toString());
                            videoRequest.setKey(KEY);
                            VideoListResponse responseTwo = videoRequest.execute();
                            List<Video> videosList = responseTwo.getItems();
                            String viewCount = "", duration = "";
                            if (videosList != null && videosList.size() > 0) {
                                Video video = videosList.get(0);
                                VideoStatistics statistics = video.getStatistics();
                                viewCount = statistics.getViewCount().toString();
                                VideoContentDetails contentDetails = video.getContentDetails();
                                duration = contentDetails.getDuration().toString();
                            }
                            item.setViewCount(viewCount, Byte.parseByte(String.valueOf("0")));
                            item.setDuration(duration);
                            item.setId(result.getId().getVideoId());
                            item.setPublishedAt(result.getSnippet().getPublishedAt().toString());
//                            item.setChannelImageURL(MSettings.getChannelImage(youtube, KEY, result.getSnippet().getChannelId()));
                        } else if (result.getId().getChannelId() != null) {
                            try {
                                YouTube.Channels.List channelRequest = youtube.channels().list("statistics");
                                channelRequest.setId(result.getId().getChannelId());
                                channelRequest.setKey(KEY);
                                ChannelListResponse channelResult = channelRequest.execute();
                                List<Channel> channelsList = channelResult.getItems();
                                if (channelsList != null) {
                                    String subscriberCount, videoCount;
                                    Channel ch = channelsList.get(0);
                                    ChannelStatistics cs = ch.getStatistics();
                                    subscriberCount = cs.getSubscriberCount().toString();
                                    videoCount = cs.getVideoCount().toString();
                                    item.setViewCount(subscriberCount, Byte.parseByte(String.valueOf("1")));
                                    item.setChanelTitle(videoCount, true);
                                    item.setId(result.getId().getChannelId());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (result.getId().getPlaylistId() != null) {
                            item.setChanelTitle(result.getSnippet().getChannelTitle(), false);
                            try {
                                YouTube.Playlists.List playlistRequest = youtube.playlists().list("contentDetails");
                                playlistRequest.setId(result.getId().getPlaylistId());
                                playlistRequest.setKey(KEY);
                                PlaylistListResponse channelResult = playlistRequest.execute();
                                List<Playlist> playlistsList = channelResult.getItems();
                                if (playlistsList != null) {
                                    String videoCount;
                                    Playlist ch = playlistsList.get(0);
                                    PlaylistContentDetails cs = ch.getContentDetails();
                                    videoCount = cs.getItemCount().toString();
                                    item.setViewCount(videoCount, Byte.parseByte(String.valueOf("2")));
                                    item.setId(result.getId().getPlaylistId());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        items.add(item);
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