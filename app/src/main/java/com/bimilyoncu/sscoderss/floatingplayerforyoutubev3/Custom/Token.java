package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom;

/**
 * Created by furkan0 on 24.12.2017.
 */

public class Token {
    private static String nextTokenForTrendVideos
            , nextTokenForTrendMusic
            , nextTokenForSimilarVideos
            , nextTokenForPlayList
            , nextTokenForChannel
            , nextTokenSearch
            , nextTokenSearchChannelVideo
            , nextTokenSearchPlaylistVideo;

    public static String getNextTokenForTrendVideos() {
        return nextTokenForTrendVideos;
    }

    public static void setNextTokenForTrendVideos(String nextTokenForTrendVideos) {
        Token.nextTokenForTrendVideos = nextTokenForTrendVideos;
    }

    public static String getNextTokenForTrendMusic() {
        return nextTokenForTrendMusic;
    }

    public static void setNextTokenForTrendMusic(String nextTokenForTrendMusic) {
        Token.nextTokenForTrendMusic = nextTokenForTrendMusic;
    }

    public static String getNextTokenForSimilarVideos() {
        return nextTokenForSimilarVideos;
    }

    public static void setNextTokenForSimilarVideos(String nextTokenForSmilarVideos) {
        Token.nextTokenForSimilarVideos = nextTokenForSmilarVideos;
    }

    public static String getNextTokenForPlayList() {
        return nextTokenForPlayList;
    }

    public static void setNextTokenForPlayList(String nextTokenForPlayList) {
        Token.nextTokenForPlayList = nextTokenForPlayList;
    }

    public static String getNextTokenForChannel() {
        return nextTokenForChannel;
    }

    public static void setNextTokenForChannel(String nextTokenForChannel) {
        Token.nextTokenForChannel = nextTokenForChannel;
    }

    public static String getNextTokenSearch() {
        return nextTokenSearch;
    }

    public static void setNextTokenSearch(String nextTokenSearch) {
        Token.nextTokenSearch = nextTokenSearch;
    }

    public static String getNextTokenSearchChannelVideo() {
        return nextTokenSearchChannelVideo;
    }

    public static void setNextTokenSearchChannelVideo(String nextTokenSearchChannelVideo) {
        Token.nextTokenSearchChannelVideo = nextTokenSearchChannelVideo;
    }

    public static String getNextTokenSearchPlaylistVideo() {
        return nextTokenSearchPlaylistVideo;
    }

    public static void setNextTokenSearchPlaylistVideo(String nextTokenSearchPlaylistVideo) {
        Token.nextTokenSearchPlaylistVideo = nextTokenSearchPlaylistVideo;
    }
}