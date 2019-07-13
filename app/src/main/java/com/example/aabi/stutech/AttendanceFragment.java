package com.example.aabi.stutech;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class AttendanceFragment extends Fragment {

    RecyclerView studentRecyclerView ;
    StudentAdapter studentAdapter ;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference ;
    List<Student> studentList = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    Query query;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TextView lecturesAttended, totalLectures, percentageAttendance;
    Button addNewAttendance, doneAttendance;
    LinearLayout teacherAttendanceView , sectionsForTeacher;
    RelativeLayout studentAttendanceView;
    String tempTeacherSubject = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_attendance, container, false);
        studentRecyclerView  = fragmentView.findViewById(R.id.attendanceRV);
        lecturesAttended = fragmentView.findViewById(R.id.lectures_attended);
        totalLectures = fragmentView.findViewById(R.id.total_lectures);
        percentageAttendance = fragmentView.findViewById(R.id.attendance_in_percentage);
        studentAttendanceView = fragmentView.findViewById(R.id.student_attendance_view);
        teacherAttendanceView = fragmentView.findViewById(R.id.teacher_attencedance_view);
        sectionsForTeacher = fragmentView.findViewById(R.id.sections_for_teacher);
        studentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        studentRecyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Students");
        mAuth = FirebaseAuth.getInstance();

        addNewAttendance = fragmentView.findViewById(R.id.add_new_attendance);
        doneAttendance = fragmentView.findViewById(R.id.done_attendance);

        return fragmentView ;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get List Student from the database
        currentUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference UserIdReference = databaseReference.child("UserIDs").child(currentUser.getUid());

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
                                    if(data.equals(SubjectActivity.subjectName)){
                                        tempTeacherSubject = data;
                                    }
                                }

                                LinearLayout.LayoutParams params =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);

                            for(int i = 0; i<teacher.getSection().size(); i++){
                                    final Button btn = new Button(getContext());
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


                                                    studentAdapter = new StudentAdapter(getActivity(), studentList);
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
                                    .child(SubjectActivity.subjectName);
                            aReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        Integer totalLectures = dataSnapshot.getValue(Integer.class);
                                        aReference.setValue(++totalLectures);
                                    }else{
                                        aReference.setValue(1);
                                    }
                                    Toast.makeText(getActivity(),"Attendance Submitted Successfully", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

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
                            .child(currentUser.getUid()).child(SubjectActivity.subjectName);

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
                                    .child(SubjectActivity.subjectName);
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


}
