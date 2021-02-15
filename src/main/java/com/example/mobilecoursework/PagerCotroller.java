package com.example.mobilecoursework;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PagerCotroller extends FragmentPagerAdapter {
    int tabCounts;
    public PagerCotroller(FragmentManager fm, int tabCounts) {
        super(fm);
        this.tabCounts = tabCounts;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new level1ScoreDetails();
            case 1:
                return new level2ScoreDetails();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
