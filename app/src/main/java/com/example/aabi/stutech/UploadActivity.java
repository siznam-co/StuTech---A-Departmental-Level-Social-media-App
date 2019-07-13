package com.example.aabi.stutech;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

public class UploadActivity extends AppCompatActivity {

    ViewPager viewPager_tab;
    PagerViewAdapter pagerViewAdapter;
    //private SlidrInterface slidrInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.uploadsToolbar);
        setSupportActionBar(toolbar);

        viewPager_tab = (ViewPager) findViewById(R.id.UploadContainer);
        setupViewPager(viewPager_tab);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs) ;
        tabLayout.setupWithViewPager(viewPager_tab);

        //slidrInterface = Slidr.attach(this);
       /* if(slidrInterface = null)
            onBackPressed();*/
            //slidrInterface = Slidr.replace(getView().findViewById(R.id.content_container), new SlidrConfig.Builder().position(SlidrPosition.LEFT).build());
    }

    /*public void lockSlide(View v) {
        slidr.lock();
    }

    public void unlockSlide(View v) {
        slidr.unlock();
    }*/

    private void setupViewPager(ViewPager viewPager) {
        pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        pagerViewAdapter.addFragment(new FragBook(), "BOOKS");
        pagerViewAdapter.addFragment(new FragNotes(), "NOTES");
        pagerViewAdapter.addFragment(new FragMedia(), "MEDIA");
        viewPager.setAdapter(pagerViewAdapter);
    }
    @Override
    public void onBackPressed() {
        if ( getFragmentManager().getBackStackEntryCount() > 0)
        {
            getFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
        finish();
    }
}
