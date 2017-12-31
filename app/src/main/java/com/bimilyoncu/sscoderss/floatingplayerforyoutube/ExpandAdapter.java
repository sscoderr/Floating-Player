package com.bimilyoncu.sscoderss.floatingplayerforyoutube;

/**
 * Created by Sabahattin on 13.03.2017.
 */

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.squareup.picasso.Picasso;

public class ExpandAdapter extends BaseExpandableListAdapter {

    private Context ct;
    private ArrayList<GetterSetterForExpandable> userDataAll;
    private LayoutInflater mInflater;
    private String tableName;
    private int pos=-1;

    public ExpandAdapter(Activity activity, ArrayList<GetterSetterForExpandable> list, String tblName) {
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.userDataAll=list;
        this.ct = activity;
        this.tableName=tblName;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        ArrayList<VideoItem> countryList = userDataAll.get(groupPosition).getvItems();
        return countryList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int position, boolean isLastChild, View rowView, ViewGroup parent) {
        final VideoItem vItem=(VideoItem)getChild(groupPosition,position);

        final ExpandAdapter.ViewHolder holder;
        if(rowView==null) {
            rowView = mInflater.inflate(R.layout.video_item, null);
            holder = new ExpandAdapter.ViewHolder();
            holder.title = (TextView) rowView.findViewById(R.id.video_title);
            holder.channelTitle = (TextView) rowView.findViewById(R.id.video_channelTitle);
            holder.viewCount = (TextView) rowView.findViewById(R.id.video_viewCount);
            holder.publishAt = (TextView) rowView.findViewById(R.id.video_publishAt);
            holder.duration = (TextView) rowView.findViewById(R.id.video_duration);
            holder.image = (ImageView) rowView.findViewById(R.id.video_thumbnail);
            holder.imgMoreItemSearch=(ImageView)rowView.findViewById(R.id.img_more_item_search);
            holder.myRelForDuration=(RelativeLayout)rowView.findViewById(R.id.semiTransBGRelative);
            holder.myRel=(RelativeLayout)rowView.findViewById(R.id.semiTransBGRelativeBig);
            holder.videoCount=(TextView) rowView.findViewById(R.id.video_Count);
            Typeface myTypeface = Typeface.createFromAsset(ct.getAssets(), "VarelaRound-Regular.ttf");
            holder.title.setTypeface(myTypeface);
            holder.channelTitle.setTypeface(myTypeface);
            holder.viewCount.setTypeface(myTypeface);
            holder.publishAt.setTypeface(myTypeface);
            rowView.setTag(holder);
        }
        else{
            holder = (ExpandAdapter.ViewHolder) rowView.getTag();
        }
        holder.myRel.setVisibility(View.INVISIBLE);
        String templateViewCount=vItem.getViewCount();
        holder.myRelForDuration.setVisibility(View.VISIBLE);
        if(vItem.getDuration()==null&&templateViewCount.substring(templateViewCount.indexOf(" "), templateViewCount.length()).equals(SearchActivity.myCt.getResources().getString(R.string.videoCountName))) {
            holder.myRel.setVisibility(View.VISIBLE);//PlayList;
            holder.videoCount.setText(vItem.getViewCount().substring(0, vItem.getViewCount().indexOf(" ")));
            Picasso.with(ct).load(vItem.getThumbnailURL()).into(holder.image);
            holder.myRelForDuration.setVisibility(View.INVISIBLE);
        }
        else if (vItem.getDuration()==null&&templateViewCount.substring(templateViewCount.indexOf(" "), templateViewCount.length()).equals(SearchActivity.myCt.getResources().getString(R.string.subscriberName))) {
            Picasso.with(ct).load(vItem.getThumbnailURL()).transform(new RoundedImage()).into(holder.image);
            holder.myRelForDuration.setVisibility(View.INVISIBLE);
        }
        else { Picasso.with(ct).load(vItem.getThumbnailURL()).into(holder.image);}


        holder.title.setText(vItem.getTitle());
        holder.channelTitle.setText(vItem.getChannelTitle());
        holder.viewCount.setText(vItem.getViewCount());
        holder.publishAt.setText(vItem.getPublishedAt());
        holder.duration.setText(vItem.getDuration());
        ChannelVideoList.channelName=vItem.getChannelTitle();
        holder.imgMoreItemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tableName.equals(""))
                    loadMoreOption(vItem,v);
                else loadMoreOptionForFavorite(vItem,v,userDataAll.get(groupPosition).getplaylistHeader(),groupPosition);

            }
        });
        return rowView;
    }
    private static class ViewHolder {
        TextView title, channelTitle, viewCount, publishAt, duration,videoCount;
        ImageView image,imgMoreItemSearch;
        RelativeLayout myRel,myRelForDuration;
    }
    private void loadMoreOption(final VideoItem vItem,View v){
        PopupMenu ppMenu=new PopupMenu(ct,v);
        ppMenu.getMenuInflater().inflate(R.menu.more_option_for_trend, ppMenu.getMenu());
        ppMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO Auto-generated method stub
                switch (item.getItemId()) {
                    case R.id.addFavorite:
                        try {
                            if (vItem.getId().length()==11) {
                                DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
                                db.addVideoFavorites(vItem.getId(), ct);
                                Toast.makeText(ct, "Favorilere Ekendi", Toast.LENGTH_SHORT).show();
                                MyDateFragment.isHaveUpdate=true;
                            }
                            else Toast.makeText(ct, "Lutfen Kanal Veya Playlist Secmeyin", Toast.LENGTH_SHORT).show();
                        }catch(Exception e){
                            Toast.makeText(ct, "Bir Hata Oluştu", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.share: Toast.makeText(ct, "share", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }

        });
        ppMenu.show();
    }
    private void loadMoreOptionForFavorite(final VideoItem vItem,View v,final String playlistName,final int pos){
        PopupMenu ppMenu=new PopupMenu(ct,v);
        ppMenu.getMenuInflater().inflate(R.menu.more_option_for_fav, ppMenu.getMenu());
        ppMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO Auto-generated method stub
                switch (item.getItemId()) {
                    case R.id.delete:
                        try {
                            DatabaseForPlaylists db = new DatabaseForPlaylists(ct);
                            db.videoDeleteFromFavs(vItem.getId(),tableName);
                            Toast.makeText(ct, playlistName, Toast.LENGTH_SHORT).show();
                            db.updateVideoCount(playlistName);
                            removeItem(vItem,pos);
                            Toast.makeText(ct, "Silindi", Toast.LENGTH_SHORT).show();
                        }catch(Exception e){
                            Toast.makeText(ct, "Bir Hata Oluştu", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.share: Toast.makeText(ct, "share", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }

        });
        ppMenu.show();
    }
    private void removeItem(VideoItem vItem,int pos){
        userDataAll.get(pos).getvItems().remove(vItem);
        this.pos=pos;
        GetterSetterForExpandable headerRow = (GetterSetterForExpandable) getGroup(pos);
        headerRow.setvideoCount(String.valueOf(Integer.parseInt(headerRow.getvideoCount())-1));
        if (Integer.parseInt(headerRow.getvideoCount().toString())<=0)
            userDataAll.remove(pos);
        this.notifyDataSetChanged();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        ArrayList<VideoItem> countryList = userDataAll.get(groupPosition).getvItems();
        return countryList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return userDataAll.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return userDataAll.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GetterSetterForExpandable headerRow = (GetterSetterForExpandable) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expand_list_header, null);
        }

        TextView userPlaylistHeader = (TextView) convertView.findViewById(R.id.txtHeader);
        TextView userPlaylistVideoCount = (TextView) convertView.findViewById(R.id.txtCount);
        Typeface myTypeface = Typeface.createFromAsset(ct.getAssets(), "VarelaRound-Regular.ttf");
        userPlaylistHeader.setTypeface(myTypeface);
        userPlaylistHeader.setText(headerRow.getplaylistHeader());
        userPlaylistVideoCount.setText(headerRow.getvideoCount());


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}