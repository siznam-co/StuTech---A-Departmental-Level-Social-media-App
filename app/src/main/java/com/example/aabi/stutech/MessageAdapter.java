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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;

    private Context mContext;
    private List<Message> mData ;

    FirebaseUser currentUser;

    public MessageAdapter(Context context, List<Message> messagesList) {
        this.mContext = context;
        this.mData = messagesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if(viewType == MSG_RIGHT){
            View row = LayoutInflater.from(mContext).inflate(R.layout.right_message_item, viewGroup ,false);
            return new MyViewHolder(row);
        }else{
            View row = LayoutInflater.from(mContext).inflate(R.layout.left_message_item, viewGroup ,false);
            return new MyViewHolder(row);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.showMessage.setText(mData.get(position).getMessage());
    }

    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy @ HH:mm",calendar).toString();
        return date;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView showMessage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
                showMessage = itemView.findViewById(R.id.text_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mData.get(position).getSenderId().equals(currentUser.getUid())){
            return MSG_RIGHT;
        }else{
            return MSG_LEFT;
        }
    }
}
