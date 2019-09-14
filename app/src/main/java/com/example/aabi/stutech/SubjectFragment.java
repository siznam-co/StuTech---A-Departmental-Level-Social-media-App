package com.example.aabi.stutech;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SubjectFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    RecyclerView subjectRecyclerView;
    List<String> subjectList = new ArrayList<>();

    SubjectAdapter subjectAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    private OnFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_subject, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        subjectRecyclerView  = fragmentView.findViewById(R.id.subjectsRV);
        subjectRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        subjectRecyclerView.setHasFixedSize(true);

        swipeRefreshLayout = fragmentView.findViewById(R.id.subject_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                onStart();
            }
        });


        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        swipeRefreshLayout.setRefreshing(true);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference newRef = databaseReference.child("UserIDs").child(currentUser.getUid());

        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //editRollNo.setText(tempKey);
                getUserData(user.getUserKey(), user.getDesignation());
                //do what you want with the email
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserData(String tempKey, String designation) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(designation).child(tempKey).child("subjectList");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subjectList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String data = snapshot.getValue(String.class);
                    subjectList.add(data);
                }
                Collections.reverse(subjectList);
                MakeRecyclerView(subjectList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void MakeRecyclerView(List<String> em) {

        swipeRefreshLayout.setRefreshing(false);

        subjectAdapter = new SubjectAdapter(getActivity(),em);
        subjectRecyclerView.setAdapter(subjectAdapter);
        subjectAdapter.notifyDataSetChanged();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
