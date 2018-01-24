package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter.AdapterSlider;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Custom.MSettings;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.R;

import org.w3c.dom.Text;

public class SlideOnboardingActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout linearLayoutTab, linearLayoutSlideDot;

    private AdapterSlider adapterSlider;

//    private TextView /*textViewBack,*/ textViewNext;
    private Button buttonBack, buttonNext;
    private TextView[] textViewsDots;

    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_onboarding);

        getSupportActionBar().hide();

        viewPager = (ViewPager) findViewById(R.id.slide_viewPager);
        linearLayoutTab = (LinearLayout) findViewById(R.id.slide_linearTab);
        linearLayoutSlideDot = (LinearLayout) findViewById(R.id.slide_linearDot);

        adapterSlider = new AdapterSlider(this);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapterSlider);

//        textViewBack = (TextView) findViewById(R.id.activitySlide_textBack);
//        textViewNext = (TextView) findViewById(R.id.activitySlide_textNext);
        buttonBack = (Button) findViewById(R.id.activitySlide_buttonBack);
        buttonNext = (Button) findViewById(R.id.activitySlide_buttonNext);


//        textViewBack.setTypeface(MSettings.getFontVarelaRound());
//        textViewNext.setTypeface(MSettings.getFontVarelaRound());
        buttonBack.setTypeface(MSettings.getFontVarelaRound());
        buttonNext.setTypeface(MSettings.getFontVarelaRound());

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(currentPage - 1);
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage < 2) {
                    viewPager.setCurrentItem(currentPage + 1);
                } else {
                    finish();
                }
            }
        });

        addDotsIndicator(0);

        viewPager.addOnPageChangeListener(viewListener);
    }

    private void addDotsIndicator(int position){

        textViewsDots = new TextView[3];
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
            currentPage = position;

            if(position == 0){
                buttonBack.setVisibility(View.GONE);
                buttonNext.setText(getString(R.string.next));
            } else if (position == 1) {
                buttonBack.setVisibility(View.VISIBLE);
                buttonBack.setText(getString(R.string.back));
                buttonNext.setText(getString(R.string.next));
            } else if (position == 2) {
                buttonBack.setVisibility(View.VISIBLE);
                buttonBack.setText(getString(R.string.back));
                buttonNext.setText(getString(R.string.finish));
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed() {

    }
}
