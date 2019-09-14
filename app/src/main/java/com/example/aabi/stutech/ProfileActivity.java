package com.example.aabi.stutech;

import android.content.Intent;
import android.security.keystore.StrongBoxUnavailableException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;


    TextView editName, editRollNo, editSemester, editSection, editSession, tvRollHeading, titleBar;
    TextView designation;
    ImageView imageView, btnEditProfile, btnChatProfile, backButton;
    LinearLayout subjectView, sectionsForTeacher;
    RelativeLayout studentLayout, teacherLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String userID = getIntent().getExtras().getString("userID");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        backButton = findViewById(R.id.back_btn_profile);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        titleBar = findViewById(R.id.title_bar);
        editName = (TextView) findViewById(R.id.profile_name);
        editRollNo = (TextView) findViewById(R.id.profile_roll_no);
        imageView = (ImageView) findViewById(R.id.profile_pic);
        designation = findViewById(R.id.profile_user_designation);
        subjectView = findViewById(R.id.current_user_subjects);
        editSemester = findViewById(R.id.profile_semester);
        sectionsForTeacher = findViewById(R.id.teacher_section);
        editSection = findViewById(R.id.profile_section);
        editSession = findViewById(R.id.profile_session);
        studentLayout = findViewById(R.id.profile_student_layout);
        teacherLayout = findViewById(R.id.profile_teacher_layout);
        tvRollHeading = findViewById(R.id.roll_no_heading_below);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnChatProfile = findViewById(R.id.btn_chat_profile);

        DatabaseReference UserIdReference = FirebaseDatabase.getInstance().getReference("UserIDs").child(userID);

        UserIdReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                designation.setText(user.getDesignation());
                DatabaseReference getDataRef = FirebaseDatabase.getInstance().getReference(user.getDesignation()).child(user.getUserKey());
                getDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(user.getDesignation().equals("Teacher")){
                            teacherLayout.setVisibility(View.VISIBLE);
                            tvRollHeading.setText("Teacher Id# ");
                            final Teacher teacher = dataSnapshot.getValue(Teacher.class);
                            titleBar.setText(teacher.getName());
                            if(currentUser.getUid().equals(userID)){

                                btnEditProfile.setVisibility(View.VISIBLE);
                                btnEditProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent goToProfileEditActivity = new Intent(getApplicationContext(), ProfileEditActivity.class);
                                        goToProfileEditActivity.putExtra("userKey", teacher.getTeacherKey());
                                        goToProfileEditActivity.putExtra("designation", teacher.getDesignation());
                                        startActivity(goToProfileEditActivity);
                                    }
                                });
                                editName.setText(currentUser.getDisplayName());
                                editRollNo.setText(teacher.getTeacherId());
                                editSemester.setText(teacher.getSemester());
                                Glide.with(getApplicationContext()).load(currentUser.getPhotoUrl()).into(imageView);

                            }else{
                                btnChatProfile.setVisibility(View.VISIBLE);
                                btnChatProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent goToMessageActivity = new Intent(getApplicationContext(), MessageActivity.class);
                                        goToMessageActivity.putExtra("userKey", teacher.getTeacherKey());
                                        goToMessageActivity.putExtra("designation", teacher.getDesignation());
                                        goToMessageActivity.putExtra("userName", teacher.getName());
                                        goToMessageActivity.putExtra("userPhoto", teacher.getUserPhoto());
                                        goToMessageActivity.putExtra("userId", teacher.getUserId());
                                        startActivity(goToMessageActivity);
                                    }
                                });
                                editName.setText(teacher.getName());
                                editRollNo.setText(teacher.getTeacherId());
                                editSemester.setText(teacher.getSemester());
                                Glide.with(getApplicationContext()).load(teacher.getUserPhoto()).into(imageView);
                            }

                            LinearLayout.LayoutParams params =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);

                            for(int i = 0; i<teacher.getSubjectList().size(); i++){
                                final TextView tempTV = new TextView(getApplicationContext());
                                tempTV.setText(teacher.getSubjectList().get(i));
                                tempTV.setLayoutParams(params);
                                subjectView.addView(tempTV);
                            }

                            for(int i = 0; i<teacher.getSection().size(); i++){
                                final TextView tempTV = new TextView(getApplicationContext());
                                tempTV.setText(teacher.getSection().get(i));
                                tempTV.setLayoutParams(params);
                                sectionsForTeacher.addView(tempTV);
                            }

                        }else{//If profile is of a student

                            studentLayout.setVisibility(View.VISIBLE);
                            final Student student = dataSnapshot.getValue(Student.class);
                            titleBar.setText(student.getName());
                            if(currentUser.getUid().equals(userID)){
                                btnEditProfile.setVisibility(View.VISIBLE);
                                btnEditProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent goToProfileEditActivity = new Intent(getApplicationContext(), ProfileEditActivity.class);
                                        goToProfileEditActivity.putExtra("userKey", student.getStudentKey());
                                        goToProfileEditActivity.putExtra("designation", student.getDesignation());
                                        startActivity(goToProfileEditActivity);
                                    }
                                });
                                editName.setText(currentUser.getDisplayName());
                                Glide.with(getApplicationContext()).load(currentUser.getPhotoUrl()).into(imageView);
                            }else{
                                btnChatProfile.setVisibility(View.VISIBLE);
                                btnChatProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent goToMessageActivity = new Intent(getApplicationContext(), MessageActivity.class);
                                        goToMessageActivity.putExtra("designation", student.getDesignation());
                                        goToMessageActivity.putExtra("userName", student.getName());
                                        goToMessageActivity.putExtra("userPhoto", student.getUserPhoto());
                                        goToMessageActivity.putExtra("userId", student.getUserId());
                                        startActivity(goToMessageActivity);
                                    }
                                });
                                editName.setText(student.getName());
                                Glide.with(getApplicationContext()).load(student.getUserPhoto()).into(imageView);
                            }


                            editRollNo.setText(student.getRoll());
                            editSemester.setText(student.getSemester());
                            editSession.setText(student.getBatch());
                            editSection.setText(student.getSection());

                            LinearLayout.LayoutParams params =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);

                            for(int i = 0; i<student.getSubjectList().size(); i++){
                                final TextView tempTV = new TextView(getApplicationContext());
                                tempTV.setText(student.getSubjectList().get(i));
                                tempTV.setLayoutParams(params);
                                subjectView.addView(tempTV);

                            }
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
