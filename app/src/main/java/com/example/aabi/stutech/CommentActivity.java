package com.example.aabi.stutech;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CommentActivity extends AppCompatActivity {

    ImageView imgCurrentUser, btnAddComment;
    EditText editTextComment;
    String PostKey;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    Query query1;
    List<Comment> commentList = new ArrayList<>();

    FirebaseDatabase firebaseDatabase;
    RecyclerView commentRecyclerView;
    CommentAdapter commentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);


        Toolbar toolbar = (Toolbar) findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        imgCurrentUser = findViewById(R.id.post_detail_currentuser_img);

        editTextComment = findViewById(R.id.post_detail_comment);
        btnAddComment = findViewById(R.id.post_detail_add_comment_btn);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();


        // add Comment button click listner

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference("Comment").child(PostKey).push();
                String comment_content = editTextComment.getText().toString();
                final String uid = firebaseUser.getUid();
                final String uname = firebaseUser.getDisplayName();
                String uimg = firebaseUser.getPhotoUrl().toString();
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

        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);
        // get post id
        PostKey = getIntent().getExtras().getString("postKey");


        commentRecyclerView  = findViewById(R.id.commentRV);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentRecyclerView.setHasFixedSize(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Comment").child(PostKey);

        //query1 = FirebaseDatabase.getInstance().getReference("Comment").child(PostKey);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()) {
                    Comment comment = postsnap.getValue(Comment.class);
                    commentList.add(comment) ;
                }

                //Collections.reverse(commentList);

                commentAdapter = new CommentAdapter(getApplicationContext(), commentList);
                commentRecyclerView.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showMessage(String message) {

        Toast.makeText(this,message,Toast.LENGTH_LONG).show();

    }


    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy @ HH:mm",calendar).toString();
        return date;


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

