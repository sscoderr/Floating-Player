package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("results")
    private List<Video> video;

    public List<Video> getVideo() {
        return video;
    }

    public void setVideo(List<Video> video) {
        this.video = video;
    }
}
