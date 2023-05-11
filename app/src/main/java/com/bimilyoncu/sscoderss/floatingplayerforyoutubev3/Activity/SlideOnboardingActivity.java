package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter.AdapterSlider;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.R;


public class SlideOnboardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout  linearLayoutSlideDot;

    private AdapterSlider adapterSlider;

    private Button buttonBack, buttonNext;
    private TextView[] textViewsDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_onboarding);
        MSettings.activeActivity = SlideOnboardingActivity.this;
        getSupportActionBar().hide();
        viewPager = (ViewPager) findViewById(R.id.slide_viewPager);
        linearLayoutSlideDot = (LinearLayout) findViewById(R.id.slide_linearDot);
        Typeface font1 = Typeface.createFromAsset(MSettings.activeActivity.getAssets(), "VarelaRound-Regular.ttf");
        adapterSlider = new AdapterSlider(this);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapterSlider);
        buttonBack = (Button) findViewById(R.id.activitySlide_buttonBack);
        buttonNext = (Button) findViewById(R.id.activitySlide_buttonNext);
        buttonBack.setTypeface(font1);
        buttonNext.setTypeface(font1);
        Intent intent = getIntent();
        final String uri = intent.getStringExtra("uri");
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebsite(uri, MSettings.activeActivity);
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebsite(uri, MSettings.activeActivity);
            }
        });
        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);
    }
    private void openWebsite(String url, Context context) {
        if (url!=null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
    }

    private void addDotsIndicator(int position){

        textViewsDots = new TextView[1];
        linearLayoutSlideDot.removeAllViews();

        for (int i=0; i < textViewsDots.length; i++) {

            textViewsDots[i] = new TextView(this);
            textViewsDots[i].setText(Html.fromHtml("&#8226;"));
            textViewsDots[i].setTextSize(35);
            textViewsDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            linearLayoutSlideDot.addView(textViewsDots[i]);

        }

        if (textViewsDots.length > 0) {
            textViewsDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed() {

    }
}
