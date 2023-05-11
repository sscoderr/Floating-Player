package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.SliderAdapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.MainActivity;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.RoundedImage;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.FirstPageHorizontalSliderItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.DestroyOrShowBanner;
import static com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings.playedPoss;


public class FirstPageHorizontalSliderAdapter extends RecyclerView.Adapter<FirstPageHorizontalSliderAdapter.viewHolder> {


    private Activity activity;
    private List<FirstPageHorizontalSliderItem> aItem;
    public FirstPageHorizontalSliderAdapter(Activity activity, List<FirstPageHorizontalSliderItem> item) {
        this.activity = activity;
        this.aItem=item;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(activity);
        view = mInflater.inflate(R.layout.first_page_horizontal_recycle_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, final int position) {
        if (aItem != null) {
            if (aItem.get(position) != null) {
                Picasso.with(activity).load(aItem.get(position).getVidthumbnialUrl()).transform(new RoundedImage()).into(holder.imgThumbnail);
                holder.videoTitle.setText(aItem.get(position).getVidTitle());
            }
        }
        holder.relClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Most And Last Played RecycleView Click**/
                DestroyOrShowBanner(MSettings.adViewTrendMusic, true);
                DestroyOrShowBanner(MSettings.adViewTrendVideo, true);
                DestroyOrShowBanner(MSettings.adViewMyData, true);
                DestroyOrShowBanner(MSettings.adViewPlaylist,true);
                MSettings.CounterForSimilarVideos = 1;
                playedPoss = new ArrayList<Integer>();
                VideoItem videoItem =new VideoItem();
                videoItem.setId(aItem.get(position).getId());
                videoItem.setTitle(aItem.get(position).getVidTitle());
                videoItem.setThumbnailURL(aItem.get(position).getVidthumbnialUrl());
                videoItem.setChanelTitle(aItem.get(position).getChannelTitle(),false);
                MSettings.currentVItem = videoItem;
                MSettings.activeVideo = videoItem;
                MSettings.activeActivity = activity;
                MainActivity mainActivity = new MainActivity();
                mainActivity.getSimilarVideos(String.valueOf(aItem.get(position).getId()), false, false, false, new String[]{});
                MSettings.IsRetry = false;
                MSettings.videoFinishStopVideoClicked = true;
                MSettings.LoadVideo();
                MSettings.LoadSixTapAds();
            }
        });
    }

    @Override
    public int getItemCount() {
        return aItem.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        TextView videoTitle;
        ImageView imgThumbnail;
        RelativeLayout relClick ;

        public viewHolder(View itemView) {
            super(itemView);

            videoTitle = (TextView) itemView.findViewById(R.id.fph_video_title) ;
            imgThumbnail = (ImageView) itemView.findViewById(R.id.fph_thumbnail);
            relClick = (RelativeLayout) itemView.findViewById(R.id.rel_for_pos);
        }
    }


}