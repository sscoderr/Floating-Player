package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.ChannelVideoList;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.Fragment.MyDateFragment;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.RoundedImage;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.SearchActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item.VideoItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Sabahattin on 11/22/2016.
 */
public class CustomAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    public List<VideoItem> searchResults;
    private Activity activity;
    private String tableName;

    public CustomAdapter(Activity activity, List<VideoItem> list, String tableName) {
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
            holder.channelTitle = (TextView) rowView.findViewById(R.id.video_channelTitle);
            holder.viewCount = (TextView) rowView.findViewById(R.id.video_viewCount);
            holder.publishAt = (TextView) rowView.findViewById(R.id.video_publishAt);
            holder.duration = (TextView) rowView.findViewById(R.id.video_duration);
            holder.image = (ImageView) rowView.findViewById(R.id.video_thumbnail);
            holder.playedVideoImage = (ImageView) rowView.findViewById(R.id.play_icon);
            holder.imgMoreItemSearch = (ImageView) rowView.findViewById(R.id.img_more_item_search);
            holder.myRelForDuration = (RelativeLayout) rowView.findViewById(R.id.semiTransBGRelative);
            holder.playedVideoRel = (RelativeLayout) rowView.findViewById(R.id.relative_play_video_color);
            holder.myRel = (RelativeLayout) rowView.findViewById(R.id.semiTransBGRelativeBig);
            holder.videoCount = (TextView) rowView.findViewById(R.id.video_Count);

            Typeface myTypeface = Typeface.createFromAsset(activity.getAssets(), "VarelaRound-Regular.ttf");
            holder.title.setTypeface(myTypeface);
            holder.channelTitle.setTypeface(myTypeface);
            holder.viewCount.setTypeface(myTypeface);
            holder.publishAt.setTypeface(myTypeface);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        holder.myRel.setVisibility(View.INVISIBLE);
        holder.myRelForDuration.setVisibility(View.VISIBLE);

        if (searchResults != null) {
            if (searchResults.get(position) != null) {
                String templateViewCount = searchResults.get(position).getViewCount();
                if (searchResults.get(position).getDuration() == null && templateViewCount.substring(templateViewCount.indexOf(" "), templateViewCount.length()).equals(SearchActivity.myCt.getResources().getString(R.string.videoCountName))) {
                    holder.myRel.setVisibility(View.VISIBLE);//PlayList;
                    holder.videoCount.setText(searchResults.get(position).getViewCount().substring(0, searchResults.get(position).getViewCount().indexOf(" ")));
                    Picasso.with(activity).load(searchResults.get(position).getThumbnailURL()).into(holder.image);
                    holder.myRelForDuration.setVisibility(View.INVISIBLE);
                } else if (searchResults.get(position).getDuration() == null && templateViewCount.substring(templateViewCount.indexOf(" "), templateViewCount.length()).equals(SearchActivity.myCt.getResources().getString(R.string.subscriberName))) {
                    Picasso.with(activity).load(searchResults.get(position).getThumbnailURL()).transform(new RoundedImage()).into(holder.image);
                    holder.myRelForDuration.setVisibility(View.INVISIBLE);
                } else {
                    Picasso.with(activity).load(searchResults.get(position).getThumbnailURL()).into(holder.image);
                }


                holder.title.setText(searchResults.get(position).getTitle());
                holder.channelTitle.setText(searchResults.get(position).getChannelTitle());
                holder.viewCount.setText(searchResults.get(position).getViewCount());
                holder.publishAt.setText(searchResults.get(position).getPublishedAt());
                holder.duration.setText(searchResults.get(position).getDuration());
                ChannelVideoList.channelName = searchResults.get(0).getChannelTitle();
                holder.imgMoreItemSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tableName.equals(""))
                            loadMoreOption(position, v);
                        else loadMoreOptionForFavorite(position, v);

                    }
                });
            }
        }

        return rowView;
    }

    public void addListItemToAdapter(List<VideoItem> list) {
        searchResults.addAll(list);
        this.notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView title, channelTitle, viewCount, publishAt, duration, videoCount;
        ImageView image, imgMoreItemSearch, playedVideoImage;
        RelativeLayout myRel, myRelForDuration, playedVideoRel;
    }

    private void loadMoreOption(final int position, View v) {
        PopupMenu ppMenu = new PopupMenu(activity, v);
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
                                db.addVideoFavorites(searchResults.get(position).getId(), activity);
                                Toast.makeText(activity, "Favorilere Ekendi", Toast.LENGTH_SHORT).show();
                                MyDateFragment.isHaveUpdate = true;
                            } else
                                Toast.makeText(activity, "Lutfen Kanal Veya Playlist Secmeyin", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
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
                }
                return true;
            }

        });
        ppMenu.show();
    }

    private void loadMoreOptionForFavorite(final int position, View v) {
        PopupMenu ppMenu = new PopupMenu(activity, v);
        ppMenu.getMenuInflater().inflate(R.menu.more_option_for_fav, ppMenu.getMenu());
        ppMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO Auto-generated method stub
                switch (item.getItemId()) {
                    case R.id.delete:
                        try {
                            DatabaseForPlaylists db = new DatabaseForPlaylists(activity);
                            db.videoDeleteFromFavs(searchResults.get(position).getId(), tableName);
                            removeItem(position);
                            Toast.makeText(activity, "Silindi", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
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

    private void removeItem(int id) {
        searchResults.remove(id);
        this.notifyDataSetChanged();
    }

    private void addItem(VideoItem id) {
        searchResults.add(id);
        this.notifyDataSetChanged();
    }
}