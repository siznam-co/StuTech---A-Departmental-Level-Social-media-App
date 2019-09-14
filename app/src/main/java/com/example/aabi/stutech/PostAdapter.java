package com.example.aabi.stutech;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    private static final int PERMISSION_STORAGE_CODE = 234;
    private Context mContext;
    private List<Post> mData ;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    public static final int TYPE_ANNOUNCE = 0;
    public static final int TYPE_POST = 1;

    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_ANNOUNCE){
            View row = LayoutInflater.from(mContext).inflate(R.layout.row_announce_item,parent,false);
            return new MyViewHolder(row);
        }else{
            View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item,parent,false);
            return new MyViewHolder(row);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        final Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.scale);
        long timestamp  = (long) mData.get(position).getTimeStamp();

        holder.tvUserName.setText(mData.get(position).getUserName());
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgPostProfile);
        holder.tvPostDate.setText(timestampToString(timestamp));
        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.tvDescription.setText(mData.get(position).getDescription());

        holder.imgPostProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToProfileActivity = new Intent(mContext, ProfileActivity.class);
                goToProfileActivity.putExtra("userID", mData.get(position).getUserId());
                mContext.startActivity(goToProfileActivity);
            }
        });

        DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference("UserIDs").child(mData.get(position).getUserId()).child("designation");
        tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String desig = dataSnapshot.getValue(String.class);
                if(desig.equals("Teacher")){
                    holder.tagTeacher.setVisibility(View.VISIBLE);
                }else{
                    holder.tagStudent.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(mData.get(position).getUserName().equals(currentUser.getDisplayName())){
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog sureDialog = new Dialog(mContext);
                    sureDialog.setContentView(R.layout.acknowledgement_dialog);
                    sureDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    sureDialog.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
                    sureDialog.getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;

                    TextView sureDialogText = sureDialog.findViewById(R.id.sure_dialog_text);
                    sureDialogText.setText("Are you sure? Want to delete this post of you?");

                    Button sureDelete = sureDialog.findViewById(R.id.sure_delete);
                    Button cancelDelete =  sureDialog.findViewById(R.id.cancel_delete);

                    sureDialog.show();

                    sureDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deletePost(mData.get(position).getPostKey(),mData.get(position).getFileURL().trim());
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
            });
        }

        if((mData.get(position).getFileURL()).equals("No")){
            //holder.itemsAttachedLayout.setVisibility(View.GONE);
        }
        else{
            //holder.itemsAttachedLayout.setVisibility(View.VISIBLE);
            if(mData.get(position).getFileType().equals("camera") || mData.get(position).getFileType().equals("image")) {
                holder.imgPost.setVisibility(View.VISIBLE);
                holder.tvFileDownloadName.setVisibility(View.GONE);
                Glide.with(mContext).load(mData.get(position).getFileURL()).into(holder.imgPost);
            }
            else{
                holder.tvFileDownloadName.setVisibility(View.VISIBLE);
                holder.imgPost.setVisibility(View.GONE);
                holder.tvFileDownloadName.setText(mData.get(position).getFileName());
            }
        }

        if(!mData.get(position).getSubjectName().equals("announce")){
            holder.tvSubjectName.setText(mData.get(position).getSubjectName());//

            //TODO: To set reminder
            holder.btnReminder.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    holder.btnReminder.startAnimation(anim);
                    Intent intent = new Intent(mContext,ReminderActivity.class);
                    intent.putExtra("subjectName", "Set Reminder for "+mData.get(position).getSubjectName());
                    intent.putExtra("title", "Todo: " +mData.get(position).getTitle());
                    intent.putExtra("postKey", mData.get(position).getPostKey());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    mContext.startActivity(intent);
                    //finish();
                }
            });

            //TODO: React Love and Unreact Love

            final DatabaseReference LikeReference = FirebaseDatabase.getInstance().getReference("Likes")
                    .child(mData.get(position).getPostKey()).child(currentUser.getUid());

            LikeReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //do ur stuff
                        holder.imageHeart.setVisibility(View.INVISIBLE);
                        holder.imageFilledHeart.setVisibility(View.VISIBLE);
                    } else {
                        //do something if not exists
                        holder.imageHeart.setVisibility(View.VISIBLE);
                        holder.imageFilledHeart.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            final Likes likes = new Likes(currentUser.getUid(),currentUser.getDisplayName(), currentUser.getPhotoUrl().toString());
            holder.imageHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.imageFilledHeart.startAnimation(anim);
                    LikeReference.setValue(likes).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            holder.imageFilledHeart.setVisibility(View.VISIBLE);
                            holder.imageHeart.setVisibility(View.INVISIBLE);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showMessage("failed to like : "+e.getMessage());
                        }
                    });
                }
            });

            holder.imageFilledHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LikeReference.child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            holder.imageHeart.setVisibility(View.VISIBLE);
                            holder.imageFilledHeart.setVisibility(View.INVISIBLE);

                        }
                    });
                }
            });
            holder.btnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.btnComment.startAnimation(anim);
                    Intent postDetailActivity = new Intent(mContext, CommentActivity.class);
                    postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
                    mContext.startActivity(postDetailActivity);
                }
            });

            //Counting the number of comments


            //TODO: To download the content attached with a particular post
            holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.btnDownload.startAnimation(anim);
                    if((mData.get(position).getFileURL()).equals("No")){
                        Toast.makeText(mContext,"No Media/file attached",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //Toast.makeText(mContext,"Dfm",Toast.LENGTH_SHORT).show();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                // everything goes well : we have permission to access user gallery
                                //openGallery();
                                startDownload(position);
                            } else{
                                ActivityCompat.requestPermissions((Activity) mContext,
                                        new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_STORAGE_CODE);
                            }
                        }else{
                            startDownload(position);
                        }
                    }
                }
            });

            //Counting no of comments on single posts
            DatabaseReference gRef = FirebaseDatabase.getInstance().getReference("Comment").child(mData.get(position).getPostKey());

            gRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        int countt = (int) dataSnapshot.getChildrenCount();
                        holder.tvNoOfComments.setText(countt+"");
                    }else{
                        holder.tvNoOfComments.setText(" ");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //Counting no of Likes on single posts
            DatabaseReference LRef = FirebaseDatabase.getInstance().getReference("Likes").child(mData.get(position).getPostKey());

            LRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        int countt = (int) dataSnapshot.getChildrenCount();
                        holder.tvNoOfLikes.setText(countt+"");
                    }else{
                        holder.tvNoOfLikes.setText(" ");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("postKey", mData.get(position).getPostKey());
                mContext.startActivity(intent);
            }
        });

    }

    private void showMessage(String string) {
        Toast.makeText(mContext,string,Toast.LENGTH_LONG).show();
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

    private void deletePost(String postKey, String fileURL) {

        final DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Posts").child(postKey);

        if (fileURL.equals("No")) {
            dR.removeValue();
        } else {
            StorageReference delRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileURL);
            delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dR.removeValue();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext, "File not deleted", Toast.LENGTH_SHORT).show();
                }
            });
        }
        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes").child(postKey);
        likeRef.removeValue();
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference("Comment").child(postKey);
        commentRef.removeValue();
    }

    private void startDownload(int p) {
        String url = mData.get(p).getFileURL().trim();
        Toast.makeText(mContext,"Download Started",Toast.LENGTH_SHORT).show();

        DownloadManager.Request request =new DownloadManager.Request(Uri.parse(url));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("stuTech");
        request.setDescription("Downloading file...");

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir("/stuTech",""+mData.get(p).getFileName());

        DownloadManager manager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvUserName, tvDescription, tvSubjectName, tvFileDownloadName, tvPostDate, tvNoOfComments, tvNoOfLikes;
        TextView tagTeacher, tagStudent;
        ImageView imgPost;
        ImageView imgPostProfile, btnReminder, imageHeart, imageFilledHeart, btnComment, btnDownload, btnDelete;
        RelativeLayout itemsAttachedLayout;

        MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_post_title);
            tvUserName = itemView.findViewById(R.id.post_user_name);
            tvDescription = itemView.findViewById(R.id.row_post_desc);
            tvSubjectName = itemView.findViewById(R.id.post_subject_name);
            itemsAttachedLayout = itemView.findViewById(R.id.items_attached_layout);
            imgPost = itemView.findViewById(R.id.row_post_img);
            tvFileDownloadName = itemView.findViewById(R.id.file_download_name);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);
            btnReminder = itemView.findViewById(R.id.reminderBell);
            imageHeart = itemView.findViewById(R.id.react_love);
            imageFilledHeart = itemView.findViewById(R.id.un_react_love);
            btnDownload = itemView.findViewById(R.id.downloadPost);
            btnDelete = itemView.findViewById(R.id.post_delete);
            btnComment = itemView.findViewById(R.id.comment);
            tvNoOfComments = itemView.findViewById(R.id.no_of_comments);
            tvNoOfLikes = itemView.findViewById(R.id.no_of_Likes);
            tvPostDate = itemView.findViewById(R.id.post_date);
            tagStudent = itemView.findViewById(R.id.tag_student);
            tagTeacher = itemView.findViewById(R.id.tag_teacher);
        }


    }

    @Override
    public int getItemViewType(int position) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mData.get(position).getSubjectName().equals("announce")){
            return TYPE_ANNOUNCE;
        }else{
            return TYPE_POST;
        }
    }
}
