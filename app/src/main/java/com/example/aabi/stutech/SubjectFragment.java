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

    Button btnSelectSubject, btnAnnounce;
    static Dialog popSubjectFilter;
    LinearLayout SubjectsListView;
    Button btnFilterIt;
    ImageView fabFilterBtn;
    TextView textViewPanel;
    String tempSubjectName = "";
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "PrefsFile2";

    private String semester;
    RecyclerView subjectRecyclerView;
    List<String> subjectList = new ArrayList<>();
    List<String> tempSubjectList = new ArrayList<>();

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

        fabFilterBtn = fragmentView.findViewById(R.id.fab_filter_it);

        fabFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popSubjectFilter.show();
            }
        });

        filterPopup();

        swipeRefreshLayout = fragmentView.findViewById(R.id.subject_refresh_layout);
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
                textViewPanel.setText(Arrays.toString(new List[]{subjectList}));
                Collections.reverse(subjectList);


                tempSubjectList.addAll(subjectList);
                MakeRecyclerView(subjectList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void filterPopup() {
        popSubjectFilter = new Dialog(getActivity());
        popSubjectFilter.setContentView(R.layout.popup_filter_subject);
        popSubjectFilter.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popSubjectFilter.getWindow().getAttributes().gravity = Gravity.CENTER;

        SubjectsListView = popSubjectFilter.findViewById(R.id.subjects_checklist);
        Spinner spinner = popSubjectFilter.findViewById(R.id.subject_semester_spinner);
        btnFilterIt = popSubjectFilter.findViewById(R.id.filter_it);
        textViewPanel = popSubjectFilter.findViewById(R.id.pop_subject_panel);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.all_semester, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
                ArrayList<String> mTestArray;

                switch (text) {
                    case "1st":
                        SubjectsListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.First)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "2nd":
                        SubjectsListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Second)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "3rd":
                        SubjectsListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Third)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "4th":
                        SubjectsListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Fourth)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "5th":
                        SubjectsListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Fifth)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "6th":
                        SubjectsListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Sixth)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "7th":
                        SubjectsListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Seventh)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "8th":
                        SubjectsListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Eighth)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    default:
                        SubjectsListView.removeAllViews();
                        break;
                }
                setSemester(text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //TODO: Hit the filter button
        btnFilterIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MakeRecyclerView(subjectList);

                //Saving subjects list preferences
                /*Set<String> set = new HashSet<String>();
                set.addAll(subjectList);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putStringSet("AllSubjects", set);
                editor.putString("oldUserId",currentUser.getUid());
                editor.apply();*/

                popSubjectFilter.hide();
            }
        });
    }

    private void setSemester(String text) {
        this.semester = text;
    }
    private String getSemester(){
        return semester;
    }

    private void setSubjectCheckBoxText(ArrayList<String> mTestArray) {
        for(int i=0; i<mTestArray.size(); i++){
            CheckBox checkBox = new CheckBox(getActivity());
            checkBox.setId(i);
            checkBox.setText(mTestArray.get(i));
            checkBox.setTextColor(getResources().getColor(R.color.color_black));
            if(subjectList.contains(checkBox.getText().toString()))
                checkBox.setChecked(true);
            checkBox.setOnClickListener(getOnClickDoSomething(checkBox));
            SubjectsListView.addView(checkBox);
        }
    }

    private View.OnClickListener getOnClickDoSomething(final CheckBox checkBox) {
        return new View.OnClickListener() {
            String subjectName = checkBox.getText().toString();
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()) {
                    if(subjectList.contains(subjectName)) {
                        Toast.makeText(getActivity(), "Already Added! "+subjectName, Toast.LENGTH_SHORT).show();
                    } else{
                        subjectList.add(subjectName);
                        textViewPanel.setText(Arrays.toString(new List[]{subjectList}));
                    }

                } else{
                    if(tempSubjectList.contains(subjectName)){
                        checkBox.setChecked(true);
                    }else {
                        subjectList.remove(subjectName);
                    }
                    textViewPanel.setText(Arrays.toString(new List[]{subjectList}));
                }
            }
        };
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
