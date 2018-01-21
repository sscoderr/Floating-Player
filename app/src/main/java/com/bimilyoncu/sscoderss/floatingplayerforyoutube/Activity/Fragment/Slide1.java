package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Activity.Fragment;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

/**
 * Created by furkan0 on 21.1.2018.
 */

public class Slide1 extends Fragment implements ISlideBackgroundColorHolder {
    @Override
    public int getDefaultBackgroundColor() {
        // Return the default background color of the slide.
        return Color.parseColor("#FFF");
    }

    @Override
    public void setBackgroundColor(@ColorInt int backgroundColor) {
        // Set the background color of the view within your slide to which the transition should be applied.
//        if (layoutContainer != null) {
//            layoutContainer.setBackgroundColor(backgroundColor);
//        }
    }
}
