package com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.Fragment.MyDateFragment;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.Fragment.TrendMusicFragment;
import com.bimilyoncu.sscoderss.floatingplayerforyoutubev3.Activity.Fragment.TrendVideoFragment;

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
