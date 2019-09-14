package com.example.aabi.stutech;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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


    RecyclerView studentRecyclerView ;
    StudentAdapter studentAdapter ;
    List<Student> studentList = new ArrayList<>();
    Query query;
    TextView lecturesAttended, totalLectures, percentageAttendance;
    Button  doneAttendance;
    LinearLayout  sectionsForTeacher, teacherTotalLectures, marksTypeView, studentMarksView;
    RelativeLayout studentAttendanceView, teacherInitialView, teacherAttendanceView;
    String tempTeacherSubject = "";
    Button marksBtn, atteandanceBtn;
    String section, marksType;
    static String type;
    List<String> marksTypeList = new ArrayList<>();

    static List<Attendance> attendancesList = new ArrayList<>();

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
        marksTypeList.clear();
        marksTypeList.add("Sessional");
        marksTypeList.add("Mid-term");
        marksTypeList.add("Final-term");

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
        marksBtn = findViewById(R.id.marks_btn);
        atteandanceBtn = findViewById(R.id.attendance_btn);
        mAuth = FirebaseAuth.getInstance();
        teacherInitialView = findViewById(R.id.teacher_initial_view);
        teacherTotalLectures = findViewById(R.id.teacher_total_lectures);
        marksTypeView = findViewById(R.id.marks_type);
        studentMarksView = findViewById(R.id.student_marks_view);

        doneAttendance = findViewById(R.id.done_attendance);

        // TODO: set the Attendance for Teacher or Student
        setAttendanceActivity();
      //  getSupportFragmentManager().beginTransaction().replace(R.id.container,new YourSubjectFragment()).commit();
    }

    private void setAttendanceActivity() {
        DatabaseReference UserIdReference = FirebaseDatabase.getInstance().getReference("UserIDs").child(currentUser.getUid());

        final LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        UserIdReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //editRollNo.setText(tempKey);
                if(user.getDesignation().equals("Teacher")){
                    studentAttendanceView.setVisibility(View.GONE);
                    teacherInitialView.setVisibility(View.VISIBLE);

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


                            for(final String sec: teacher.getSection()){
                                DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference("TotalLectures")
                                        .child(AttendanceActivity.subjectName).child(sec);
                                tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final TextView textView = new TextView(getApplicationContext());
                                        textView.setText("Section "+sec+": "+dataSnapshot.getValue(Integer.class));
                                        textView.setLayoutParams(params);
                                        textView.setGravity(Gravity.CENTER_HORIZONTAL);
                                        textView.setBackgroundResource(R.drawable.edittext_button_style_rounded);
                                        params.setMargins(50,5,50,0);
                                        teacherTotalLectures.addView(textView);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            atteandanceBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    sectionsForTeacher.setVisibility(View.VISIBLE);
                                    AttendanceActivity.type = "attend";
                                    //teacherAttendanceView.setVisibility(View.VISIBLE);
                                    teacherInitialView.setVisibility(View.GONE);
                                }
                            });

                            marksBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    sectionsForTeacher.setVisibility(View.VISIBLE);
                                    AttendanceActivity.type = "marks";
                                    //teacherAttendanceView.setVisibility(View.VISIBLE);
                                    teacherInitialView.setVisibility(View.GONE);
                                }
                            });

                            for(int i = 0; i<teacher.getSection().size(); i++){
                                final Button btn = new Button(getApplicationContext());
                                btn.setText(teacher.getSection().get(i));
                                btn.setLayoutParams(params);
                                btn.setBackgroundResource(R.drawable.edittext_button_style_rounded);
                                params.setMargins(50,50,50,5);
                                sectionsForTeacher.addView(btn);
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final String section = btn.getText().toString();
                                        setSection(section);
                                        //Query inside another query--------------------------------------------------
                                        query.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                studentList.clear();
                                                attendancesList.clear();
                                                for (DataSnapshot studentsnap : dataSnapshot.getChildren()) {

                                                    Student student = studentsnap.getValue(Student.class);

                                                    for (String subject : student.getSubjectList()){
                                                        if(subject.equals(tempTeacherSubject) && section.equals(student.getSection())){
                                                            studentList.add(student);
                                                        }
                                                    }

                                                }

                                                if(studentList.isEmpty()){
                                                    showMessage("No Students in "+subjectName+ " Class of Section "+ section);
                                                }else{
                                                    if(AttendanceActivity.type.equals("marks")){
                                                        sectionsForTeacher.setVisibility(View.GONE);
                                                        marksTypeView.setVisibility(View.VISIBLE);
                                                        for(int i = 0; i<marksTypeList.size(); i++){
                                                            final Button btn = new Button(getApplicationContext());
                                                            btn.setText(marksTypeList.get(i));
                                                            btn.setLayoutParams(params);
                                                            btn.setBackgroundResource(R.drawable.edittext_button_style_rounded);
                                                            params.setMargins(50,50,50,5);
                                                            marksTypeView.addView(btn);
                                                            btn.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    setMarksType(btn.getText().toString());
                                                                    teacherAttendanceView.setVisibility(View.VISIBLE);
                                                                    doneAttendance.setText("Add this Mark-Sheet");
                                                                    marksTypeView.setVisibility(View.GONE);
                                                                    studentAdapter = new StudentAdapter(getApplicationContext(), studentList);
                                                                    studentRecyclerView.setAdapter(studentAdapter);
                                                                    studentAdapter.notifyDataSetChanged();
                                                                }
                                                            });
                                                        }
                                                    }else{
                                                        teacherAttendanceView.setVisibility(View.VISIBLE);
                                                        sectionsForTeacher.setVisibility(View.GONE);
                                                        studentAdapter = new StudentAdapter(getApplicationContext(), studentList);
                                                        studentRecyclerView.setAdapter(studentAdapter);
                                                        studentAdapter.notifyDataSetChanged();
                                                    }
                                                }
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
                            showMessage(attendancesList.size()+"");
                            if(attendancesList.size() == studentList.size()) {
                                ProgressBar progressBar = findViewById(R.id.attendance_progress_bar);
                                progressBar.setVisibility(View.VISIBLE);
                                if (AttendanceActivity.type.equals("marks")) {
                                    for (final Attendance attendance : AttendanceActivity.attendancesList){
                                        DatabaseReference markRef = FirebaseDatabase.getInstance()
                                            .getReference("Marks").child(attendance.getUserId())
                                                .child(AttendanceActivity.subjectName).child(getMarksType());
                                        int m = Integer.parseInt(attendance.getMarked());
                                        markRef.setValue(m);
                                    }
                                    showMessage("Marks added Successfully");
                                } else{

                                    final DatabaseReference aReference = FirebaseDatabase.getInstance().getReference("TotalLectures")
                                            .child(AttendanceActivity.subjectName).child(getSection());
                                    aReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Integer totalLectures = dataSnapshot.getValue(Integer.class);
                                                aReference.setValue(++totalLectures);
                                            } else {
                                                aReference.setValue(1);
                                            }

                                            for (final Attendance attendance : AttendanceActivity.attendancesList) {

                                                final DatabaseReference aReference = FirebaseDatabase.getInstance().getReference("Attendance")
                                                        .child(attendance.getUserId()).child(subjectName);

                                                aReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            Integer mark = dataSnapshot.getValue(Integer.class);
                                                            if (attendance.getMarked().equals("P")) {
                                                                if (mark >= 0)
                                                                    ++mark;
                                                            }
                                                            aReference.setValue(mark);
                                                            //addAttendance(mark, attendance.getMarked(), attendance.getUserId());
                                                        } else {
                                                            if (attendance.getMarked().equals("P")) {
                                                                aReference.setValue(1);
                                                            } else {
                                                                aReference.setValue(0);
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });


                                            }

                                            showMessage("Attendance Submitted Successfully");
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            showMessage("Attendance Submission failed! ");
                                        }
                                    });
                                }
                                teacherAttendanceView.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                teacherInitialView.setVisibility(View.VISIBLE);
                            }else{
                                showAlertDialog();
                            }
                        }
                    });

                }else{
                    studentAttendanceView.setVisibility(View.VISIBLE);
                    teacherAttendanceView.setVisibility(View.GONE);
                    teacherInitialView.setVisibility(View.GONE);
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
                                    .child(AttendanceActivity.subjectName).child(HomeActivity.studentSection);
                            final Integer finalObtain = obtain;
                            bReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Integer total = 1;
                                    if(dataSnapshot.exists()){
                                        total = dataSnapshot.getValue(Integer.class);
                                        totalLectures.setText("= "+total);
                                    }else{
                                        totalLectures.setText("= Null");
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

                    DatabaseReference markRef = FirebaseDatabase.getInstance()
                            .getReference("Marks").child(currentUser.getUid())
                            .child(AttendanceActivity.subjectName);
                    markRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    TextView btn = new TextView(getApplicationContext());
                                    btn.setText(snapshot.getKey()+" = "+snapshot.getValue(Integer.class));
                                    btn.setLayoutParams(params);
                                    btn.setGravity(Gravity.CENTER_HORIZONTAL);
                                    btn.setTextSize(20);
                                    btn.setTextColor(Color.parseColor("#2F80ED"));
                                    btn.setBackgroundResource(R.drawable.edittext_button_style_rounded);
                                    params.setMargins(50,5,50,5);
                                    studentMarksView.addView(btn);
                                }
                            }
                            /*for(int i = 0; i<marksTypeList.size(); i++){
                                final Button btn = new Button(getApplicationContext());
                                btn.setText(marksTypeList.get(i));
                                btn.setLayoutParams(params);
                                btn.setBackgroundResource(R.drawable.edittext_button_style_rounded);
                                params.setMargins(50,50,50,5);
                                marksTypeView.addView(btn);
                            }*/
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

    public String getMarksType() {
        return marksType;
    }

    public void setMarksType(String marksType) {
        this.marksType = marksType;
    }

    private void showAlertDialog() {
        final Dialog sureDialog = new Dialog(this);
        sureDialog.setContentView(R.layout.acknowledgement_dialog);
        sureDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sureDialog.getWindow().setLayout(android.widget.Toolbar.LayoutParams.MATCH_PARENT, android.widget.Toolbar.LayoutParams.WRAP_CONTENT);
        sureDialog.getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;

        TextView sureDialogText = sureDialog.findViewById(R.id.sure_dialog_text);
        sureDialogText.setText(R.string.attendance_alert_message);

        Button sureDelete = sureDialog.findViewById(R.id.sure_delete);
        Button cancelDelete =  sureDialog.findViewById(R.id.cancel_delete);

        sureDialog.show();

        sureDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacherAttendanceView.setVisibility(View.GONE);
                teacherInitialView.setVisibility(View.VISIBLE);
                sureDialog.hide();
            }
        });
        cancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sureDialog.hide();
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

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
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
