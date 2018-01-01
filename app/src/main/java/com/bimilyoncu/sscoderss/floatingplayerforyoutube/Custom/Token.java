package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom;

/**
 * Created by furkan0 on 24.12.2017.
 */

public class Token {
    private static String nextTokenForTrendVideos
            , nextTokenForTrendMusic
            , nextTokenForSimilarVideos
            , nextTokenForPlayList
            , nextTokenForChannel
            , nextToken;

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

    public String getNextToken() {
        return this.nextToken;
    }

    public void setNextToken(String nextToken) {
        this.nextToken = nextToken;
    }
}