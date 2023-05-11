package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Connector;

import android.content.Context;
import android.content.SharedPreferences;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.ChannelVideoList;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
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

import static android.content.Context.MODE_PRIVATE;

public class YoutubeConnector {
    private YouTube youtube;
    private YouTube.Search.List query;
    public static String KEY = "AIzaSyApwUEthNBFN8nbT2Fpjm02ud0fgk7LPJA";
    private String contentType;
    private Context ct;
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
        this.contentType=contentType;
        this.ct=context;
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
                    query.setType("video");
                else if (!contentType.equals("All") && firstForFilter)
                    query.setType(contentType);
                if (!sortBy.equals("relevance") && firstForFilter)
                    query.setOrder(sortBy);
                query.setFields("items(id/videoId,id/playlistId,snippet/title,snippet/publishedAt,snippet/channelTitle,snippet/thumbnails/medium/url),nextPageToken");
            } else {
                query = youtube.search().list("id,snippet");
                query.setKey(KEY);
                query.setMaxResults(12L);
                query.setRelatedToVideoId(sortBy);
                query.setType("video");
                query.setFields("items(id/videoId,snippet/title,snippet/channelTitle,snippet/thumbnails/medium/url),nextPageToken");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<VideoItem> items = new ArrayList<>();

    public List<VideoItem> search(String keywords, boolean first, boolean isChannel, boolean isSimilarVideos,boolean isSearch) {
        if (!isChannel)
            query.setQ(keywords);
        try {
            if ((!isChannel && (contentType.equals("playlist")?(getToken(keywords) != null):(getVideoToken(keywords) != null) || first == true)) || (isChannel && (MSettings.token.getNextTokenForChannel() != null || first == true)) || isSimilarVideos) {
                if (!first && isSimilarVideos) {
                    query.setPageToken(MSettings.token.getNextTokenForSimilarVideos());
                } else if (!first && !isChannel) {
                    if (contentType.equals("playlist"))
                        query.setPageToken(getToken(keywords));
                    else query.setPageToken(getVideoToken(keywords));
                } else if (!first && isChannel) {
                    query.setPageToken(MSettings.token.getNextTokenForChannel());
                }
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
                        }else if (result.getId().getPlaylistId() != null) {
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
                                    item.setViewCount(videoCount, Byte.parseByte(("2")));
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
                    if (contentType.equals("playlist"))
                        setToken(keywords,response.getNextPageToken());
                    else setVideoToken(keywords,response.getNextPageToken());
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
    public List<VideoItem> search(String keywords, boolean first, boolean isChannel, boolean isSimilarVideos) {
        if (!isChannel)
            query.setQ(keywords);
        try {
            if ((!isChannel && (MSettings.token.getNextTokenSearch() != null || first == true)) || (isChannel && (MSettings.token.getNextTokenForChannel() != null || first == true)) || isSimilarVideos) {
                if (!first && isSimilarVideos) {
                    query.setPageToken(MSettings.token.getNextTokenForSimilarVideos());
                }
                SearchListResponse response = query.execute();
                List<SearchResult> results = response.getItems();
                for (SearchResult result : results) {
                    try {
                        VideoItem item = new VideoItem();
                        item.setTitle(result.getSnippet().getTitle());
                        item.setId(result.getId().getVideoId());
                        item.setThumbnailURL(result.getSnippet().getThumbnails().getMedium().getUrl());
                        item.setChanelTitle(result.getSnippet().getChannelTitle(), false);
                        items.add(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (isSimilarVideos)
                    MSettings.token.setNextTokenForSimilarVideos(response.getNextPageToken());
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
    private String getToken(String word){
        SharedPreferences preferences = ct.getSharedPreferences("searchPlaylistTokens", MODE_PRIVATE);
        return preferences.getString(word,"");
    }
    private String getVideoToken(String word){
        SharedPreferences preferences = ct.getSharedPreferences("searchVideoTokens", MODE_PRIVATE);
        return preferences.getString(word,"");
    }
    private void setToken(String word ,String token){
        SharedPreferences preferences = ct.getSharedPreferences("searchPlaylistTokens", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(word,token);
        editor.commit();
    }
    private void setVideoToken(String word ,String token){
        SharedPreferences preferences = ct.getSharedPreferences("searchVideoTokens", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(word,token);
        editor.commit();
    }
}