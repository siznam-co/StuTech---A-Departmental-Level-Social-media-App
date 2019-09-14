package com.example.aabi.stutech;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TabLayout tabLayout;
    static String userKey;
    static String designation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userKey = getIntent().getExtras().getString("userKey");
        designation = getIntent().getExtras().getString("designation");

        ViewPager viewPager_tab = (ViewPager) findViewById(R.id.ChatContainer);
        setupViewPager(viewPager_tab);

        tabLayout = (TabLayout) findViewById(R.id.chat_tabs) ;
        tabLayout.setupWithViewPager(viewPager_tab);

    }

    private void setupViewPager(ViewPager viewPager_tab) {

        PagerViewAdapter pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        pagerViewAdapter.addFragment(new ChatFragment(), "Chats");
        pagerViewAdapter.addFragment(new UserFragment(), "Users");

        viewPager_tab.setAdapter(pagerViewAdapter);
        viewPager_tab.setOffscreenPageLimit(1);

    }

    @Override
    public void onBackPressed() {
        Intent subjectIntent = new Intent(ChatActivity.this, HomeActivity.class);
        startActivity(subjectIntent);
        finish();
    }

}
