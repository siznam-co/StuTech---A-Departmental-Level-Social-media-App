package com.example.aabi.stutech;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PERMISSION_STORAGE_CODE = 124 ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    RelativeLayout relativeLayoutTop;
    Button btnUploads, btnSettings, btnLogout, btnFellows, btnTeachers ;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_menu, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        TextView UserName = fragmentView.findViewById(R.id.user_name_menu_fragment);
        ImageView UserPhoto = fragmentView.findViewById(R.id.profile_pic_menu_fragment);
        relativeLayoutTop = fragmentView.findViewById(R.id.top_layout_menu_fragment);

        btnUploads = fragmentView.findViewById(R.id.button_upload_menu_fragment);
        btnSettings = fragmentView.findViewById(R.id.button_settings_menu_fragment);
        btnLogout = fragmentView.findViewById(R.id.button_logout_menu_fragment);
        btnFellows = fragmentView.findViewById(R.id.button_fellows_menu_fragment);
        btnTeachers = fragmentView.findViewById(R.id.button_teachers_menu_fragment);

        if(HomeActivity.designation.equals("Teacher")){
            btnFellows.setText("  Students");
            btnTeachers.setText("  Colleagues");
        }

        UserName.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(UserPhoto);


        relativeLayoutTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToProfileActivity = new Intent(getActivity(), ProfileActivity.class);
                goToProfileActivity.putExtra("userID", currentUser.getUid());
                startActivity(goToProfileActivity);
            }
        });

        btnFellows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFriendsActivity = new Intent(getActivity(), FriendsActivity.class);
                goToFriendsActivity.putExtra("desig", "Student" );
                startActivity(goToFriendsActivity);
            }
        });

        btnTeachers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFriendsActivity = new Intent(getActivity(), FriendsActivity.class);
                goToFriendsActivity.putExtra("desig", "Teacher" );
                startActivity(goToFriendsActivity);
            }
        });

        btnUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToUploadActivity = new Intent(getActivity(), UploadActivity.class);
                startActivity(goToUploadActivity);
                /*Intent intent = new Intent(getFragment().getContext(),NewActivity.class);
                getFragment().getContext().startActivity(intent);

                FragmentManager fm = CurrentFragment.getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.addToBackStack(CurrentFragment.class.getName()).commit();
                fm.executePendingTransactions();*/
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Settings button pressed", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(),LoginActivity.class);
                intent.putExtra("logout","y");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

        return fragmentView ;
    }


    @Override
    public void onStart() {
        super.onStart();
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
