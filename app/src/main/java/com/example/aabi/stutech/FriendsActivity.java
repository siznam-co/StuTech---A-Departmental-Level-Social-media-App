package com.example.aabi.stutech;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    RecyclerView chatRecyclerView;
    List<Chat> chatList = new ArrayList<>();
    List<String > subjectList = new ArrayList<>();

    ChatAdapter chatAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    String searchByDesig;

    boolean alreadyAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        searchByDesig = getIntent().getExtras().getString("desig");

        chatRecyclerView  = findViewById(R.id.friendsRV);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        chatRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout = findViewById(R.id.user_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                onStart();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance().getReference(HomeActivity.designation).child(HomeActivity.userKey).child("subjectList");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subjectList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String subject = snapshot.getValue(String.class);
                    subjectList.add(subject);
                }

                DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("UserIDs");
                newRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        chatList.clear();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            final User user = snapshot.getValue(User.class);
                            //Current User key
                            if(!user.getUserKey().equals(HomeActivity.userKey)){

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(user.getDesignation()).child(user.getUserKey());
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        alreadyAdded = false;
                                        if(searchByDesig.equals("Teacher")){
                                            if(user.getDesignation().equals("Teacher")){
                                                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                                for(String sub: subjectList){
                                                    for(String userSub: teacher.getSubjectList()){
                                                        if(sub.equals(userSub) && !alreadyAdded){
                                                            Chat chat = new Chat(teacher.designation, teacher.teacherKey, teacher.name, teacher.getUserPhoto(), teacher.userId);
                                                            chatList.add(chat);
                                                            MakeRecyclerView(chatList);
                                                            alreadyAdded = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }else{
                                            if(user.getDesignation().equals("Student")){
                                                Student student = dataSnapshot.getValue(Student.class);
                                                for(String sub: subjectList){
                                                    for(String userSub: student.getSubjectList()){
                                                        if(sub.equals(userSub) && !alreadyAdded){
                                                            Chat chat = new Chat(student.getDesignation(), student.getStudentKey(), student.getName(), student.getUserPhoto(), student.getUserId());
                                                            chatList.add(chat);
                                                            MakeRecyclerView(chatList);
                                                            alreadyAdded = true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
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

    }

    private void MakeRecyclerView(List<Chat> em) {

        swipeRefreshLayout.setRefreshing(false);

        chatAdapter = new ChatAdapter(getApplicationContext(),em);
        chatRecyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
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
}
