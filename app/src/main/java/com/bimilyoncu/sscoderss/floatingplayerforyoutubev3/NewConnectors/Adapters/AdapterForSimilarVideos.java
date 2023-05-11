package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.Adapters;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Database.DatabaseForPlaylists;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.Fragment.MyDateFragment;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.NewConnectors.ItemModels.Video;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.google.gson.annotations.SerializedName;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Sabahattin on 7/23/2020.
 */
public class AdapterForSimilarVideos extends BaseAdapter {

    private LayoutInflater mInflater;
    @SerializedName("result")
    public List<Video> Results;
    private Activity activity;
    private String tableName;
    private String[] playlistList;

    public AdapterForSimilarVideos(Activity activity, List<Video> list) {
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.Results = list;
        this.activity = activity;
        this.tableName = "";

    }

    @Override
    public int getCount() {
        return Results != null ? Results.size() : 0;
    }

    @Override
    public Video getItem(int position) {
        return Results.get(position);
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
            holder.image = (ImageView) rowView.findViewById(R.id.video_thumbnail);

            holder.viewCount = (TextView) rowView.findViewById(R.id.video_viewCount);
            holder.publishAt = (TextView) rowView.findViewById(R.id.video_publishAt);
            holder.duration = (TextView) rowView.findViewById(R.id.video_duration);
            holder.myRelForDuration = (RelativeLayout) rowView.findViewById(R.id.semiTransBGRelative);
            holder.imgMoreItemSearch = (ImageView) rowView.findViewById(R.id.img_more_item_search);
            holder.viewCount.setVisibility(View.INVISIBLE);
            holder.publishAt.setVisibility(View.INVISIBLE);
            holder.duration.setVisibility(View.INVISIBLE);
            holder.myRelForDuration.setVisibility(View.INVISIBLE);

            Typeface myTypeface = Typeface.createFromAsset(activity.getAssets(), "VarelaRound-Regular.ttf");
            holder.title.setTypeface(myTypeface);
            holder.channelTitle.setTypeface(myTypeface);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        if (Results != null) {
            if (Results.get(position) != null) {
                Picasso.with(activity).load(Results.get(position).getThumbnailSrc()).into(holder.image);
                holder.title.setText(Results.get(position).getTitle());
                holder.channelTitle.setText(Results.get(position).getUsername());

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

    public void addListItemToAdapter(List<Video> list) {
        Results.addAll(list);
        this.notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView title, channelTitle, viewCount, publishAt, duration, videoCount;
        ImageView image,imgMoreItemSearch;
        RelativeLayout myRelForDuration;
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
                            if (Results.get(position).getId().length() == 11) {
                                DatabaseForPlaylists db = new DatabaseForPlaylists(activity);
                                db.addVideoFavorites(Results.get(position).getId(), Results.get(position).getTitle(), Results.get(position).getUsername(), Results.get(position).getThumbnailSrc(), activity);
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
                        if (Results.get(position).getId().length() == 11) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, MSettings.youtubeWatchURL + Results.get(position).getId());
                            sendIntent.setType("text/plain");
                            activity.startActivity(sendIntent);
                            break;
                        } else {
                            Toast.makeText(activity, "Please select a video", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.add_to_playlist:
                        try{
                            if (MSettings.CheckService)
                                MSettings.MinimizePlayer();
                        }catch (Exception e){
                        }

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
                                    db.addUserVideo(Results.get(position).getId(), Results.get(position).getTitle(), Results.get(position).getUsername(), Results.get(position).getThumbnailSrc(),activity,playlistId);
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
                                    db.addUserVideo(Results.get(position).getId(), Results.get(position).getTitle(), Results.get(position).getUsername(), Results.get(position).getThumbnailSrc(),activity,playlistId);
                                    Toast.makeText(activity, activity.getResources().getString(R.string.toast_video_added_playlist)+" "+edtPlaylistName.getText().toString(), Toast.LENGTH_SHORT).show();
                                }
                                save.dismiss();
                            }
                        });
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                            save.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                        else save.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
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
                            db.videoDeleteFromFavs(Results.get(position).getId(), tableName);
                            removeItem(position);
                            Toast.makeText(activity, "Deleted", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.share:
                        if (Results.get(position).getId().length() == 11) {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, MSettings.youtubeWatchURL + Results.get(position).getId());
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
        Results.remove(id);
        this.notifyDataSetChanged();
    }
}