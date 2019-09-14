package com.example.aabi.stutech;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private Context cContext;
    private List<Comment> cData ;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.cContext = context;
        this.cData = commentList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(cContext).inflate(R.layout.row_comment_item, viewGroup ,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.tvUserCommentName.setText(cData.get(position).getUname());
        myViewHolder.tvUserComment.setText(cData.get(position).getContent());
        long timestamp  = (long) cData.get(position).getTimestamp();
        myViewHolder.tvCommentDate.setText(timestampToString(timestamp));
        Glide.with(cContext).load(cData.get(position).getUimg()).into(myViewHolder.imgCurrentUserCommented);
    }

    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String tempMonth = DateFormat.format("MM",calendar).toString();
        switch (tempMonth){
            case "01":
                tempMonth = "January";
                break;
            case "02":
                tempMonth = "February";
                break;
            case "03":
                tempMonth = "March";
                break;
            case "04":
                tempMonth = "April";
                break;
            case "05":
                tempMonth = "May";
                break;
            case "06":
                tempMonth = "June";
                break;
            case "07":
                tempMonth = "July";
                break;
            case "08":
                tempMonth = "August";
                break;
            case "09":
                tempMonth = "September";
                break;
            case "10":
                tempMonth = "October";
                break;
            case "11":
                tempMonth = "November";
                break;
            case "12":
                tempMonth = "December";
                break;

        }
        String timing = " AM";

        int hour = Integer.parseInt(DateFormat.format("HH",calendar).toString());
        if(hour>12){
            hour = hour - 12;
            timing = " PM";
        }

        String date = DateFormat.format("dd",calendar).toString() + " " +
                tempMonth+ " at "+ hour + DateFormat.format(":mm",calendar).toString() + timing;
        return date;


    }

    @Override
    public int getItemCount() {
        return cData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserCommentName, tvUserComment, tvCommentDate;
        CircleImageView imgCurrentUserCommented;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUserCommentName = itemView.findViewById(R.id.user_commented_id);
            tvUserComment = itemView.findViewById(R.id.user_comment);
            imgCurrentUserCommented = itemView.findViewById(R.id.user_commented_image);
            tvCommentDate = itemView.findViewById(R.id.comment_date);
        }
    }
}
