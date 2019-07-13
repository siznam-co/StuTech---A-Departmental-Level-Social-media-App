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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    TextView tvTitle, tvUserName, tvDescription, tvSubjectName, tvFileDownloadName, tvPostDate, tvNoOfComments, tvNoOfLikes;
    ImageView imgPost;
    ImageView imgPostProfile, imagePost, imageHeart, imageFilledHeart, btnComment, btnDownload, btnPopupMenu;
    String postKey;
    Query query;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private static final int PERMISSION_STORAGE_CODE = 234;
    ImageView imgCurrentUser, btnAddComment;
    EditText editTextComment;
    RecyclerView commentRecyclerView;
    CommentAdapter commentAdapter;
    List<Comment> commentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        postKey = getIntent().getExtras().getString("postKey");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.post_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvTitle = findViewById(R.id.row_post_title);
        tvUserName = findViewById(R.id.post_user_name);
        tvDescription = findViewById(R.id.row_post_desc);
        tvSubjectName = findViewById(R.id.post_subject_name);
        imgPost = findViewById(R.id.row_post_img);
        tvFileDownloadName = findViewById(R.id.file_download_name);
        imgPostProfile = findViewById(R.id.row_post_profile_img);
        imagePost = findViewById(R.id.reminderBell);
        imageHeart = findViewById(R.id.react_love);
        imageFilledHeart = findViewById(R.id.un_react_love);
        btnDownload = findViewById(R.id.downloadPost);
        btnPopupMenu = findViewById(R.id.post_options);
        btnComment = findViewById(R.id.comment);
        tvNoOfComments = findViewById(R.id.no_of_comments);
        tvNoOfLikes = findViewById(R.id.no_of_Likes);
        tvPostDate = findViewById(R.id.post_date);
        imgCurrentUser = findViewById(R.id.post_detail_currentuser_img);
        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);
        commentRecyclerView  = findViewById(R.id.commentRV);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerView.setHasFixedSize(true);

        query = FirebaseDatabase.getInstance().getReference("Posts").child(postKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Post post = dataSnapshot.getValue(Post.class);

                getSupportActionBar().setTitle(post.getSubjectName());

                final Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
                long timestamp  = (long) post.getTimeStamp();

                tvUserName.setText(post.getUserName());
                tvPostDate.setText(timestampToString(timestamp));
                tvSubjectName.setText(post.getSubjectName());
                tvTitle.setText(post.getTitle());
                tvDescription.setText(post.getDescription());
                if((post.getFileURL()).equals("No")){
                    imgPost.setVisibility(View.GONE);
                    tvFileDownloadName.setVisibility(View.GONE);
                }
                else{
                    if(post.getFileType().equals("camera") || post.getFileType().equals("image")) {
                        imgPost.setVisibility(View.VISIBLE);
                        tvFileDownloadName.setVisibility(View.GONE);
                        Glide.with(getApplicationContext()).load(post.getFileURL()).into(imgPost);
                    }
                    else{
                        tvFileDownloadName.setVisibility(View.VISIBLE);
                        imgPost.setVisibility(View.GONE);
                        tvFileDownloadName.setText(post.getFileName());
                    }
                }
                Glide.with(getApplicationContext()).load(post.getUserPhoto()).into(imgPostProfile);

                //TODO: To set reminder
                imagePost.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                        imagePost.startAnimation(anim);
                        Intent intent = new Intent(getApplicationContext(),ReminderActivity.class);
                    /*intent.putExtra("title", mData.get(p).getTitle());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    */
                        getApplicationContext().startActivity(intent);
                        //finish();
                    }
                });

                //TODO: React Love and Unreact Love

                final DatabaseReference LikeReference = FirebaseDatabase.getInstance().getReference("Likes")
                        .child(post.getPostKey());

                LikeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(currentUser.getUid()).exists()) {
                            //do ur stuff
                            imageHeart.setVisibility(View.INVISIBLE);
                            imageFilledHeart.setVisibility(View.VISIBLE);
                        } else {
                            //do something if not exists
                            imageHeart.setVisibility(View.VISIBLE);
                            imageFilledHeart.setVisibility(View.INVISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                final Likes likes = new Likes(currentUser.getUid(),currentUser.getDisplayName(), currentUser.getPhotoUrl().toString());
                imageHeart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageFilledHeart.startAnimation(anim);
                        LikeReference.child(currentUser.getUid()).setValue(likes).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                imageFilledHeart.setVisibility(View.VISIBLE);
                                imageHeart.setVisibility(View.INVISIBLE);
                       /* DatabaseReference notifyRef = FirebaseDatabase.getInstance().getReference("Notifications").push();
                        Notifications notification = new Notifications("like", post.getPostKey(), currentUser.getDisplayName(), currentUser.getPhotoUrl().toString(), notifyRef.getKey());
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

                imageFilledHeart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LikeReference.child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                imageHeart.setVisibility(View.VISIBLE);
                                imageFilledHeart.setVisibility(View.INVISIBLE);
                        /*final DatabaseReference delRef = FirebaseDatabase.getInstance().getReference("Notifications");
                        final Query query = FirebaseDatabase.getInstance().getReference("Notifications")
                                .orderByChild("type").equalTo("like");
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                    Notifications notifications = snapshot.getValue(Notifications.class);
                                    if(notifications.getUser().equals(currentUser.getDisplayName()) && notifications.getRef().equals(post.getPostKey())){
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
                btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnComment.startAnimation(anim);
                        editTextComment.requestFocus();
                        /*Intent postDetailActivity = new Intent(getApplicationContext(), CommentActivity.class);
                *//*postDetailActivity.putExtra("postImage",post.getFileURL());
                postDetailActivity.putExtra("description",post.getDescription());
                postDetailActivity.putExtra("userPhoto",post.getUserPhoto());*//*
                        postDetailActivity.putExtra("postKey",post.getPostKey());
                        // will fix this later i forgot to add user name to post object
                        //postDetailActivity.putExtra("userName",post.getUsername);
                *//*long timestamp  = (long) post.getTimeStamp();
                postDetailActivity.putExtra("postDate",timestamp) ;*//*
                        //Toast.makeText(getApplicationContext(),"Get lost",Toast.LENGTH_SHORT).show();
                        getApplicationContext().startActivity(postDetailActivity);*/
                    }
                });

                //Counting the number of comments


                //TODO: To download the content attached with a particular post
                btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnDownload.startAnimation(anim);
                        if((post.getFileURL()).equals("No")){
                            Toast.makeText(getApplicationContext(),"No Media/file attached",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //Toast.makeText(getApplicationContext(),"Dfm",Toast.LENGTH_SHORT).show();
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED) {
                                    // everything goes well : we have permission to access user gallery
                                    //openGallery();
                                    startDownload(post);
                                } else{
                                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),
                                            new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_STORAGE_CODE);
                                }
                            }else{
                                startDownload(post);
                            }
                        }
                    }
                });


                btnPopupMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Creating the instance of PopupMenu
                        Context wrapper = new ContextThemeWrapper(getApplicationContext(), R.style.MyPopupMenu);
                        PopupMenu popup = new PopupMenu(wrapper, v);
                        //PopupMenu popup = new PopupMenu(getApplicationContext(), btnPopupMenu);
                        //Inflating the Popup using xml file
                        //popup.getMenuInflater().inflate(R.menu.post_option_items, popup.getMenu());

                        popup.getMenu().add(Menu.NONE,1,1,"Share...");
                        popup.getMenu().add(Menu.NONE,2,2,"Report...");
                        if(currentUser.getUid().equals(post.getUserId()))
                            popup.getMenu().add(Menu.NONE,3,3,"Delete");
                        popup.show(); //showing popup menu

                        //registering popup with OnMenuItemClickListener
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                int id = item.getItemId();

                                //noinspection SimplifiableIfStatement
                                switch (id){
                                    case 1:
                                        Toast.makeText(getApplicationContext(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                                        break;
                                    case 3:
                                        deletePost(post.getPostKey(),post.getFileURL().trim());
                                        break;
                                    case 2:
                                        Toast.makeText(getApplicationContext(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                                        break;
                                }
                                //Toast.makeText(getApplicationContext(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        });

                    }
                });

                //Counting no of comments on single posts
                DatabaseReference gRef = FirebaseDatabase.getInstance().getReference("Comment").child(post.getPostKey());

                gRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            int countt = (int) dataSnapshot.getChildrenCount();
                            tvNoOfComments.setText(countt+"");
                        }else{
                            tvNoOfComments.setText(" ");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //Counting no of Likes on single posts
                DatabaseReference LRef = FirebaseDatabase.getInstance().getReference("Likes").child(post.getPostKey());

                LRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            int countt = (int) dataSnapshot.getChildrenCount();
                            tvNoOfLikes.setText(countt+"");
                        }else{
                            tvNoOfLikes.setText(" ");
                        }
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

        Glide.with(this).load(currentUser.getPhotoUrl()).into(imgCurrentUser);

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = FirebaseDatabase.getInstance().getReference("Comment").child(postKey).push();
                String comment_content = editTextComment.getText().toString();
                final String uid = currentUser.getUid();
                final String uname = currentUser.getDisplayName();
                String uimg = currentUser.getPhotoUrl().toString();
                Comment comment = new Comment(comment_content,uid,uimg,uname);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        editTextComment.setText("");
                        showMessage("Comment Added");

                        /*DatabaseReference notifyRef = FirebaseDatabase.getInstance().getReference("Notifications").push();
                        Notifications notification = new Notifications("comment", PostKey, uname, currentUser.getPhotoUrl().toString(), notifyRef.getKey());
                        notifyRef.setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        });*/
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("fail to add comment : "+e.getMessage());
                    }
                });
            }
        });

        DatabaseReference hitCommentRef = FirebaseDatabase.getInstance().getReference("Comment").child(postKey);

        hitCommentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()) {
                    Comment comment = postsnap.getValue(Comment.class);
                    commentList.add(comment) ;
                }

                Collections.reverse(commentList);

                commentAdapter = new CommentAdapter(getApplicationContext(), commentList);
                commentRecyclerView.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMessage(String string) {
        Toast.makeText(getApplicationContext(),string,Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "File not deleted", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void startDownload(Post p) {
        String url = p.getFileURL().trim();
        Toast.makeText(getApplicationContext(),"Download Started",Toast.LENGTH_SHORT).show();

        DownloadManager.Request request =new DownloadManager.Request(Uri.parse(url));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("stuTech");
        request.setDescription("Downloading file...");

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir("/stuTech",""+p.getFileName());

        DownloadManager manager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);


    }

    @Override
    public void onBackPressed() {
        if ( getFragmentManager().getBackStackEntryCount() > 0)
        {
            getFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
