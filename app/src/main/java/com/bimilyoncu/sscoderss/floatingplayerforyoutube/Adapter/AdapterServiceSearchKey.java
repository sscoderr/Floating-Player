package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item.VideoItem;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;

import java.util.List;

/**
 * Created by Sabahattin on 29.12.2016.
 */

public class AdapterServiceSearchKey extends BaseAdapter {
    private LayoutInflater mInflater;
    private String[] searchResults;

    public AdapterServiceSearchKey(Activity activity, String[] list) {
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.searchResults = list;
    }

    @Override
    public int getCount() {
        return searchResults.length;
    }

    @Override
    public String getItem(int position) {
        return searchResults[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final myViewHolder vw;

        if (view == null) {
            view = mInflater.inflate(R.layout.search_item, null);
            vw = new AdapterServiceSearchKey.myViewHolder();

            vw.image = (ImageView) view.findViewById(R.id.search_autocomplate_image);
            vw.text = (TextView) view.findViewById(R.id.search_autocomplate_text);
            view.setTag(vw);
        } else {
            vw = (AdapterServiceSearchKey.myViewHolder) view.getTag();
        }

        vw.text.setTypeface(Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "VarelaRound-Regular.ttf"));
        vw.text.setText(searchResults[position]);
        vw.image.setImageResource(R.mipmap.search_button);
        return view;
    }

    private static class myViewHolder {
        TextView text;
        ImageView image;
    }
}