package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item;


import android.app.Activity;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Sabahattin on 11/19/2016.
 */
public class VideoItem {
    private String title;
    private String channelTitle;
    private String thumbnailURL;
    private String viewCount;
    private String publishedAt;
    private String id;
    private String duration;
    private String text;
    private String channelImageURL;
    private Activity ac= MSettings.activeActivity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(title.toString().length()>50)
            title=title.toString().substring(0,50)+"...";
        this.title = title;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChanelTitle(String channelTitle,boolean isVideoCount) {
        if (isVideoCount)
            this.channelTitle = channelTitle+" Video";
        else{
            if(channelTitle.length()>20)
                channelTitle=channelTitle.substring(0,20)+"...";
            this.channelTitle = channelTitle;
        }
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnail) {

        this.thumbnailURL = thumbnail;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount, byte selection) {
        if (selection==0||selection==1) {
            byte counter = 0, c = 3;
            String[] array = new String[4];
            for (byte i = Byte.parseByte(String.valueOf(viewCount.length())); i > 0; i--) {
                counter++;
                if (counter == 3) {
                    array[c] = viewCount.substring(i - 1, i + 2);
                    c--;
                    counter = 0;
                } else if (i == 1) {
                    if (counter == 1)
                        array[c] = viewCount.substring(i - 1, i);
                    else if (counter == 2) array[c] = viewCount.substring(i - 1, i + 1);
                    break;
                }
            }
            String template = "";
            for (String text : array) {
                if (text != null)
                    template += text + ".";
            }
            viewCount = template.substring(0, template.length() - 1);
            if (selection==0)
                this.viewCount = viewCount+ac.getResources().getString(R.string.videoView);
            else
                this.viewCount = viewCount + ac.getResources().getString(R.string.subscriberName);
        }
        else this.viewCount = viewCount+ac.getResources().getString(R.string.videoCountName);

    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        publishedAt.substring(0, publishedAt.indexOf("T"));
        String[] myArray = publishedAt.split("-");
        myArray[2]=myArray[2].substring(0,2);
        if (Integer.parseInt(String.valueOf(year))-Integer.parseInt(myArray[0])<=0) {
            if (Integer.parseInt(String.valueOf(month))-Integer.parseInt(myArray[1])<=0) {
                byte value=Byte.parseByte(String.valueOf(Integer.parseInt(String.valueOf(day))-Integer.parseInt(myArray[2])));
                if (value!=0)
                    publishedAt = String.valueOf(value)+ac.getResources().getString(R.string.dateDay);
                else publishedAt="1"+ac.getResources().getString(R.string.dateDay);
            }
            else publishedAt=String.valueOf(Integer.parseInt(String.valueOf(month))-Integer.parseInt(myArray[1]))+ac.getResources().getString(R.string.dateMonth);
        }
        else publishedAt=String.valueOf(Integer.parseInt(String.valueOf(year))-Integer.parseInt(myArray[0]))+ac.getResources().getString(R.string.dateYear);
        this.publishedAt = "  "+publishedAt;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        String first=duration;
        if(duration.indexOf("D")==-1){
            duration=duration.substring(2);
            duration=duration.replace("H", ":");
            duration=duration.replace("M", ":");
            duration=duration.replace("S", "");
        }
        else{
            duration=duration.substring(1);
            int location=duration.indexOf("H");
            String extraHour="0";
            if (location!=-1){
                extraHour=duration.substring(location-1,location);
                if (Character.isDigit(duration.substring(location - 2, location - 1).charAt(0)))
                    extraHour=duration.substring(location-2,location);
            }
            if (Character.isDigit(duration.substring(1,2).charAt(0)))
                duration=String.valueOf((Integer.parseInt(duration.substring(0,1)) + Integer.parseInt(duration.substring(1,2))) * 24+Integer.parseInt(extraHour))+":"+duration.substring(4);
            else duration=(Integer.parseInt(duration.substring(0,1))*24+Integer.parseInt(extraHour))+":"+duration.substring(3);
            if(location!=-1)duration=duration.substring(0, location - 2)+":"+duration.substring(location+1);
            duration=duration.replace("T", "");
            duration = duration.replace("D", ":");
            duration=duration.replace("H", ":");
            duration=duration.replace("M", ":");
            duration=duration.replace("S", "");
        }
        duration = durationEdit(duration,first);
        this.duration = duration;
    }

    private String durationEdit(String durationLast,String durationFirst) {
        String[] myArray = durationLast.split(":");
        String dr = "";
        if (durationLast.substring(durationLast.length()-1,durationLast.length()).equals(":"))
            durationLast=durationLast.substring(0,durationLast.length()-1);
        int isHave_D=durationFirst.indexOf("D"),isHave_H=durationFirst.indexOf("H"),isHave_M=durationFirst.indexOf("M"),isHave_S=durationFirst.indexOf("S");
        boolean H=false,M=false,S=false;
        if (isHave_H != -1)
            H=true;
        if (isHave_M != -1)
            M=true;
        if (isHave_S != -1)
            S=true;
        byte location=Byte.parseByte(String.valueOf(durationLast.indexOf(":")));
        if (isHave_H != -1 || isHave_D!=-1) {
            if (myArray.length<=1)
                dr = durationLast + ":00:00";
            else if (myArray.length==2)
                dr = durationLast.substring(0,location) +":"+ myMetod(M, durationLast.substring(location+1))+":"+ myMetod(S,durationLast.substring(location+1));
            else if (myArray.length==3)
                dr =  durationLast.substring(0,location) +":"+getLocation(true, location, durationLast)+":"+ getLocation(false, location, durationLast);
        }
        else if (isHave_M != -1) {
            if (myArray.length<=1)
                dr = durationLast + ":00";
            else if (myArray.length==2)
                dr =  durationLast.substring(0, location) +":"+ myMetod(S, durationLast.substring(location + 1));
        }
        else if (isHave_S != -1) {
            if (myArray.length<=1) {
                if (durationLast.length() == 1)
                    dr = "0:0" + durationLast;
                else dr = "0:" + durationLast;
            }
            else if (myArray.length==2)
                dr =  myMetod(H,durationLast.substring(location+1))+":"+ myMetod(M,durationLast.substring(location+1))+":"+durationLast.substring(0,location);
        }
        if (durationFirst.equals(" PT0S")){
            dr="Live";
        }
        return dr;
    }

    private String myMetod(boolean value,String otherValue){
        if (value){
            if (otherValue.length()==1)
                otherValue="0"+otherValue;
            return otherValue;
        }

        return "00";
    }

    private String getLocation(boolean isMin,byte location,String duration){
        duration=duration.substring(location+1);
        byte localLocation=Byte.parseByte(String.valueOf(duration.indexOf(":")));
        if (isMin)
        {
            duration=duration.substring(0,localLocation);
            if(duration.length()==1)
                return "0"+duration;
            else return duration;

        }
        duration=duration.substring(localLocation+1);
        if(duration.length()==1)
            return "0"+duration;
        else
            return duration;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getChannelImageURL() {
        return channelImageURL;
    }

    public void setChannelImageURL(String channelImageURL) {
        this.channelImageURL = channelImageURL;
    }
}