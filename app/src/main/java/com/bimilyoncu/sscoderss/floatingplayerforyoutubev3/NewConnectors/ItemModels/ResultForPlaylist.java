package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultForPlaylist {
    @SerializedName("results")
    private List<PlaylistList> video;

    public List<PlaylistList> getVideo() {
        return video;
    }

    public void setVideo(List<PlaylistList> video) {
        this.video = video;
    }
}
