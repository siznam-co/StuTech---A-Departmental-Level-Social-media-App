package com.example.aabi.stutech;

import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.media.audiofx.NoiseSuppressor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.Nls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.security.auth.Subject;

public class NotificationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PERMISSION_STORAGE_CODE = 124 ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    RecyclerView notiRecyclerView ;
    NotiAdapter notiAdapter ;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference ;
    List<Notifications> NList = new ArrayList<>();
    Query query, query1, query2, query3;
    RelativeLayout relativeLayout;
    ImageView myProfilePic;
    List<String> subjectList = new ArrayList<>();
    List<String> myPosts = new ArrayList<>();
    SwipeRefreshLayout refreshLayout;



    private static final int PReqCode = 2 ;
    private static final int PICK_IMAGE_REQUEST = 3;
    String fileType = "";
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    static Dialog popAddPost;




    public NotificationFragment(){}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_profile);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_notifications, container, false);

        //FirebaseMessaging.getInstance().subscribeToTopic("HCI");
        refreshLayout = fragmentView.findViewById(R.id.notification_refresh_layout);

        notiRecyclerView  = fragmentView.findViewById(R.id.notificationRV);
        notiRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        notiRecyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        relativeLayout = fragmentView.findViewById(R.id.home_top_items);
        //FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                onStart();
            }
        });
        return fragmentView;

    }

    @Override
    public void onStart() {
        super.onStart();

        //TODO: We want notifications of:
        // * posts(subjects from current user subjectList),
        // * comments (on current user post),
        // * likes (on current user post),
        // * attendance (of subjects from current user subjectList => latest attendance marked)

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        DatabaseReference UserIdReference = FirebaseDatabase.getInstance().getReference("UserIDs").child(currentUser.getUid());

        UserIdReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //editRollNo.setText(tempKey);
                DatabaseReference userReference;
                if(user.getDesignation().equals("Teacher")){
                    userReference = FirebaseDatabase.getInstance()
                            .getReference("Teacher").child(user.getUserKey()).child("subjectList");

                }else{
                    userReference = FirebaseDatabase.getInstance()
                            .getReference("Student").child(user.getUserKey()).child("subjectList");
                }
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        subjectList.clear();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            String subject = snapshot.getValue(String.class);
                            subjectList.add(subject);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                /*notiAdapter = new NotiAdapter(getActivity(),NList);
                notiRecyclerView.setAdapter(notiAdapter);
                Toast.makeText(getActivity(), ""+NList.size(),Toast.LENGTH_SHORT).show();*/


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        query1 = FirebaseDatabase.getInstance().getReference("Posts");

        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myPosts.clear();
                NList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    final Post post = snapshot.getValue(Post.class);
                    if(post.getUserId().equals(currentUser.getUid())){
                        myPosts.add(post.getPostKey());
                        //Nested Query1
                        /*query2 = FirebaseDatabase.getInstance().getReference("Likes").child(post.getPostKey());
                        query2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                for(DataSnapshot childSnapShot: snapshot1.getChildren()){
                                    Likes likes = childSnapShot.getValue(Likes.class);
                                    if(likes.getUserName().equals(currentUser.getDisplayName())){
                                        Notifications no = new Notifications("like", post.getPostKey(), likes.getUserName(), likes.getUserPhoto(), likes.getTimeStamp());

                                    }else{
                                        Notifications no = new Notifications("like", post.getPostKey(), likes.getUserName(), likes.getUserPhoto(), likes.getTimeStamp());
                                        NList.add(no);
                                        Collections.sort(NList, dateSort);
                                        //Toast.makeText(getActivity(), ""+NList.size(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        //Nested Query2
                        query3 = FirebaseDatabase.getInstance().getReference("Comment").child(post.getPostKey());
                        query3.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                for(DataSnapshot childSnapShot: snapshot2.getChildren()){
                                    Comment comment = childSnapShot.getValue(Comment.class);
                                    if(comment.getUname().equals(currentUser.getDisplayName())){
                                        Notifications no = new Notifications("comment", post.getPostKey(), comment.getUname(), comment.getUimg(), comment.getTimestamp());

                                    }else{
                                        Notifications no = new Notifications("comment", post.getPostKey(), comment.getUname(), comment.getUimg(), comment.getTimestamp());
                                        NList.add(no);
                                        //Toast.makeText(getActivity(), ""+NList.size(),Toast.LENGTH_SHORT).show();
                                        Collections.sort(NList, dateSort);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });*/


                    }else{
                        if(post.getSubjectName().equals("announce")){
                            Notifications no = new Notifications("announce", post.getPostKey(), post.getUserName(), post.getUserPhoto(), post.getTimeStamp());
                            NList.add(no);
                            Collections.sort(NList, dateSort);

                        }else{
                            for(String sub: subjectList){
                                if(sub.equals(post.getSubjectName())){
                                    Notifications no = new Notifications(sub, post.getPostKey(), post.getUserName(), post.getUserPhoto(), post.getTimeStamp());
                                    NList.add(no);
                                    /*Collections.sort(NList, dateSort);*/

                                }
                            }
                        }
                    }

                }
                /*Collections.sort(NList, dateSort);
                notiAdapter = new NotiAdapter(getActivity(),NList);
                notiRecyclerView.setAdapter(notiAdapter);
                refreshLayout.setRefreshing(false);*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        query2 = FirebaseDatabase.getInstance().getReference("Likes");
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        for(String temp: myPosts){
                            if(temp.equals(snapshot.getKey())){
                                for(DataSnapshot childSnapShot: snapshot.getChildren()){
                                    Likes likes = childSnapShot.getValue(Likes.class);
                                    if(likes.getUserName().equals(currentUser.getDisplayName())){
                                        Notifications no = new Notifications("like", snapshot.getKey(), likes.getUserName(), likes.getUserPhoto(), likes.getTimeStamp());

                                    }else{
                                        Notifications no = new Notifications("like", snapshot.getKey(), likes.getUserName(), likes.getUserPhoto(), likes.getTimeStamp());
                                        NList.add(no);
                                        //Toast.makeText(getActivity(), ""+NList.size(),Toast.LENGTH_SHORT).show();
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
        query3 = FirebaseDatabase.getInstance().getReference("Comment");
        query3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        for(String temp: myPosts){
                            if(temp.equals(snapshot.getKey())){
                                for(DataSnapshot childSnapShot: snapshot.getChildren()){
                                    Comment comment = childSnapShot.getValue(Comment.class);
                                    if(comment.getUname().equals(currentUser.getDisplayName())){
                                        Notifications no = new Notifications("comment", snapshot.getKey(), comment.getUname(), comment.getUimg(), comment.getTimestamp());

                                    }else{
                                        Notifications no = new Notifications("comment", snapshot.getKey(), comment.getUname(), comment.getUimg(), comment.getTimestamp());
                                        NList.add(no);
                                        //Toast.makeText(getActivity(), ""+NList.size(),Toast.LENGTH_SHORT).show();
                                        Collections.sort(NList, dateSort);
                                        notiAdapter = new NotiAdapter(getActivity(),NList);
                                        notiRecyclerView.setAdapter(notiAdapter);
                                        refreshLayout.setRefreshing(false);
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


        /*query = FirebaseDatabase.getInstance().getReference("Notifications");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                NList.clear();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()) {
                    Notifications n = postsnap.getValue(Notifications.class);
                    NList.add(n) ;
                }
                Collections.reverse(NList);

                notiAdapter = new NotiAdapter(getActivity(),NList);
                notiRecyclerView.setAdapter(notiAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


    }

    public static Comparator<Notifications> dateSort = new Comparator<Notifications>() {

        public int compare(Notifications s1, Notifications s2) {

            Long no1 = (Long) s1.getTimeStamp();
            Long no2 = (Long) s2.getTimeStamp();

            /*For ascending order*/
            return (int) (no2-no1);

            /*For descending order*/
            //rollno2-rollno1;
        }};


}
