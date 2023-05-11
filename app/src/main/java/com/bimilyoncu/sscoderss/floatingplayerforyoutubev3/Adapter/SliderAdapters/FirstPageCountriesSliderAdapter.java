package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.SliderAdapters;

 import android.app.Activity;
 import android.content.Intent;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.widget.ImageView;
 import android.widget.RelativeLayout;
 import android.widget.TextView;

 import androidx.recyclerview.widget.RecyclerView;

 import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.PlaylistActivity;
 import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.RoundedImage;
 import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.CountryGridItem;
 import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
 import com.squareup.picasso.Picasso;

 import java.util.List;



public class FirstPageCountriesSliderAdapter extends RecyclerView.Adapter<FirstPageCountriesSliderAdapter.viewHolder> {
    private Activity activity;
    private List<CountryGridItem> aItem;
    public FirstPageCountriesSliderAdapter(Activity activity, List<CountryGridItem> item) {
        this.activity = activity;
        this.aItem=item;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(activity);
        view = mInflater.inflate(R.layout.country_grid_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, final int position) {
        if (aItem != null) {
            if (aItem.get(position) != null) {
                Picasso.with(activity).load(aItem.get(position).getCounrtyFlagUrl()).transform(new RoundedImage()).into(holder.imgThumbnail);
                holder.videoTitle.setText(aItem.get(position).getCountryName());
            }
        }
        holder.relClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PlaylistActivity.class);
                intent.putExtra("contentType","countryTrendVid");
                intent.putExtra("countryCode",aItem.get(position).getCountryCode());
                intent.putExtra("countryFlagUrl",aItem.get(position).getCounrtyFlagUrl());
                intent.putExtra("countryName",aItem.get(position).getCountryName());
                activity.startActivity(intent);
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
        RelativeLayout relClick;

        public viewHolder(View itemView) {
            super(itemView);
            videoTitle = (TextView) itemView.findViewById(R.id.grid_item_country_name) ;
            imgThumbnail = (ImageView) itemView.findViewById(R.id.grid_item_flag_image);
            relClick = (RelativeLayout) itemView.findViewById(R.id.cardview_id);
        }
    }


}

