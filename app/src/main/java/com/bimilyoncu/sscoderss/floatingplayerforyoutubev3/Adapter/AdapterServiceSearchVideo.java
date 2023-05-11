package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.ChannelVideoList;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Sabahattin on 11/22/2016.
 */
public class AdapterServiceSearchVideo extends BaseAdapter {

    private LayoutInflater mInflater;
    public List<VideoItem> searchResults;
    private Activity activity;
    private String tableName;

    public AdapterServiceSearchVideo(Activity activity, List<VideoItem> list, String tableName) {
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.searchResults = list;
        this.activity = activity;
        this.tableName = tableName;

    }

    @Override
    public int getCount() {
        return searchResults != null ? searchResults.size() : 0;
    }

    @Override
    public VideoItem getItem(int position) {
        return searchResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        final ViewHolder holder;
        if (rowView == null) {
            rowView = mInflater.inflate(R.layout.video_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) rowView.findViewById(R.id.video_title);
            holder.image = (ImageView) rowView.findViewById(R.id.video_thumbnail);
            holder.myRelForDuration = (RelativeLayout) rowView.findViewById(R.id.semiTransBGRelative);
            holder.playedVideoRel = (RelativeLayout) rowView.findViewById(R.id.relative_play_video_color);
            holder.myRel = (RelativeLayout) rowView.findViewById(R.id.semiTransBGRelativeBig);
            Typeface myTypeface = Typeface.createFromAsset(activity.getAssets(), "VarelaRound-Regular.ttf");
            holder.title.setTypeface(myTypeface);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.myRel.setVisibility(View.INVISIBLE);
        holder.myRelForDuration.setVisibility(View.VISIBLE);

        if (searchResults != null) {
            if (searchResults.get(position) != null) {
                Picasso.with(activity).load(searchResults.get(position).getThumbnailURL()).into(holder.image);
                holder.myRelForDuration.setVisibility(View.INVISIBLE);

                holder.title.setText(searchResults.get(position).getTitle());
                ChannelVideoList.channelName = searchResults.get(0).getChannelTitle();
            }
        }

        return rowView;
    }

    private static class ViewHolder {
        TextView title;
        ImageView image;
        RelativeLayout myRel, myRelForDuration, playedVideoRel;
    }
}