package com.bimilyoncu.sscoderss.floatingplayerforyoutube.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Page.Fragment.MyDateFragment;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Page.Fragment.TrendMusicFragment;
import com.bimilyoncu.sscoderss.floatingplayerforyoutube.Page.Fragment.TrendVideoFragment;

/**
 * Created by Sabahattin on 1/9/2016.
 */
public class CustomAdapterForFragments extends FragmentPagerAdapter {

    //private String fragments[]={"Trend Music","Trend Video","My Favorite"};

    public CustomAdapterForFragments(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new TrendMusicFragment();
            case 1:
                return new TrendVideoFragment();
            case 2:
                return new MyDateFragment();
            default :
               return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    /*@Override
    public CharSequence getPageTitle(int position) {
        return fragments[position];
    }*/
}
