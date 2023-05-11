package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item;

public class PlaylistListGridItem {
    private String id;
    private String playlistTitle;
    private String videoCount;
    private String thumbnailUrl;

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaylistTitle() {
        return playlistTitle;
    }

    public void setPlaylistTitle(String playlistTitle) {
        if(playlistTitle.toString().length()>40)
            playlistTitle=playlistTitle.toString().substring(0,35)+"...";
        this.playlistTitle = playlistTitle;
    }

    public String getVideoCount() {
        return videoCount;
    }

    public void setVideoCount(String videoCount) {
        this.videoCount = videoCount;
    }
}
