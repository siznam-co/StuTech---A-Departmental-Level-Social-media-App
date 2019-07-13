package com.example.aabi.stutech;


import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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




    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item,parent,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        final Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.scale);
        long timestamp  = (long) mData.get(position).getTimeStamp();

        holder.tvUserName.setText(mData.get(position).getUserName());
        holder.tvPostDate.setText(timestampToString(timestamp));
        holder.tvSubjectName.setText(mData.get(position).getSubjectName());
        holder.tvTitle.setText(mData.get(position).getTitle());
        holder.tvDescription.setText(mData.get(position).getDescription());
        if((mData.get(position).getFileURL()).equals("No")){
            holder.imgPost.setVisibility(View.GONE);
            holder.tvFileDownloadName.setVisibility(View.GONE);
        }
        else{
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
        Glide.with(mContext).load(mData.get(position).getUserPhoto()).into(holder.imgPostProfile);

        holder.imgPostProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToProfileActivity = new Intent(mContext, ProfileActivity.class);
                goToProfileActivity.putExtra("userID", mData.get(position).getUserId());
                mContext.startActivity(goToProfileActivity);
            }
        });

        //TODO: To set reminder
        holder.imagePost.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                holder.imagePost.startAnimation(anim);
                Intent intent = new Intent(mContext,ReminderActivity.class);
                    /*intent.putExtra("title", mData.get(p).getTitle());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    */
                mContext.startActivity(intent);
                //finish();
            }
        });

        //TODO: React Love and Unreact Love

        final DatabaseReference LikeReference = FirebaseDatabase.getInstance().getReference("Likes")
                .child(mData.get(position).getPostKey());

        LikeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUser.getUid()).exists()) {
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
                LikeReference.child(currentUser.getUid()).setValue(likes).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        holder.imageFilledHeart.setVisibility(View.VISIBLE);
                        holder.imageHeart.setVisibility(View.INVISIBLE);
                       /* DatabaseReference notifyRef = FirebaseDatabase.getInstance().getReference("Notifications").push();
                        Notifications notification = new Notifications("like", mData.get(position).getPostKey(), currentUser.getDisplayName(), currentUser.getPhotoUrl().toString(), notifyRef.getKey());
                        notifyRef.setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                                        }
                        });*/
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
                        /*final DatabaseReference delRef = FirebaseDatabase.getInstance().getReference("Notifications");
                        final Query query = FirebaseDatabase.getInstance().getReference("Notifications")
                                .orderByChild("type").equalTo("like");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    Notifications notifications = snapshot.getValue(Notifications.class);
                                    if(notifications.getUser().equals(currentUser.getDisplayName()) && notifications.getRef().equals(mData.get(position).getPostKey())){
                                        delRef.child(notifications.getKey()).removeValue();

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });*/


                    }
                });
            }
        });
        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnComment.startAnimation(anim);
                Intent postDetailActivity = new Intent(mContext, CommentActivity.class);
                /*postDetailActivity.putExtra("postImage",mData.get(position).getFileURL());
                postDetailActivity.putExtra("description",mData.get(position).getDescription());
                postDetailActivity.putExtra("userPhoto",mData.get(position).getUserPhoto());*/
                postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
                // will fix this later i forgot to add user name to post object
                //postDetailActivity.putExtra("userName",mData.get(position).getUsername);
                /*long timestamp  = (long) mData.get(position).getTimeStamp();
                postDetailActivity.putExtra("postDate",timestamp) ;*/
                //Toast.makeText(mContext,"Get lost",Toast.LENGTH_SHORT).show();
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


        holder.btnPopupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(mContext, holder.btnPopupMenu);
                //Inflating the Popup using xml file
                //popup.getMenuInflater().inflate(R.menu.post_option_items, popup.getMenu());

                popup.getMenu().add(Menu.NONE,1,1,"Share...");
                if(currentUser.getUid().equals(mData.get(position).getUserId()))
                    popup.getMenu().add(Menu.NONE,2,2,"Delete");
                popup.getMenu().add(Menu.NONE,3,3,"Report...");

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        //noinspection SimplifiableIfStatement
                        switch (id){
                            case 1:
                                Toast.makeText(mContext,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                deletePost(mData.get(position).getPostKey(),mData.get(position).getFileURL().trim());
                                break;
                            case 3:
                                Toast.makeText(mContext,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                                break;
                        }
                        //Toast.makeText(mContext,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show(); //showing popup menu
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

    private void showMessage(String string) {
        Toast.makeText(mContext,string,Toast.LENGTH_LONG).show();
    }

    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy @ HH:mm",calendar).toString();
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
        ImageView imgPost;
        ImageView imgPostProfile, imagePost, imageHeart, imageFilledHeart, btnComment, btnDownload, btnPopupMenu;
        MyViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.row_post_title);
            tvUserName = itemView.findViewById(R.id.post_user_name);
            tvDescription = itemView.findViewById(R.id.row_post_desc);
            tvSubjectName = itemView.findViewById(R.id.post_subject_name);
            imgPost = itemView.findViewById(R.id.row_post_img);
            tvFileDownloadName = itemView.findViewById(R.id.file_download_name);
            imgPostProfile = itemView.findViewById(R.id.row_post_profile_img);
            imagePost = itemView.findViewById(R.id.reminderBell);
            imageHeart = itemView.findViewById(R.id.react_love);
            imageFilledHeart = itemView.findViewById(R.id.un_react_love);
            btnDownload = itemView.findViewById(R.id.downloadPost);
            btnPopupMenu = itemView.findViewById(R.id.post_options);
            btnComment = itemView.findViewById(R.id.comment);
            tvNoOfComments = itemView.findViewById(R.id.no_of_comments);
            tvNoOfLikes = itemView.findViewById(R.id.no_of_Likes);
            tvPostDate = itemView.findViewById(R.id.post_date);

        }


    }
}
