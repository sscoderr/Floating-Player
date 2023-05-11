package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.ChannelVideoList;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.Fragment.MyDateFragment;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.RoundedImage;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Sabahattin on 11/22/2016.
 */
public class AdapterForSearchVideo extends BaseAdapter {

    private LayoutInflater mInflater;
    public List<VideoItem> searchResults;
    private Activity activity;
    private String tableName;
    private String[] playlistList;

    public AdapterForSearchVideo(Activity activity, List<VideoItem> list, String tableName) {
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
                if (searchResults.get(position).getDuration() == null && templateViewCount.substring(templateViewCount.indexOf(" ")).equals(activity.getResources().getString(R.string.videoCountName))) {
                    if (holder.myRel!=null)
                        holder.myRel.setVisibility(View.VISIBLE);//PlayList;
                    if (holder.videoCount!=null)
                        holder.videoCount.setText(searchResults.get(position).getViewCount().substring(0, searchResults.get(position).getViewCount().indexOf(" ")));
                    if (holder.image!=null)
                        Picasso.with(activity).load(searchResults.get(position).getThumbnailURL()).into(holder.image);
                    if (holder.myRelForDuration!=null)
                        holder.myRelForDuration.setVisibility(View.INVISIBLE);
                } else if (searchResults.get(position).getDuration() == null && templateViewCount.substring(templateViewCount.indexOf(" ")).equals(activity.getResources().getString(R.string.subscriberName))) {
                    if (holder.image!=null)
                        Picasso.with(activity).load(searchResults.get(position).getThumbnailURL()).transform(new RoundedImage()).into(holder.image);
                    if (holder.myRelForDuration!=null)
                        holder.myRelForDuration.setVisibility(View.INVISIBLE);
                } else {
                    if (holder.image!=null)
                        Picasso.with(activity).load(searchResults.get(position).getThumbnailURL()).into(holder.image);
                }


                holder.title.setText(searchResults.get(position).getTitle());//
                holder.channelTitle.setText(searchResults.get(position).getChannelTitle());// videoCount
                holder.viewCount.setText(searchResults.get(position).getViewCount());// subscriberCount
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
                                db.addVideoFavorites(searchResults.get(position).getId(),searchResults.get(position).getTitle(),searchResults.get(position).getChannelTitle(),searchResults.get(position).getThumbnailURL(), activity);
                                Toast.makeText(activity, "Added Favorites", Toast.LENGTH_SHORT).show();
                                MyDateFragment.isHaveUpdate = true;
                            } else
                                Toast.makeText(activity, "Please select a video", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(activity, "Please select a video", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.add_to_playlist:
                        LayoutInflater li = LayoutInflater.from(activity);
                        View view = li.inflate(R.layout.playlist_dialog, null);
                        final Dialog save = new Dialog(activity);
                        save.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        save.setContentView(view);
                        Button btnCancel = (Button) save.findViewById(R.id.btn_Cancel);
                        Button btnApply = (Button) save.findViewById(R.id.btn_Apply);
                        EditText edtPlaylistName = (EditText) save.findViewById(R.id.playlist_name);
                        final ListView lstPlaylist = (ListView)save.findViewById(R.id.playlist_items_for_dialog);
                        DatabaseForPlaylists db = new DatabaseForPlaylists(activity);
                        SQLiteDatabase readableDatabase = db.getReadableDatabase();
                        final Cursor cur = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_playlist where isYoutubePlaylist=0 order by id desc", null);
                        if (cur != null) {
                            if (cur.moveToFirst()) {
                                playlistList=new String[cur.getCount()];
                                new Thread() {
                                    public void run() {
                                        int i=0;
                                        do {
                                            playlistList[i]=cur.getString(cur.getColumnIndex("playlistName"));
                                            i=i+1;
                                        } while (cur.moveToNext());
                                        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                                                android.R.layout.simple_list_item_1,playlistList);
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                lstPlaylist.setAdapter(adapter);
                                            }
                                        });
                                    }
                                }.start();
                            }
                        }
                        lstPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                Cursor cursor = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_playlist where playlistName=?", new String[]{playlistList[i]});
                                if (cursor != null && cursor.moveToFirst()) {
                                    String playlistId = cursor.getString(cursor.getColumnIndex("id"));
                                    db.addUserVideo(searchResults.get(position).getId(),searchResults.get(position).getTitle(),searchResults.get(position).getChannelTitle(),searchResults.get(position).getThumbnailURL(),activity,playlistId);
                                }
                                Toast.makeText(activity, activity.getResources().getString(R.string.toast_video_added_playlist)+" "+playlistList[i], Toast.LENGTH_SHORT).show();
                                save.dismiss();
                            }
                        });
                        btnCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                save.dismiss();
                            }
                        });
                        btnApply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                db.addUserPlaylist(edtPlaylistName.getText().toString(), 1, activity,0);
                                Cursor mCursor = readableDatabase.rawQuery("SELECT * FROM tbl_fromyoutube_playlist where playlistName=?", new String[]{edtPlaylistName.getText().toString()});
                                if (mCursor != null && mCursor.moveToFirst()) {
                                    String playlistId = mCursor.getString(mCursor.getColumnIndex("id"));
                                    db.addUserVideo(searchResults.get(position).getId(),searchResults.get(position).getTitle(),searchResults.get(position).getChannelTitle(),searchResults.get(position).getThumbnailURL(),activity,playlistId);
                                    Toast.makeText(activity, activity.getResources().getString(R.string.toast_video_added_playlist)+" "+edtPlaylistName.getText().toString(), Toast.LENGTH_SHORT).show();
                                }
                                save.dismiss();
                            }
                        });

                        save.show();
                        break;
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
                            Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(activity, "Please select a video", Toast.LENGTH_SHORT).show();
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