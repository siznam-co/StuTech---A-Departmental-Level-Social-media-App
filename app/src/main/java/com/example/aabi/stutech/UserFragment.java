package com.example.aabi.stutech;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UserFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    RecyclerView chatRecyclerView;
    List<Chat> chatList = new ArrayList<>();
    List<String > subjectList = new ArrayList<>();

    ChatAdapter chatAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    boolean alreadyAdded = false;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_user, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        chatRecyclerView  = fragmentView.findViewById(R.id.userRV);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout = fragmentView.findViewById(R.id.user_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                onStart();
            }
        });


        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance().getReference(ChatActivity.designation).child(ChatActivity.userKey).child("subjectList");
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
                            if(!user.getUserKey().equals(ChatActivity.userKey)){

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(user.getDesignation()).child(user.getUserKey());
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        alreadyAdded = false;
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
                                        }else{
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

        chatAdapter = new ChatAdapter(getActivity(),em);
        chatRecyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
    }


}
