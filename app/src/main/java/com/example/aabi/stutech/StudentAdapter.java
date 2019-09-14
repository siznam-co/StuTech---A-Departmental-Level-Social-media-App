package com.example.aabi.stutech;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder> {

        private Context aContext;
        private List<Student> aData ;
        private Attendance aAttendance;


    public StudentAdapter(Context aContext, List<Student> aData) {
        this.aContext = aContext;
        this.aData = aData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(AttendanceActivity.type.equals("marks")){
            View row = LayoutInflater.from(aContext).inflate(R.layout.row_marks_item,parent,false);
            return new MyViewHolder(row);
        }else{
            View row = LayoutInflater.from(aContext).inflate(R.layout.row_student_item,parent,false);
            return new MyViewHolder(row);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final String roll = aData.get(position).getRoll() + "-BSCS-"+ aData.get(position).getBatch()+"-" + aData.get(position).getSection();

        holder.tvTitle.setText(aData.get(position).getName());
        holder.rollNo.setText(roll);

        Glide.with(aContext).load(aData.get(position).getUserPhoto()).into(holder.imgStudentProfile);

        if(AttendanceActivity.type.equals("marks")){
            holder.numberPicker.setMinValue(0);
            holder.numberPicker.setMaxValue(100);
            /*holder.numberPicker.setFormatter(new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.format("%02d",value);
                }
            });*/
            holder.numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    aAttendance = new Attendance(aData.get(position).getUserId(), newVal+"");
                    setaAttendanceForThis(aAttendance);
                }
            });
            /*if(!holder.editTextMarks.getText().toString().isEmpty()){
                aAttendance = new Attendance(aData.get(position).getUserId(), holder.editTextMarks.getText().toString());
                setaAttendanceForThis(aAttendance);
            }*/
        }else{
            holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    holder.radioButton = group.findViewById(checkedId);
                    final String marked =  holder.radioButton.getText().toString().trim();


                    aAttendance = new Attendance(aData.get(position).getUserId(), marked);
                    setaAttendanceForThis(aAttendance);
                }
            });
        }

    }

    private void setaAttendanceForThis(Attendance AttendanceItem) {
        if(AttendanceActivity.attendancesList.isEmpty()){
            AttendanceActivity.attendancesList.add(AttendanceItem);
            //Toast.makeText(aContext, AttendanceActivity.attendancesList.size()+"", Toast.LENGTH_SHORT).show();
        }else{
            if(!isAlreadyThere(AttendanceItem)){
                AttendanceActivity.attendancesList.add(AttendanceItem);
                //Toast.makeText(aContext, AttendanceActivity.attendancesList.size()+"", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private boolean isAlreadyThere(Attendance attendanceItem) {
        boolean isThere = false;
        for(Attendance attendance: AttendanceActivity.attendancesList){
            if(attendance.getUserId().equals(attendanceItem.getUserId())){
                attendance.setMarked(attendanceItem.getMarked());
                //Toast.makeText(aContext, AttendanceActivity.attendancesList.size()+"", Toast.LENGTH_SHORT).show();
                isThere = true;
            }
        }
        return isThere;
    }



    @Override
    public int getItemCount() {
        return aData.size();
    }

    //TODO View Holoder class for finding objects from row_student_items
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView imgStudentProfile;
        RadioGroup radioGroup;
        RadioButton radioButton;
        TextView rollNo;
        NumberPicker numberPicker;
        //EditText editTextMarks;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_student_title);
            imgStudentProfile = itemView.findViewById(R.id.row_student_profile_img);
            radioGroup = itemView.findViewById(R.id.markAttendance);
            rollNo = itemView.findViewById(R.id.attendance_roll_no);
            numberPicker = itemView.findViewById(R.id.marks);
            //editTextMarks = itemView.findViewById(R.id.student_marks);
        }
    }
}
