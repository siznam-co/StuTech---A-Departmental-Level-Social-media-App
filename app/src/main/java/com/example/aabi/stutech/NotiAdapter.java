package com.example.aabi.stutech;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;

import android.text.SpannableStringBuilder;
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

public class NotiAdapter extends RecyclerView.Adapter<NotiAdapter.MyViewHolder> {
    private List<Notifications> nData;
    private Context nContext;


    public NotiAdapter(FragmentActivity nContext, List<Notifications> nData) {
        this.nData = nData;
        this.nContext = nContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(nContext).inflate(R.layout.row_notification_item,viewGroup,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull NotiAdapter.MyViewHolder myViewHolder, final int position) {

        /*Query query = FirebaseDatabase.getInstance().getReference("Posts").child(nData.get(position).getRef()).child("subjectName");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String subject = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        Glide.with(nContext).load(nData.get(position).getUserPhoto()).into(myViewHolder.imgUserPic);

        String sourceString = nData.get(position).getUser();
        String subjectName = nData.get(position).getType();
        SpannableStringBuilder fancySentence;
        String temp;

        switch (nData.get(position).getType()){
            case "announce":
                subjectName = "Announcement";
                myViewHolder.tvNotification.setText(sourceString+" made an "+subjectName);
                temp = myViewHolder.tvNotification.getText().toString();
                fancySentence = new SpannableStringBuilder(temp);
                fancySentence.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, sourceString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                fancySentence.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), temp.length()-subjectName.length(), temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                myViewHolder.tvNotification.setText(fancySentence);
                break;
            case "comment":
                myViewHolder.tvNotification.setText(sourceString+" commented on your post ");
                temp = myViewHolder.tvNotification.getText().toString();
                fancySentence = new SpannableStringBuilder(temp);
                fancySentence.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, sourceString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                myViewHolder.tvNotification.setText(fancySentence);
                break;
            case "like":
                myViewHolder.tvNotification.setText(sourceString+" liked your post ");
                temp = myViewHolder.tvNotification.getText().toString();
                fancySentence = new SpannableStringBuilder(temp);
                fancySentence.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, sourceString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                myViewHolder.tvNotification.setText(fancySentence);
                break;
            default:
                myViewHolder.tvNotification.setText(sourceString+" posted in "+subjectName);
                temp = myViewHolder.tvNotification.getText().toString();
                fancySentence = new SpannableStringBuilder(temp);
                fancySentence.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, sourceString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                fancySentence.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), temp.length()-subjectName.length(), temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                myViewHolder.tvNotification.setText(fancySentence);
                break;
        }
        long timestamp  = (long) nData.get(position).getTimeStamp();
        myViewHolder.tvDate.setText(timestampToString(timestamp));

        //Toast.makeText(nContext, ""+nData.size(),Toast.LENGTH_SHORT).show();

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(nContext, ""+nData.size(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(nContext, PostDetailActivity.class);
                intent.putExtra("postKey", nData.get(position).getRef());
                nContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return nData.size();
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

        class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imgUserPic;
        TextView tvNotification, tvDate;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUserPic = itemView.findViewById(R.id.user_pic_notify);
            tvNotification = itemView.findViewById(R.id.text_notify);
            tvDate = itemView.findViewById(R.id.notification_date);


        }
    }
}
