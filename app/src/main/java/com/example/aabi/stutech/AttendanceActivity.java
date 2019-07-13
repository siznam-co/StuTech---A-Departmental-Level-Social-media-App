package com.example.aabi.stutech;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity{

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    static String subjectName;
    DatabaseReference databaseReference;

    RecyclerView studentRecyclerView ;
    StudentAdapter studentAdapter ;
    FirebaseDatabase firebaseDatabase;
    List<Student> studentList = new ArrayList<>();
    Query query;
    TextView lecturesAttended, totalLectures, percentageAttendance;
    Button addNewAttendance, doneAttendance;
    LinearLayout teacherAttendanceView , sectionsForTeacher;
    RelativeLayout studentAttendanceView;
    String tempTeacherSubject = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

       /// getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        //iniPopup();
        subjectName = getIntent().getExtras().getString("subjectName");

        // TeacherSubject();

        Toolbar toolbar = (Toolbar) findViewById(R.id.subject_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(subjectName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        studentRecyclerView  = findViewById(R.id.attendanceRV);
        lecturesAttended = findViewById(R.id.lectures_attended);
        totalLectures = findViewById(R.id.total_lectures);
        percentageAttendance = findViewById(R.id.attendance_in_percentage);
        studentAttendanceView = findViewById(R.id.student_attendance_view);
        teacherAttendanceView = findViewById(R.id.teacher_attencedance_view);
        sectionsForTeacher = findViewById(R.id.sections_for_teacher);
        studentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        studentRecyclerView.setHasFixedSize(true);
        mAuth = FirebaseAuth.getInstance();

        addNewAttendance = findViewById(R.id.add_new_attendance);
        doneAttendance = findViewById(R.id.done_attendance);

        // TODO: set the Attendance for Teacher or Student
        setAttendanceActivity();
      //  getSupportFragmentManager().beginTransaction().replace(R.id.container,new YourSubjectFragment()).commit();
    }

    private void setAttendanceActivity() {
        DatabaseReference UserIdReference = FirebaseDatabase.getInstance().getReference("UserIDs").child(currentUser.getUid());

        UserIdReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //editRollNo.setText(tempKey);
                if(user.getDesignation().equals("Teacher")){
                    studentAttendanceView.setVisibility(View.GONE);
                    addNewAttendance.setVisibility(View.VISIBLE);
                    addNewAttendance.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sectionsForTeacher.setVisibility(View.VISIBLE);
                            //teacherAttendanceView.setVisibility(View.VISIBLE);
                            addNewAttendance.setVisibility(View.GONE);
                        }
                    });
                    query = FirebaseDatabase.getInstance().getReference("Student");
                    DatabaseReference teacherReference = FirebaseDatabase.getInstance()
                            .getReference("Teacher").child(user.getUserKey());


                    teacherReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            final Teacher teacher = dataSnapshot.getValue(Teacher.class);

                            for (String data : teacher.getSubjectList()){
                                if(data.equals(AttendanceActivity.subjectName)){
                                    tempTeacherSubject = data;
                                }
                            }

                            LinearLayout.LayoutParams params =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);

                            for(int i = 0; i<teacher.getSection().size(); i++){
                                final Button btn = new Button(getApplicationContext());
                                btn.setText(teacher.getSection().get(i));
                                btn.setLayoutParams(params);
                                sectionsForTeacher.addView(btn);
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        teacherAttendanceView.setVisibility(View.VISIBLE);
                                        sectionsForTeacher.setVisibility(View.GONE);
                                        final String section = btn.getText().toString();
                                        //Query inside another query--------------------------------------------------
                                        query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                studentList.clear();
                                                for (DataSnapshot studentsnap : dataSnapshot.getChildren()) {

                                                    Student student = studentsnap.getValue(Student.class);

                                                    for (String subject : student.getSubjectList()){
                                                        if(subject.equals(tempTeacherSubject) && section.equals(student.getSection())){
                                                            studentList.add(student);
                                                        }
                                                    }

                                                }


                                                studentAdapter = new StudentAdapter(getApplicationContext(), studentList);
                                                studentRecyclerView.setAdapter(studentAdapter);
                                                studentAdapter.notifyDataSetChanged();


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    doneAttendance.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final DatabaseReference aReference = FirebaseDatabase.getInstance().getReference("TotalLectures")
                                    .child(AttendanceActivity.subjectName);
                            aReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        Integer totalLectures = dataSnapshot.getValue(Integer.class);
                                        aReference.setValue(++totalLectures);
                                        showMessage("Attendance Submitted Successfully");
                                    }else{
                                        aReference.setValue(1);
                                        showMessage("Attendance Submitted Successfully");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    showMessage("Attendance Submission failed! ");
                                }
                            });
                            teacherAttendanceView.setVisibility(View.GONE);
                            addNewAttendance.setVisibility(View.VISIBLE);
                        }
                    });

                }else{
                    studentAttendanceView.setVisibility(View.VISIBLE);
                    teacherAttendanceView.setVisibility(View.GONE);
                    addNewAttendance.setVisibility(View.GONE);
                    DatabaseReference aReference = FirebaseDatabase.getInstance().getReference("Attendance")
                            .child(currentUser.getUid()).child(AttendanceActivity.subjectName);

                    aReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Integer obtain = 1;
                            if(dataSnapshot.exists()){
                                obtain = dataSnapshot.getValue(Integer.class);
                                lecturesAttended.setText("= "+obtain);
                            }else{
                                lecturesAttended.setText("= 0");
                            }
                            DatabaseReference bReference = FirebaseDatabase.getInstance().getReference("TotalLectures")
                                    .child(AttendanceActivity.subjectName);
                            final Integer finalObtain = obtain;
                            bReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Integer total = 1;
                                    if(dataSnapshot.exists()){
                                        total = dataSnapshot.getValue(Integer.class);
                                        totalLectures.setText("= "+total);
                                    }else{
                                        totalLectures.setText("= 0");
                                    }
                                    setPercentage(finalObtain,total);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setPercentage(Integer finalObtain, Integer total) {
        int percent = (int) ((float) finalObtain /total*100);
        if(percent<80)
            percentageAttendance.setTextColor(getResources().getColor(R.color.color_red));
        percentageAttendance.setText("= "+percent+"%");
    }

    private void showMessage(String message) {
        Toast.makeText(AttendanceActivity.this,message,Toast.LENGTH_LONG).show();

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
        Intent j = new Intent(AttendanceActivity.this, HomeActivity.class);
        startActivity(j);*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
