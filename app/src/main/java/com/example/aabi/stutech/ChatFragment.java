package com.example.aabi.stutech;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    RecyclerView chatRecyclerView;
    List<Chat> chatList = new ArrayList<>();
    List<String > userList = new ArrayList<>();

    ChatAdapter chatAdapter;
    SwipeRefreshLayout swipeRefreshLayout;


    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_chat, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        chatRecyclerView  = fragmentView.findViewById(R.id.chatRV);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout = fragmentView.findViewById(R.id.fragment_chat_swipe_layout);
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
        //swipeRefreshLayout.setRefreshing(true);
        DatabaseReference conversation = FirebaseDatabase.getInstance().getReference("message");
        conversation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if(message.getSenderId().equals(currentUser.getUid())){
                        if(!userList.contains(message.getReceiverId())){
                            userList.add(message.getReceiverId());
                        }
                    }if(message.getReceiverId().equals(currentUser.getUid())){
                        if(!userList.contains(message.getSenderId())){
                            userList.add(message.getSenderId());
                        }
                    }
                }
                DisplaySelectedUsers(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void DisplaySelectedUsers(final List<String> userList) {

        DatabaseReference usersKeyRef = FirebaseDatabase.getInstance().getReference("UserIDs");
        usersKeyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    for(String uid: userList){
                        final User user = snapshot.getValue(User.class);

                        if(uid.equals(snapshot.getKey())){
                        //Query inside another query
                            DatabaseReference userDataRef = FirebaseDatabase.getInstance().getReference(user.getDesignation()).child(user.getUserKey());
                            userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(user.getDesignation().equals("Teacher")){
                                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                                        Chat chat = new Chat(teacher.getDesignation(),teacher.getTeacherKey(),teacher.getName(),teacher.getUserPhoto(),teacher.getUserId());
                                        chatList.add(chat);
                                        MakeRecyclerView(chatList);
                                    }else{
                                        Student student = dataSnapshot.getValue(Student.class);
                                        Chat chat = new Chat(student.getDesignation(),student.getStudentKey(),student.getName(),student.getUserPhoto(),student.getUserId());
                                        chatList.add(chat);
                                        MakeRecyclerView(chatList);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void MakeRecyclerView(List<Chat> list) {

        swipeRefreshLayout.setRefreshing(false);

        chatAdapter = new ChatAdapter(getActivity(),list);
        chatRecyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();
    }
}
