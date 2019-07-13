package com.example.aabi.stutech;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AnnounceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PERMISSION_STORAGE_CODE = 124 ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    RecyclerView postRecyclerView ;
    PostAdapter postAdapter ;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference ;
    List<Post> postList = new ArrayList<>();
    Query query1;

    private static final int PReqCode = 2 ;
    private static final int PICK_IMAGE_REQUEST = 3;
    String fileType = "";
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    static Dialog popAddPost;
    ImageView popupUserImage,popupPostImage,popupAddBtn, popupAddPictureBtn, popupAddFileBtn, popupCaptureCameraBtn;
    TextView popupTitle,popupDescription, popupFileUploadName;
    static String subjectName;
    ProgressBar popupClickProgress;
    //a Uri object to store file path
    private Uri filePath = null;
    Uri imageUri;
    String imageDownlaodLink;

    SwipeRefreshLayout swipeRefreshLayout;





    public AnnounceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnnounceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnnounceFragment newInstance(String param1, String param2) {
        AnnounceFragment fragment = new AnnounceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View fragmentView = inflater.inflate(R.layout.fragment_announce, container, false);
        postRecyclerView  = fragmentView.findViewById(R.id.HomePostRV);
        postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRecyclerView.setHasFixedSize(true);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Posts");
        //relativeLayout = fragmentView.findViewById(R.id.t);
        //FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        iniPopup();

        ImageView fab = fragmentView.findViewById(R.id.fab_announce);
        //if(AttendanceActivity.subjectName.equals("all"))
            //relativeLayout.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });

        swipeRefreshLayout = fragmentView.findViewById(R.id.announce_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                onStart();
            }
        });


        return fragmentView ;
    }


    @Override
    public void onStart() {
        super.onStart();

        // Get List Posts from the database
        /*if(AttendanceActivity.subjectName.equals("all")){
            query1 = FirebaseDatabase.getInstance().getReference("Posts");
        }else if(AttendanceActivity.subjectName.equals("announce")){*/
            query1 = FirebaseDatabase.getInstance().getReference("Posts")
                    .orderByChild("subjectName")
                    .equalTo("announce");
        /*}else {
            query1 = FirebaseDatabase.getInstance().getReference("Posts")
                    .orderByChild("subjectName")
                    .equalTo(AttendanceActivity.subjectName);
        }*/
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                postList.clear();
                for (DataSnapshot postsnap: dataSnapshot.getChildren()) {
                    Post post = postsnap.getValue(Post.class);
                    postList.add(post) ;
                }
                Collections.reverse(postList);

                swipeRefreshLayout.setRefreshing(false);
                postAdapter = new PostAdapter(getActivity(),postList);
                postRecyclerView.setAdapter(postAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



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

    public void iniPopup() {
        popAddPost = new Dialog(getActivity());
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;
        final Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.scale);

        // ini popup widgets
        popupUserImage = popAddPost.findViewById(R.id.popup_user_image);
        popupPostImage = popAddPost.findViewById(R.id.popup_img);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupAddPictureBtn = popAddPost.findViewById(R.id.upload_picture_btn);
        popupAddFileBtn = popAddPost.findViewById(R.id.upload_file_btn);
        popupCaptureCameraBtn = popAddPost.findViewById(R.id.capture_camera_btn);

        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupFileUploadName = popAddPost.findViewById(R.id.file_upload_name);

        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupClickProgress = popAddPost.findViewById(R.id.popup_progressBar);

        // load Current user profile photo

        Glide.with(getActivity()).load(currentUser.getPhotoUrl()).into(popupUserImage);


        //TODO: Handle pop up camera button clicked
        popupCaptureCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupCaptureCameraBtn.startAnimation(anim);
                fileType = "camera" ;
                checkAndRequestForPermission(fileType);
            }
        });
        // Add post click Listener
        //TODO: Handle pop up image clicked
        popupAddPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // here when image clicked we need to open the gallery
                // before we open the gallery we need to check if our app have the access to user files
                // we did this before in register activity I'm just going to copy the code to save time ...
                popupAddPictureBtn.startAnimation(anim);
                fileType = "image";
                checkAndRequestForPermission(fileType);
                //showFileChooser

            }
        });
        //TODO: Handle popUp file upload button clicked
        popupAddFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddFileBtn.startAnimation(anim);
                fileType = "file";
                checkAndRequestForPermission(fileType);

            }
        });

        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupAddBtn.setVisibility(View.INVISIBLE);
                popupClickProgress.setVisibility(View.VISIBLE);

                // we need to test all input fields (Title and description ) and post image

                if (!popupTitle.getText().toString().isEmpty()
                        && !popupDescription.getText().toString().isEmpty()) {
                    uploadFile();
                }
                else {
                    showMessage("Please verify all input fields") ;
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupClickProgress.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void checkAndRequestForPermission(String type) {

        //TODO: External Storage
        if(type.equals("file") || type.equals("image")) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // everything goes well : we have permission to access user gallery
                //openGallery();
                this.fileType = type;
                showFileChooser(fileType);

            } else

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
        }else{
            //TODO Camera
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    // everything goes well : we have permission to access user gallery
                    //openGallery();
                    this.fileType = type;
                    showFileChooser(fileType);

                } else{
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            PReqCode);
                }
            }else
                showFileChooser(fileType);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==PReqCode && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            showMessage("Permission granted");
            //showFileChooser(fileType);
        }
        else{
            showMessage("Please accept Required permission");
        }
    }



    //TODO: method to show file chooser
    private void showFileChooser(String type) {
        if(type.equals("image")) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }
        else if(type.equals("camera")){

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
            this.imageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values  );
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(Intent.createChooser(intent, "captured Picture"), PICK_IMAGE_REQUEST);

            //showFileChooser("image");
        }
        else{
            String[] mimeTypes =
                    {"application/pdf", "application/msword", "application/vnd.ms-powerpoint", "application/vnd.ms-excel", "text/plain"};

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                if (mimeTypes.length > 0) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }
            } else {
                String mimeTypesStr = "";
                for (String mimeType : mimeTypes) {
                    mimeTypesStr += mimeType + "|";
                }
                intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
            }
            startActivityForResult(Intent.createChooser(intent, "ChooseFile"), PICK_IMAGE_REQUEST);
        }
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if (fileType.equals("camera")) {
                //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                popupPostImage.setVisibility(View.VISIBLE);
                this.filePath = imageUri;
                popupPostImage.setImageURI(imageUri);

            } else {
                super.onActivityResult(requestCode, resultCode, data);
                this.filePath = data.getData();

                if (requestCode == PICK_IMAGE_REQUEST && data.getData() != null) {

                    if (fileType.equals("image")) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                            popupPostImage.setVisibility(View.VISIBLE);
                            popupPostImage.setImageBitmap(bitmap);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }/*else if(fileType.equals("camera")){
                Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                popupPostImage.setVisibility(View.VISIBLE);
                popupPostImage.setImageBitmap(bitmap);
            }*/ else {
                        String temp = filePath.toString();
                        String[] bits = temp.split("/");
                        String lastOne = bits[bits.length - 1];
                        popupFileUploadName.setText(getCompleteFileName(filePath));
                        popupFileUploadName.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    //this method will upload the file
    private void uploadFile() {
        //if there is a file to upload
        if (filePath != null || fileType.equals("camera") ) {
            //displaying a progress dialog while upload is going on
            /*final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();*/

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Post_Files");
            final StorageReference imageFilePath = storageReference.child(filePath.getLastPathSegment());
            imageFilePath.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageDownlaodLink = uri.toString();

                                    // Add post to firebase database using uploaded image
                                    addPost(imageDownlaodLink,fileType,getCompleteFileName(filePath));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // something goes wrong uploading picture

                                    showMessage(e.getMessage());
                                    popupClickProgress.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            /*progressDialog.dismiss();*/

                            //and displaying error message
                            Toast.makeText(getActivity().getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
            addPost("No","no", "no");
            //popupPostImage.setVisibility(View.INVISIBLE);
        }
        //----------------------------
    }

    private String getCompleteFileName(Uri uri)
    {
        String fileName=uri.toString(), path;
        /*ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();*/
        if(fileType.equals("image")){
            String[] bits = fileName.split("/");
            fileName = bits[bits.length - 1];

        }else{
            path = getRealPathFromURI(uri);
            fileName = path.substring(path.lastIndexOf("/")+1);
        }
        /*String file;
        if (filename.indexOf(".") > 0) {
            file = filename.substring(0, filename.lastIndexOf("."));
        } else {
            file =  filename;
        }y

        fileName= file + mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));*/
        return fileName;
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = getActivity().getApplicationContext().getContentResolver().query(uri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    private void addPost(String imageDownlaodLink, String ft, String completeFileName) {

        // create post Object
        Post post = new Post(popupTitle.getText().toString(),
                popupDescription.getText().toString(),
                imageDownlaodLink,
                currentUser.getUid(),
                currentUser.getPhotoUrl().toString(), "announce", currentUser.getDisplayName(), ft, completeFileName);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        // get post unique ID and upadte post key
        final String key = myRef.getKey();
        post.setPostKey(key);


        // add post data to firebase database

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                /*DatabaseReference notifyRef = FirebaseDatabase.getInstance().getReference("Notifications").push();
                Notifications notification = new Notifications("Announcement", key, currentUser.getDisplayName(), currentUser.getPhotoUrl().toString(), notifyRef.getKey());
                notifyRef.setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });*/

                showMessage("Post Added successfully");

                popupClickProgress.setVisibility(View.INVISIBLE);
                popupAddBtn.setVisibility(View.VISIBLE);
                popupDescription.setText("");
                popupTitle.setText("");
                popupPostImage.setVisibility(View.GONE);
                popupFileUploadName.setVisibility(View.GONE);
                popAddPost.dismiss();

                /*DatabaseReference notificationReference = FirebaseDatabase.getInstance()
                        .getReference("Notifications").child(key);
                notificationReference.setValue(key);*/
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

    }
    public void notice(String nf){



        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.drawable.stutech_logo) //set icon for notification
                        .setContentTitle("Notifications Example") //set title of notification
                        .setContentText(nf)//this is notification message
                        .setAutoCancel(true) // makes auto cancel of notification
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT); //set priority of notification


        Intent notificationIntent = new Intent(getContext(), HomeActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //notification message will get at NotificationView
        notificationIntent.putExtra("message", "This is a notification message");

        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());


    }
}
