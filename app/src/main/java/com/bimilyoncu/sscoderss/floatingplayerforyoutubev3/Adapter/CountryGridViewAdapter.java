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

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.RoundedImage;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.CountryGridItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CountryGridViewAdapter extends BaseAdapter {
    private Activity activity;
    private List<CountryGridItem> aItem;
    private LayoutInflater mInflater;
    public CountryGridViewAdapter(Activity activity, List<CountryGridItem> item) {
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.aItem=item;
    }
    @Override
    public View getView(final int position, View rowView, ViewGroup parent) {
        final ViewHolder holder;
        if (rowView == null) {
            rowView = mInflater.inflate(R.layout.country_grid_item, null);
            holder = new ViewHolder();
            holder.countryName = (TextView) rowView.findViewById(R.id.grid_item_country_name);
            holder.flagImage = (ImageView) rowView.findViewById(R.id.grid_item_flag_image);
            Typeface myTypeface = Typeface.createFromAsset(activity.getAssets(), "VarelaRound-Regular.ttf");
            holder.countryName.setTypeface(myTypeface);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }
        if (aItem != null) {
            if (aItem.get(position) != null) {
                Picasso.with(activity).load(aItem.get(position).getCounrtyFlagUrl()).transform(new RoundedImage()).into(holder.flagImage);
                holder.countryName.setText(aItem.get(position).getCountryName());
            }
        }
        return rowView;
    }
    @Override
    public int getCount() {
        return aItem != null ? aItem.size() : 0;
    }

    @Override
    public CountryGridItem getItem(int position) {
        return aItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private static class ViewHolder {
        TextView countryName;
        ImageView flagImage;
    }
}
/*import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.RoundedImage;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Item.CountryGridItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.squareup.picasso.Picasso;

import java.util.List;



public class CountryGridViewAdapter extends RecyclerView.Adapter<CountryGridViewAdapter.viewHolder> {


    private Activity activity;
    private List<CountryGridItem> aItem;
    public CountryGridViewAdapter(Activity activity, List<CountryGridItem> item) {
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
        //holder.cardView.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {

            //}
        //});



    }

    @Override
    public int getItemCount() {
        return aItem.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {

        TextView videoTitle;
        ImageView imgThumbnail;
        //CardView cardView ;

        public viewHolder(View itemView) {
            super(itemView);

            videoTitle = (TextView) itemView.findViewById(R.id.grid_item_country_name) ;
            imgThumbnail = (ImageView) itemView.findViewById(R.id.grid_item_flag_image);
            //cardView = (CardView) itemView.findViewById(R.id.cardview_id);


        }
    }


}*/
