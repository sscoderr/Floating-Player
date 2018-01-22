package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;

/**
 * Created by furkan0 on 22.1.2018.
 */

public class AdapterSlider extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private android.graphics.drawable.Drawable[] slide_images;
    private String[] slide_headimgs;

    public AdapterSlider(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide1, container, false);

        if (slide_images == null) {
            slide_images = new android.graphics.drawable.Drawable[3];
            slide_images[0] = context.getResources().getDrawable(R.drawable.service_toolbar);
            slide_images[1] = context.getResources().getDrawable(R.drawable.service_controller);
            slide_images[2] = context.getResources().getDrawable(R.drawable.service_key_function);
        }

        if (slide_headimgs == null) {
            slide_headimgs = new String[3];
            slide_headimgs[0] = context.getString(R.string.thisIsThePlayerScreen);
            slide_headimgs[1] = context.getString(R.string.videoControlPanel);
            slide_headimgs[2] = context.getString(R.string.homeAndBackButtonFunction);
        }


        ImageView slideImageView = (ImageView)  view.findViewById(R.id.slide1_imageView);
        TextView slideTextHead = (TextView) view.findViewById(R.id.slide1_textView_head);

        slideTextHead.setTypeface(MSettings.getFontVarelaRound());
        slideTextHead.setText(slide_headimgs[position]);

        slideImageView.setImageDrawable(slide_images[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout) object);

    }
}
