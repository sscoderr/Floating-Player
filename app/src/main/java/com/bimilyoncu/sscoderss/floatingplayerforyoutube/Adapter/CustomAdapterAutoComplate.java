package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Item.VideoItem;

import java.util.List;

/**
 * Created by Sabahattin on 29.12.2016.
 */

public class CustomAdapterAutoComplate extends BaseAdapter {
    private int[] imagesId = {R.mipmap.history_button, R.mipmap.search_button};
    private LayoutInflater mInflater;
    private List<VideoItem> searchResults;
    private int count;

    public CustomAdapterAutoComplate(Activity activity, List<VideoItem> list, int count) {
        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.searchResults = list;
        this.count = searchResults.size() - count;
    }

    @Override
    public int getCount() {
        return searchResults.size();
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        final myViewHolder vw;

        if (view == null) {
            view = mInflater.inflate(R.layout.search_item, null);
            vw = new CustomAdapterAutoComplate.myViewHolder();
            vw.image = (ImageView) view.findViewById(R.id.search_autocomplate_image);
            vw.text = (TextView) view.findViewById(R.id.search_autocomplate_text);
            view.setTag(vw);
        } else {
            vw = (CustomAdapterAutoComplate.myViewHolder) view.getTag();
        }
        vw.text.setText(searchResults.get(position).getText());
        if (count > position)
            vw.image.setImageResource(imagesId[0]);
        else
            vw.image.setImageResource(imagesId[1]);
        return view;
    }

    private static class myViewHolder {
        TextView text;
        ImageView image;
    }
}