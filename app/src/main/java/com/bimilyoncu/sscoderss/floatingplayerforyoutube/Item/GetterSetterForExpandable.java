package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item;

import java.util.ArrayList;

/**
 * Created by Sabahattin on 14.03.2017.
 */

public class GetterSetterForExpandable {
    private String playlistHeader;
    private ArrayList<VideoItem>vItems=new ArrayList<VideoItem>();
    private String videoCount;

    public GetterSetterForExpandable(String header, ArrayList<VideoItem> vItems, String videoCount) {
        super();
        this.vItems = vItems;
        this.playlistHeader = header;
        this.videoCount=videoCount;
    }

    public String getvideoCount() {
        return videoCount;
    }

    public void setvideoCount(String date) {
        this.videoCount = date;
    }

    public String getplaylistHeader() {
        return playlistHeader;
    }

    public void setplaylistHeader(String name) {
        this.playlistHeader = name;
    }

    public ArrayList<VideoItem> getvItems() {
        return vItems;
    }

    public void setvItems(ArrayList<VideoItem> countryList) {
        this.vItems = countryList;
    }
}
