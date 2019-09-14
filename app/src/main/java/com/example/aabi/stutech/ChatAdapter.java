package com.example.aabi.stutech;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private Context cContext;
    private List<Chat> cData ;

    public ChatAdapter(Context sContext, List<Chat> chatList) {
        this.cContext = sContext;
        this.cData = chatList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View row = LayoutInflater.from(cContext).inflate(R.layout.row_chat_item, viewGroup ,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        Glide.with(cContext).load(cData.get(position).getUserPhoto()).into(holder.userImage);
        holder.userName.setText(cData.get(position).getUserName());
        if(cData.get(position).getDesignation().equals("Teacher")){
            holder.tagTeacher.setVisibility(View.VISIBLE);
        }else{
            holder.tagStudent.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToMessageActivity = new Intent(cContext, MessageActivity.class);
                goToMessageActivity.putExtra("designation",cData.get(position).getDesignation());
                goToMessageActivity.putExtra("userName", cData.get(position).getUserName());
                goToMessageActivity.putExtra("userPhoto", cData.get(position).getUserPhoto());
                goToMessageActivity.putExtra("userId", cData.get(position).getUserId());
                cContext.startActivity(goToMessageActivity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tagTeacher, tagStudent, userName;
        ImageView userImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.chat_user_img);
            tagStudent = itemView.findViewById(R.id.tag_student);
            tagTeacher = itemView.findViewById(R.id.tag_teacher);
            userName = itemView.findViewById(R.id.chat_user_name);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent subjectToHomeActivity = new Intent(sContext, AttendanceActivity.class);
                    int position = getAdapterPosition();

                    //subjectToHomeActivity.putExtra("subjectName",sData.get(position));
                    //Toast.makeText(sContext,sData.get(position),Toast.LENGTH_SHORT).show();
                    subjectToHomeActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    sContext.startActivity(subjectToHomeActivity);
                    //Toast.makeText(sContext,"Get lost",Toast.LENGTH_SHORT).show();

                }
            });*/
        }
    }
}
