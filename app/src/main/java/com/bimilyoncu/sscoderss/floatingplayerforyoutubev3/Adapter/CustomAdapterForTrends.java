package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.Fragment.MyDateFragment.isHaveUpdate;

/**
 * Created by Sabahattin on 11/22/2016.
 */
public class CustomAdapterForTrends extends BaseAdapter {
    private LayoutInflater mInflater;
    public List<VideoItem> searchResults;
    private Activity activity;

    public CustomAdapterForTrends(Activity activity, List<VideoItem> list) {
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.searchResults = list;
        this.activity=activity;
    }

    @Override
    public int getCount() { return searchResults != null ? searchResults.size() : 0; }

    @Override
    public VideoItem getItem(int position) {
        return searchResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position,View rowView, ViewGroup parent) {
        final ViewHolder holder;
        if(rowView==null) {
            rowView = mInflater.inflate(R.layout.trend_activity_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) rowView.findViewById(R.id.txtDescription);
            holder.channelTitle = (TextView) rowView.findViewById(R.id.txtChannelName);
            holder.viewCount = (TextView) rowView.findViewById(R.id.txtViewCount);
            holder.publishAt = (TextView) rowView.findViewById(R.id.txtUploadDate);
            holder.duration = (TextView) rowView.findViewById(R.id.txtDuration);
            holder.image = (ImageView) rowView.findViewById(R.id.video_img);
            holder.moreOption = (ImageView) rowView.findViewById(R.id.img_more_item);
            holder.myRelForDuration = (RelativeLayout) rowView.findViewById(R.id.semiTransBGRelative);

            holder.title.setTypeface(MSettings.getFontVarelaRound());
            holder.channelTitle.setTypeface(MSettings.getFontVarelaRound());
            holder.viewCount.setTypeface(MSettings.getFontVarelaRound());
            holder.publishAt.setTypeface(MSettings.getFontVarelaRound());

            holder.viewCount.setVisibility(View.INVISIBLE);
            holder.publishAt.setVisibility(View.INVISIBLE);
            holder.duration.setVisibility(View.INVISIBLE);
            holder.myRelForDuration.setVisibility(View.INVISIBLE);

            rowView.setTag(holder);
        }
        else{
            holder = (ViewHolder) rowView.getTag();
        }
        Picasso.with(activity).load(searchResults.get(position).getThumbnailURL()).into(holder.image);
        holder.title.setText(searchResults.get(position).getTitle());
        holder.channelTitle.setText(searchResults.get(position).getChannelTitle());
        holder.moreOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu ppMenu=new PopupMenu(activity,v);
                ppMenu.getMenuInflater().inflate(R.menu.more_option_for_trend, ppMenu.getMenu());
                ppMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // TODO Auto-generated method stub
                        switch (item.getItemId()) {
                            case R.id.addFavorite:
                                try {
                                    if (searchResults.get(position).getId().length() == 11) {
                                        DatabaseForPlaylists db = new DatabaseForPlaylists(activity);
                                        db.addVideoFavorites(searchResults.get(position).getId(),searchResults.get(position).getTitle(),searchResults.get(position).getChannelTitle(),searchResults.get(position).getThumbnailURL(), activity);
                                        Toast.makeText(activity, "Favorilere Ekendi", Toast.LENGTH_SHORT).show();
                                        isHaveUpdate = true;
                                    } else
                                        Toast.makeText(activity, "Lutfen Kanal Veya Playlist Secmeyin", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(activity, "Bir Hata Oluştu", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case R.id.share:
                                if (searchResults.get(position).getId().length() == 11) {
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, MSettings.youtubeWatchURL + searchResults.get(position).getId());
                                    sendIntent.setType("text/plain");
                                    activity.startActivity(sendIntent);
                                    break;
                                } else {
                                    Toast.makeText(activity, "Lutfen Kanal Veya Playlist Secmeyin", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                        return true;
                    }

                });
                ppMenu.show();
            }
        });
        return rowView;
    }
    public void addListItemToAdapter(List<VideoItem> list) {
        searchResults.addAll(list);
        this.notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView title, channelTitle, viewCount, publishAt, duration;
        ImageView image,moreOption;
        RelativeLayout myRelForDuration;
    }
}