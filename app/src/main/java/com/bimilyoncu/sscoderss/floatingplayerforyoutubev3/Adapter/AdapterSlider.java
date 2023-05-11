package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;
import com.squareup.picasso.Picasso;

/**
 * Created by furkan0 on 22.1.2018.
 */

public class AdapterSlider extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private Integer[] slide_images;
    private String[] slide_headimgs;

    public AdapterSlider(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return 1;
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
            slide_images = new Integer[1];
            slide_images[0] = R.drawable.download;
        }

        if (slide_headimgs == null) {
            slide_headimgs = new String[1];
            slide_headimgs[0] = context.getString(R.string.ssplayertitle);
        }


        ImageView slideImageView = (ImageView)  view.findViewById(R.id.slide1_imageView);
        TextView slideTextHead = (TextView) view.findViewById(R.id.slide1_textView_head);

        slideTextHead.setTypeface(MSettings.getFontVarelaRound());
        slideTextHead.setText(slide_headimgs[position]);
        Intent intent = ((Activity)context).getIntent();
        final String uri = intent.getStringExtra("uri");
        slideImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebsite(uri,MSettings.activeActivity);
            }
        });
        /*Glide.with(MSettings.activeActivity)
                .load(slide_images[position])
                .into(slideImageView);*/
        Picasso.with(MSettings.activeActivity).load(slide_images[position]).into(slideImageView);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout) object);

    }
    private void openWebsite(String url, Context context) {
        if(url!=null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
    }
}
