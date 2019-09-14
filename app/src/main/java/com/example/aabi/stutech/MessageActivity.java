package com.example.aabi.stutech;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    String designation, userName, userPhoto, receiverUId;

    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    
    TextView titleBar, tagteacher, tagStudent;
    ImageView profilePhoto, backButton, sendButton;
    EditText userMessage;

    List<Message> messagesList = new ArrayList<>();
    RecyclerView messageRecyclerView;
    MessageAdapter messageAdapter;

    static boolean active = false;

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        designation = getIntent().getExtras().getString("designation");
        userName = getIntent().getExtras().getString("userName");
        userPhoto = getIntent().getExtras().getString("userPhoto");
        receiverUId = getIntent().getExtras().getString("userId");

        titleBar = findViewById(R.id.title_bar);
        tagteacher = findViewById(R.id.tag_teacher);
        tagStudent = findViewById(R.id.tag_student);
        profilePhoto = findViewById(R.id.chat_profile_pic);
        backButton = findViewById(R.id.back_btn_profile);


        Glide.with(this).load(userPhoto).into(profilePhoto);
        titleBar.setText(userName);
        if(designation.equals("Teacher")){
            tagteacher.setVisibility(View.VISIBLE);
        }else{
            tagStudent.setVisibility(View.VISIBLE);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent goToProfileActivity = new Intent(getApplicationContext(), ProfileActivity.class);
                goToProfileActivity.putExtra("userID", receiverUId);
                startActivity(goToProfileActivity);
            }
        });

        messageRecyclerView  = findViewById(R.id.messageRV);
        /*messageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageRecyclerView.setHasFixedSize(true);*/

        LinearLayoutManager layoutManager=
                new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,true);
        //layoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(layoutManager);

        //TODO: Getting this user conversation, if any
        getConversationMessages();

        userMessage = findViewById(R.id.user_message);
        sendButton = findViewById(R.id.send_btn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userMessage.getText().toString().isEmpty()){
                    showMessage("Please write a message");
                }else{
                    sendButton.setVisibility(View.INVISIBLE);
                    sendMessage(userMessage.getText().toString());
                }
            }
        });

    }

    private void getConversationMessages() {

        DatabaseReference conversation = FirebaseDatabase.getInstance().getReference("message");
        conversation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if((message.getSenderId().equals(currentUser.getUid()) && message.getReceiverId().equals(receiverUId)) ||
                            (message.getSenderId().equals(receiverUId) && message.getReceiverId().equals(currentUser.getUid()))){
                        messagesList.add(message);
                    }
                }
                MakeRecyclerView(messagesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void MakeRecyclerView(List<Message> messagesList) {
        //Collections.sort(messagesList, dateSort);
        Collections.reverse(messagesList);
        messageAdapter = new MessageAdapter(getApplicationContext(), messagesList);
        messageRecyclerView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();
    }

    /*public static Comparator<Message> dateSort = new Comparator<Message>() {

        public int compare(Message s1, Message s2) {

            Long no1 = (Long) s1.getTimeStamp();
            Long no2 = (Long) s2.getTimeStamp();

            *//*For ascending order*//*
            return (int) (no2-no1);

            *//*For descending order*//*
            //rollno2-rollno1;
        }};
*/
    private void sendMessage(String mes) {
        final Message message = new Message(mes, currentUser.getUid(), currentUser.getDisplayName(), receiverUId);
        DatabaseReference messageReference= FirebaseDatabase.getInstance()
                .getReference("message").push();
        messageReference.setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                sendButton.setVisibility(View.VISIBLE);
                userMessage.setText("");
                getConversationMessages();
            }
        });

    }

    private void showMessage(String string) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("UserIDs").child(currentUser.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Intent goToChatActivity = new Intent(getApplicationContext(), ChatActivity.class);
                goToChatActivity.putExtra("userKey", user.getUserKey());
                goToChatActivity.putExtra("designation", user.getDesignation());
                startActivity(goToChatActivity);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
