package com.example.aabi.stutech;

import android.os.Handler;
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

public class HomeActivity extends AppCompatActivity{

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //filterPopup();
        /*String notificationIntent = getIntent().getStringExtra("notify");

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // If menuFragment is defined, then this activity was launched with a fragment selection
        if (notificationIntent != null) {

            // Here we can decide what do to -- perhaps load other parameters from the intent extras such as IDs, etc
            if (notificationIntent.equals("newNotification")) {
                NotificationFragment notificationFragment = new NotificationFragment();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(android.R.id.content, notificationFragment);
            }
        } else {
            // Activity was not launched with a menuFragment selected -- continue as if this activity was opened from a launcher (for example)
            HomeFragment homeFragment = new HomeFragment();
            fragmentTransaction.replace(android.R.id.content, homeFragment);
        }*/

        ViewPager viewPager_tab = (ViewPager) findViewById(R.id.HomeContainer);
        setupViewPager(viewPager_tab);

        tabLayout = (TabLayout) findViewById(R.id.HomeTabs) ;
        tabLayout.setupWithViewPager(viewPager_tab);
        setupTabIcons();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
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
