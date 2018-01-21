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
    public static String KEY = "AIzaSyDzYXfEUkLxWCO4Ah6MM_5iF8yjwJDAbVA";
    public static  final String[] myApiKeys={ "AIzaSyDws4e3XqnRRnxjDq_jbnvqMqM2ynBITY0" //            "AIzaSyDzYXfEUkLxWCO4Ah6MM_5iF8yjwJDAbVA",
//            "AIzaSyDtDVfXOkttjFCcRA3Oz6stUk6wbsGwMI4",
//            "AIzaSyB4oWW5ouoZDD9jRL2ec_iM57Lw43DJsSQ",
//            "AIzaSyC3Px-30XphZKjNaFgQDNyxf4EZjNy-hIM",
//            "AIzaSyCrOmNyrJI_E1_FxPf7RJOJaYIiQA7jF0E",
//            "AIzaSyApwUEthNBFN8nbT2Fpjm02ud0fgk7LPJA",
//            "AIzaSyDC70HEYwod3Z1uo18E6_lsBdrxxSw9sI4",
//            "AIzaSyDAvu0PNSWLiZtuTiG0Dy4uW24yVEpfMSo",
//            /*FSBIMILYONCU*/
//            "AIzaSyDp2bMUPNAYuYbCO7qAKj1CWyzcB5dMibw",
//            "AIzaSyCKzy1bjKgGsR9TTfbI4Ghr8lSqC_64K8k",
//            "AIzaSyCpuhxQ6k3KMmaFluNDuHB9lB3isTa5DuM",
//            "AIzaSyCvBZ5Rchyb0qqHI1Lq1GrVn8bhwnQQIhQ",
//            "AIzaSyD_m3vrPzvLN2MQ_sPADj7FjEeTOEaHX0k",
//            "AIzaSyAum5tEQy16LmoWUEMnmIf1NR8LJatx6sk",
//            "AIzaSyCwO5Yw0BaAPmvF3zNUK9sBv1h-ZyhQ3Qo",
//            "AIzaSyDHtIGNohuxRZAJVEkpFPBRX_W1gCgt4pg",
//            "AIzaSyDrPz6H8HPSl_1O17Zz_m8kjVhZIChfxFU",
//            "AIzaSyAN0_Ig6rXAZoau67VL35qKiVkKqnah_9k",
//            /**MYKEYMAILONE**/
//            "AIzaSyB-HuJ6pMFse4QAEZcYXiai0LorMSlEpxc",
//            "AIzaSyAyaur-mNdI1jofZyzewuJ17TzdZQG-rcQ",
//            "AIzaSyDDk8MY7ZzP3efgseKYClUwMXJIOu5d0gg",
//            "AIzaSyBaKtI9SvD62CtclLhDvS_h8LoBJm8g4Ag",
//            "AIzaSyCxxANeHC3RW_wb4f8_V7BQu0atu6WvLmM",
//            "AIzaSyDfGpq0Oi5bECUHHcDeRW0xyd-AOUT5u90",
//            "AIzaSyBZ5za6scpolvFUmbRH6T4je0nJY28rKr4",
//            "AIzaSyDeneY4NKefHwN3O6E4WJS-q57evknSLHI",
//            "AIzaSyCwsaaEWyQQmvEzBT91adMrZxS3rr8J9Po",
//            "AIzaSyB3g1RRW97xILp8831Lt4bad7K4m3r5mwY",
//            "AIzaSyBMC8Y7nKChZptgFBv457HzVSWY-8Kn5Y8",
//            "AIzaSyDM6vyUWghBImyZYlAZaI721A3PhztOnjc",
//            /**MYKEYMAILTWO**/
//            "AIzaSyBhKko50a-UkZ1yK6c0vzTuERU1UR6iQmo",
//            "AIzaSyCoiuJCSCIx67ltF1dXQ7wTjTUXE6P3Lnw",
//            "AIzaSyAoSHRcL1A98X9keihyTh1PKQid9TiUYyo",
//            "AIzaSyAplYxtQlPW513Bqyn7xG9kHLizq1c2F6c",
//            "AIzaSyBC-b0zzzWbIP0eyxUbwUgVGhzDU1yRabA",
//            "AIzaSyDzee0ryNLDdJYNQwyv1WHjl9sQjpRr4XI",
//            "AIzaSyDRrpKtH7IrMq34Ag6w-bN-583QCEiaB5A",
//            "AIzaSyBcXwzXsp1QhDDoSQrfHXvKOYSmUi8jWLM",
//            "AIzaSyAS0gKGerEbLv-lnSkmhPmtONZm_qOa1pQ",
//            "AIzaSyDvjYGQ2Yv0r-rZVMkU_pMr0PX0zCKGPjI",
//            "AIzaSyAOTRbnVfyGQDoT2AY2XzQpNR7C7FFZ5Lc",
//            "AIzaSyBOlFl7zLxKUR6s720oIbEejUfJGMI1ReM"
        };

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
            Log.d("YC", "Could not initialize: " + e);
        }
    }

    List<VideoItem> items = new ArrayList<>();

    public List<VideoItem> search(String keywords, boolean first, boolean isChannel, boolean isSimilarVideos) {
        if (!isChannel)
            query.setQ(keywords);
        try {
            if ((!isChannel && (MSettings.token.getNextToken() != null || first == true)) || (isChannel && (MSettings.token.getNextTokenForChannel() != null || first == true)) || isSimilarVideos) {
                if (!first && isSimilarVideos) {
                    query.setPageToken(MSettings.token.getNextTokenForSimilarVideos());
                } else if (!first && !isChannel) {
                    query.setPageToken(MSettings.token.getNextToken());
                } else if (!first && isChannel) {
                    query.setPageToken(MSettings.token.getNextTokenForChannel());
                }
                Log.e("Token2: ", String.valueOf(MSettings.token.getNextToken()));
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
                            }
                        }
                        items.add(item);
                    } catch (Exception e) {
                    }
                }
                if (isSimilarVideos) {
                    MSettings.token.setNextTokenForSimilarVideos(response.getNextPageToken());
                } else if (!isChannel) {
                    MSettings.token.setNextToken(response.getNextPageToken());
                } else if (isChannel) {
                    MSettings.token.setNextTokenForChannel(response.getNextPageToken());
                }
            }
            return items;
        } catch (IOException e) {
            return null;
        }
    }
}