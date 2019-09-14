package com.example.aabi.stutech;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeActivity extends AppCompatActivity{

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TabLayout tabLayout;
    static String studentSection, designation, userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewPager viewPager_tab = (ViewPager) findViewById(R.id.HomeContainer);
        setupViewPager(viewPager_tab);

        tabLayout = (TabLayout) findViewById(R.id.HomeTabs) ;
        tabLayout.setupWithViewPager(viewPager_tab);
        setupTabIcons();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newRef = databaseReference.child("UserIDs").child(currentUser.getUid());

        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //editRollNo.setText(tempKey);
                HomeActivity.designation = user.getDesignation();
                HomeActivity.userKey = user.getUserKey();
                if(user.getDesignation().equals("Student")){
                    final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                         .getReference(user.getDesignation()).child(user.getUserKey()).child("section");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            HomeActivity.studentSection = dataSnapshot.getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


                //do what you want with the email
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int[] tabIcons = {
            R.drawable.ic_event_note_black_24dp,
            R.drawable.announce,
            R.drawable.ic_library_books_black_24dp,
            R.drawable.ic_notifications_black_24dp,
            R.drawable.ic_menu_black_24dp
    };

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
        tabLayout.getTabAt(4).setIcon(tabIcons[4]);
    }

    private void setupViewPager(ViewPager viewPager_tab) {

        ViewPagerAdapter pagerViewAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerViewAdapter.addFragment(new HomeFragment());
        pagerViewAdapter.addFragment(new AnnounceFragment());
        pagerViewAdapter.addFragment(new SubjectFragment());
        pagerViewAdapter.addFragment(new NotificationFragment());
        pagerViewAdapter.addFragment(new MenuFragment());
        /*pagerViewAdapter.addFragment(new HomeFragment(), "FEEDS");
        pagerViewAdapter.addFragment(new AnnounceFragment(), "Announcement");
        pagerViewAdapter.addFragment(new SubjectFragment(), "SUBJECTS");
        pagerViewAdapter.addFragment(new NotificationFragment(), "Notifications");
        pagerViewAdapter.addFragment(new MenuFragment(), "menu");*/
        viewPager_tab.setAdapter(pagerViewAdapter);
        viewPager_tab.setOffscreenPageLimit(0);

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                this.moveTaskToBack(true);
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
            //=-----------------
        }
    }

}
