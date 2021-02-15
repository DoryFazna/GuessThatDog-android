package com.example.mobilecoursework;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class scoreDetails extends AppCompatActivity {
    Toolbar mToolBar;
    TabLayout mTabLayout;
    TabItem level1Scores;
    TabItem level2Scores;
    ViewPager mPager;
    PagerCotroller mPageController;

    TextView userName;
    static long level1HighScore;
    static long level2HighScore;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_details);
        mToolBar = findViewById(R.id.toolBar);

        userName = findViewById(R.id.userName);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Tab 1");

        mTabLayout = findViewById(R.id.tabLayout);
        level1Scores=findViewById(R.id.level1Scores);
        level2Scores=findViewById(R.id.level2Scores);
        mPager=findViewById(R.id.viewPager);
        mPageController = new PagerCotroller(getSupportFragmentManager(),mTabLayout.getTabCount());
        mPager.setAdapter(mPageController);

        userName.setText(loginSignup.getUserId());

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));


    }


    public void backToMain(View view) {
        finish();
    }
}
