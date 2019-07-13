package com.example.aabi.stutech;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
public class SubjectActivity extends AppCompatActivity{

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    static String subjectName;
    ViewPager viewPager_tab;
    PagerViewAdapter pagerViewAdapter;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

       /// getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        //iniPopup();

        subjectName = getIntent().getExtras().getString("subjectName");
        // TeacherSubject();

        Toolbar toolbar = (Toolbar) findViewById(R.id.subject_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(subjectName);

        viewPager_tab = (ViewPager) findViewById(R.id.Container);
        setupViewPager(viewPager_tab, subjectName);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.SubjectActivityTabs) ;
        tabLayout.setupWithViewPager(viewPager_tab);
        // set the home fragment as the default one

      //  getSupportFragmentManager().beginTransaction().replace(R.id.container,new YourSubjectFragment()).commit();
    }

    private void setupViewPager(ViewPager viewPager_tab, String sub) {

        pagerViewAdapter = new PagerViewAdapter(getSupportFragmentManager());
        pagerViewAdapter.addFragment(new YourSubjectFragment(), "FEEDS");
        if(!(sub.equals("all") || sub.equals("announce")))
            pagerViewAdapter.addFragment(new AttendanceFragment(), "ATTENDANCE");
        viewPager_tab.setAdapter(pagerViewAdapter);
        viewPager_tab.setOffscreenPageLimit(2);

    }

    private void showMessage(String message) {
        Toast.makeText(SubjectActivity.this,message,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressed() {
        if ( getFragmentManager().getBackStackEntryCount() > 0)
        {
            getFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
        /*super.onBackPressed();
        Intent j = new Intent(SubjectActivity.this, HomeActivity.class);
        startActivity(j);*/
    }

}
