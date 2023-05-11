package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.PlaylistListGridItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PlaylistListsAdapter extends BaseAdapter {
    private Activity activity;
    private List<PlaylistListGridItem> aItem;
    private LayoutInflater mInflater;
    public PlaylistListsAdapter(Activity activity, List<PlaylistListGridItem> item) {
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.aItem=item;
    }
    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        final ViewHolder holder;
        if (rowView == null) {
            rowView = mInflater.inflate(R.layout.playlists_list_grid_item, null);
            holder = new ViewHolder();
            holder.videoCount = (TextView) rowView.findViewById(R.id.grid_video_count);
            holder.playlistTitle = (TextView) rowView.findViewById(R.id.playlist_title);
            holder.vidThumbnail = (ImageView) rowView.findViewById(R.id.grid_playlist_thumb);
            Typeface myTypeface = Typeface.createFromAsset(activity.getAssets(), "VarelaRound-Regular.ttf");
            holder.videoCount.setTypeface(myTypeface);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        if (aItem != null) {
            if (aItem.get(position) != null) {
                Picasso.with(activity).load(aItem.get(position).getThumbnailUrl()).into(holder.vidThumbnail);
                holder.videoCount.setText(aItem.get(position).getVideoCount());
                holder.playlistTitle.setText(aItem.get(position).getPlaylistTitle());
            }
        }
        return rowView;
    }
    @Override
    public int getCount() {
        return aItem != null ? aItem.size() : 0;
    }

    @Override
    public PlaylistListGridItem getItem(int position) {
        return aItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private static class ViewHolder {
        TextView videoCount;
        TextView playlistTitle;
        ImageView vidThumbnail;
    }
    public void addListItemToAdapter(List<PlaylistListGridItem> list) {
        aItem.addAll(list);
        this.notifyDataSetChanged();
    }
}
