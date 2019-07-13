package com.example.aabi.stutech;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        private Integer mark = 0;


    public StudentAdapter(Context aContext, List<Student> aData) {
        this.aContext = aContext;
        this.aData = aData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(aContext).inflate(R.layout.row_student_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final String title = aData.get(position).getName() + " (" + aData.get(position).getRoll() + ")";
        holder.tvTitle.setText(title);
        Glide.with(aContext).load(aData.get(position).getUserPhoto()).into(holder.imgStudentProfile);
        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                holder.radioButton = group.findViewById(checkedId);
                final String marked =  holder.radioButton.getText().toString().trim();


                //aAttendance = new Attendance(AttendanceActivity.subjectName, marked);

                final DatabaseReference aReference = FirebaseDatabase.getInstance().getReference("Attendance")
                        .child(aData.get(position).getUserId()).child(AttendanceActivity.subjectName);

                aReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            mark = dataSnapshot.getValue(Integer.class);
                            addAttendance(mark, marked, aData.get(position).getUserId(), holder, position);
                        }else{
                            addAttendance(0, marked, aData.get(position).getUserId(), holder, position);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });

    }

    private void addAttendance(Integer mark, final String marked, final String userId, final MyViewHolder holder, final int position) {
        final DatabaseReference attendanceReference = FirebaseDatabase.getInstance().getReference("Attendance")
                .child(userId).child(AttendanceActivity.subjectName);


        if(marked.equals("P")){
            if(mark>=0)
                ++mark;
        }

        final int finalMark = mark;

        holder.btnSingleStundentAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendanceReference.setValue(finalMark).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(aContext, aData.get(position).getName() + " = " + marked, Toast.LENGTH_SHORT).show();
                        holder.btnSingleStundentAttendanceSubmitted.setVisibility(View.VISIBLE);
                        holder.btnSingleStundentAttendance.setVisibility(View.GONE);
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return aData.size();
    }

    //TODO View Holoder class for finding objects from row_student_items
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        ImageView imgStudentProfile, btnSingleStundentAttendance, btnSingleStundentAttendanceSubmitted;
        RadioGroup radioGroup;
        RadioButton radioButton;


        public MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_student_title);
            imgStudentProfile = itemView.findViewById(R.id.row_student_profile_img);
            radioGroup = itemView.findViewById(R.id.markAttendance);
            btnSingleStundentAttendance = itemView.findViewById(R.id.submit_single_attendance);
            btnSingleStundentAttendanceSubmitted = itemView.findViewById(R.id.grey_submit_single_attendance);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    *//*Intent studentDetailActivity = new Intent(aContext,studentDetailActivity.class);
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("title",aData.get(position).getTitle());
                    postDetailActivity.putExtra("postImage",aData.get(position).getPicture());
                    postDetailActivity.putExtra("description",aData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey",aData.get(position).getPostKey());
                    postDetailActivity.putExtra("userPhoto",aData.get(position).getUserPhoto());
                    // will fix this later i forgot to add user name to post object
                    //postDetailActivity.putExtra("userName",aData.get(position).getUsername);
                    long timestamp  = (long) aData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate",timestamp) ;
                    aContext.startActivity(postDetailActivity);*//*
                    Toast.makeText(aContext,"Get lost",Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }
}
