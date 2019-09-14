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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.google.firebase.database.ServerValue;
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
    ImageView imgPostProfile, btnReminder, imageHeart, imageFilledHeart, btnComment, btnDownload, btnPopupMenu;
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
    TextView tagTeacher, tagStudent;

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
        btnReminder = findViewById(R.id.reminderBell);
        imageHeart = findViewById(R.id.react_love);
        imageFilledHeart = findViewById(R.id.un_react_love);
        btnDownload = findViewById(R.id.downloadPost);
        btnPopupMenu = findViewById(R.id.post_delete);
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
        tagStudent = findViewById(R.id.tag_student);
        tagTeacher = findViewById(R.id.tag_teacher);

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

                DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference("UserIDs").child(post.getUserId()).child("designation");
                tagRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String desig = dataSnapshot.getValue(String.class);
                        if(desig.equals("Teacher")){
                            tagTeacher.setVisibility(View.VISIBLE);
                        }else{
                            tagStudent.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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
                btnReminder.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {

                        btnReminder.startAnimation(anim);
                        Intent intent = new Intent(getApplicationContext(),ReminderActivity.class);
                        intent.putExtra("subjectName", "Set Reminder for "+post.getSubjectName());
                        intent.putExtra("title", "Todo: " + post.getTitle());
                        intent.putExtra("postKey", post.getPostKey());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
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

                            }
                        });
                    }
                });
                btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnComment.startAnimation(anim);
                        editTextComment.requestFocus();
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


                if(post.getUserName().equals(currentUser.getDisplayName())){
                    btnPopupMenu.setVisibility(View.VISIBLE);
                    btnPopupMenu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog sureDialog = new Dialog(PostDetailActivity.this);
                            sureDialog.setContentView(R.layout.acknowledgement_dialog);
                            sureDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            sureDialog.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
                            sureDialog.getWindow().getAttributes().gravity = Gravity.CENTER_VERTICAL;

                            TextView sureDialogText = sureDialog.findViewById(R.id.sure_dialog_text);
                            sureDialogText.setText("Are your sure? Want to delete this post of you?");

                            Button sureDelete = sureDialog.findViewById(R.id.sure_delete);
                            Button cancelDelete =  sureDialog.findViewById(R.id.cancel_delete);

                            sureDialog.show();

                            sureDelete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deletePost(post.getPostKey(),post.getFileURL().trim());
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
