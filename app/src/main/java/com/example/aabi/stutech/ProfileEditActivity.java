package com.example.aabi.stutech;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileEditActivity extends AppCompatActivity implements View.OnClickListener {

    String userKey, designation;
    ImageView userPhoto, SelectImageBtn;
    static int PReqCode = 1 ;
    static int REQUESCODE = 1 ;
    Uri pickedImgUri;
    private EditText editStudentRollno, editTeacherIdNo;
    private String semester, Session, section;
    private ProgressBar progressBar;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    LinearLayout SubjectCheckListView, SectionCheckListView;
    List<String> subjectList = new ArrayList<>();
    List<String> teacherSectionList = new ArrayList<>();
    List<String> tempSubjectList = new ArrayList<>();


    TextView textViewPanel, sectionViewPanel;
    RelativeLayout relativeLayoutStudent, relativeLayoutTeacher;

    ScrollView scrollView;

    Spinner spinner1, spinner2, spinner3;

    boolean ifButtonReSubmitPressed= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        userKey = getIntent().getExtras().getString("userKey");
        designation = getIntent().getExtras().getString("designation");

        //showMessage(userKey.toString()+designation.toString());

        scrollView = findViewById(R.id.register_scroll_view);

        userPhoto = findViewById(R.id.RegisterLogo);
        SelectImageBtn = (ImageView)findViewById(R.id.img_button_click);

        imageToUpload();

        SubjectCheckListView = findViewById(R.id.register_subjects_checklist);
        textViewPanel = findViewById(R.id.register_subject_panel);
        SectionCheckListView = findViewById(R.id.register_section_checklist);
        sectionViewPanel = findViewById(R.id.register_section_panel);
        editStudentRollno = findViewById(R.id.Register_roll_no);
        editTeacherIdNo = findViewById(R.id.Register_Teacher_id_no);
        relativeLayoutStudent = (RelativeLayout) findViewById(R.id.Register_Student_form);
        relativeLayoutTeacher = (RelativeLayout)findViewById(R.id.Register_Teacher_form);

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        getUserData(userKey, designation);

        findViewById(R.id.re_submit_button).setOnClickListener(this);

    }

    private void getUserData(String userKey, final String designation) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(designation).child(userKey);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Glide.with(getApplicationContext()).load(currentUser.getPhotoUrl()).into(userPhoto);

                if(designation.equals("Teacher")){
                    //TODO: Teacher data fetching

                    relativeLayoutStudent.setVisibility(View.GONE);
                    relativeLayoutTeacher.setVisibility(View.VISIBLE);

                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                    editTeacherIdNo.setText(teacher.getTeacherId());

                    subjectList.addAll(teacher.getSubjectList());

                    for(String subject: subjectList){
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(subject+"");
                    }

                    Matcher matcher = Pattern.compile("\\d+").matcher(teacher.getSemester());
                    matcher.find();
                    int semester = Integer.valueOf(matcher.group());
                    spinnerForSemester(semester);

                    teacherSectionList.addAll(teacher.getSection());
                    sectionViewPanel.setText(Arrays.toString(new List[]{teacherSectionList}));
                    sectionViewPanel.setVisibility(View.VISIBLE);

                    setSectionCheckBoxText();

                }else{//TODO: Student data fetching

                    relativeLayoutTeacher.setVisibility(View.GONE);
                    relativeLayoutStudent.setVisibility(View.VISIBLE);

                    Student student = dataSnapshot.getValue(Student.class);
                    editStudentRollno.setText(student.getRoll());

                    subjectList.addAll(student.getSubjectList());
                    for(String subject: subjectList){
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(subject+"");
                    }

                    FirebaseMessaging.getInstance().unsubscribeFromTopic(student.getSection());

                    Matcher matcher = Pattern.compile("\\d+").matcher(student.getSemester());
                    matcher.find();
                    int semester = Integer.valueOf(matcher.group());
                    spinnerForSemester(semester);

                    spinnerForSession(student.getBatch());
                    spinnerForSection(student.getSection());
                }

                textViewPanel.setVisibility(View.VISIBLE);
                textViewPanel.setText(Arrays.toString(new List[]{subjectList}));
                tempSubjectList.addAll(subjectList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void imageToUpload() {
        SelectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT>=22){
                    checkAndRequestForPermission();
                }else{
                    openGallery();
                }

            }
        });
    }

    private void openGallery() {
        //TODO: open gallery intent and wait for user to pick an image !

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(ProfileEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(ProfileEditActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }

            else
            {
                ActivityCompat.requestPermissions(ProfileEditActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }

        }
        else
            openGallery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null ) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.getData();
            userPhoto.setImageURI(pickedImgUri);


        }


    }

    private void spinnerForSemester(int semester) {
        spinner1 = findViewById(R.id.semester_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.all_semester, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);

        spinner1.setSelection(semester);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                ArrayList<String> mTestArray;

                switch (text) {
                    case "1st":
                        SubjectCheckListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.First)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "2nd":
                        SubjectCheckListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Second)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "3rd":
                        SubjectCheckListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Third)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "4th":
                        SubjectCheckListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Fourth)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "5th":
                        SubjectCheckListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Fifth)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "6th":
                        SubjectCheckListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Sixth)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "7th":
                        SubjectCheckListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Seventh)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    case "8th":
                        SubjectCheckListView.removeAllViews();
                        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.Eighth)));
                        setSubjectCheckBoxText(mTestArray);
                        break;
                    default:
                        SubjectCheckListView.removeAllViews();
                        break;
                }

                setSemester(text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
            CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setId(i);
            checkBox.setText(mTestArray.get(i));
            checkBox.setTextColor(getResources().getColor(R.color.color_black));
            if(subjectList.contains(checkBox.getText().toString()))
                checkBox.setChecked(true);
            checkBox.setOnClickListener(getOnClickDoSomething(checkBox));
            SubjectCheckListView.addView(checkBox);
        }
    }

    private View.OnClickListener getOnClickDoSomething(final CheckBox checkBox) {
        return new View.OnClickListener() {
            String subjectName = checkBox.getText().toString();
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()) {
                    if(subjectList.contains(subjectName)) {
                        Toast.makeText(getApplicationContext(), "Already Added! "+subjectName, Toast.LENGTH_SHORT).show();
                    } else{
                        subjectList.add(subjectName);
                        textViewPanel.setText(Arrays.toString(new List[]{subjectList}));
                    }

                } else{
                    subjectList.remove(subjectName);
                    textViewPanel.setText(Arrays.toString(new List[]{subjectList}));
                }
            }
        };
    }

    private void setSectionCheckBoxText() {
        ArrayList<String> mTestArray;
        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.teacher_sections)));
        for(int i=0; i<mTestArray.size(); i++){
            final CheckBox checkBox = new CheckBox(getApplicationContext());
            checkBox.setId(i);
            checkBox.setText(mTestArray.get(i));
            checkBox.setTextColor(getResources().getColor(R.color.color_black));
            if(teacherSectionList.contains(checkBox.getText().toString()))
                checkBox.setChecked(true);
            SectionCheckListView.addView(checkBox);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = checkBox.getText().toString();
                    if(checkBox.isChecked()) {
                        sectionViewPanel.setVisibility(View.VISIBLE);
                        if(teacherSectionList.contains(text)) {
                            showMessage("Already Added! "+text);
                        } else {
                            teacherSectionList.add(text);
                            sectionViewPanel.setText(Arrays.toString(new List[]{teacherSectionList}));
                        }

                    }
                    else{
                        teacherSectionList.remove(text);
                        sectionViewPanel.setText(Arrays.toString(new List[]{teacherSectionList}));
                    }
                }
            });
        }
    }
    private void spinnerForSession(String session) {
        spinner2 = findViewById(R.id.batch_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.all_batch, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner2.setAdapter(adapter);
        ArrayList<String> mTestArray;
        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.all_batch)));

        final int index = mTestArray.indexOf(session);
        spinner2.setSelection(index);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                setSession(text);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSession(String text) { this.Session = text; }
    private String getSession(){
        return Session;
    }

    private void spinnerForSection(String section) {
        spinner3 = findViewById(R.id.section_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.all_sections, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter);
        ArrayList<String> mTestArray;
        mTestArray = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.all_sections)));

        int index = mTestArray.indexOf(section);
        spinner3.setSelection(index);


        spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                setSection(text);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSection(String text) { this.section = text; }
    private String getSection(){
        return section;
    }


    private void ReSubmitUser() {

        final String Rollno = editStudentRollno.getText().toString().trim();
        final String TeacherId = editTeacherIdNo.getText().toString().trim();
        final String UserSemester = getSemester();
        final String StudentSession = getSession();
        final String section = getSection();
        // Toast.makeText(RegisterActivity.this, designation, Toast.LENGTH_LONG).show();

        if (UserSemester.equals("Select Semester")) {
            showMessage("Please select your semester");
            spinner1.requestFocus();
            return;
        }


        if(designation.equals("Student")) {
            if (StudentSession.equals("Select Session")) {
                showMessage("Please select your Session");
                spinner2.requestFocus();
                return;
            }
            if (section.equals("Select Section")) {
                showMessage("Please select your Section");
                spinner3.requestFocus();
                return;
            }
        }
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        if(pickedImgUri != null) {
            StorageReference delRef = FirebaseStorage.getInstance().getReferenceFromUrl(currentUser.getPhotoUrl().toString());
            delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("users_photos");
                    final StorageReference imageFilePath = storageReference.child(pickedImgUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownlaodLink = uri.toString();

                                    // Add student to firebase database
                                    if (designation.equals("Student")) {
                                        // create student Object
                                        Student student = new Student(currentUser.getDisplayName(), currentUser.getEmail(), designation, Rollno, UserSemester,
                                                StudentSession, section,
                                                currentUser.getUid(),
                                                imageDownlaodLink, subjectList);
                                        addStudent(student, designation);
                                    } else {
                                        Teacher teacher = new Teacher(currentUser.getDisplayName(), currentUser.getEmail(), designation, TeacherId,
                                                currentUser.getUid(), teacherSectionList, imageDownlaodLink, UserSemester, subjectList);
                                        addTeacher(teacher, designation);
                                    }

                                    UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(currentUser.getDisplayName())
                                            .setPhotoUri(uri)
                                            .build();
                                    currentUser.updateProfile(profleUpdate);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // something goes wrong uploading picture

                                    Toast.makeText(ProfileEditActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    scrollView.setVisibility(View.VISIBLE);

                                }
                            });
                        }
                    });                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "File not deleted", Toast.LENGTH_SHORT).show();
                }
            });


        }//TODO: if user has not change the picture
        else{
            if (designation.equals("Student")) {
                // create student Object
                Student student = new Student(currentUser.getDisplayName(), currentUser.getEmail(), designation, Rollno, UserSemester,
                        StudentSession, section,
                        currentUser.getUid(),
                        currentUser.getPhotoUrl().toString(), subjectList);
                addStudent(student, designation);
            } else {
                Teacher teacher = new Teacher(currentUser.getDisplayName(), currentUser.getEmail(), designation, TeacherId,
                        currentUser.getUid(), teacherSectionList, currentUser.getPhotoUrl().toString(), UserSemester, subjectList);
                addTeacher(teacher, designation);
            }
        }

    }
    // TODO: Adding student to firebase

    private void addStudent(Student student, final String desig) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Student").child(userKey);

        // add student data to firebase database
        student.setStudentKey(userKey);

        myRef.setValue(student).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //move to home activity
                addUserIdstoFirebase(desig,userKey);


                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileEditActivity.this, "User details updated!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // something goes wrong uploading picture

                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                Toast.makeText(ProfileEditActivity.this, "User detail update failed!", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void addUserIdstoFirebase(String desig, String key) {
        User user = new User(desig, key);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UserIDs");
        myRef.child(currentUser.getUid()).setValue(user);

        currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ProfileEditActivity.this, "Details updated by "+ currentUser.getEmail(), Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
    }

    // TODO: Adding Teacher to firebase
    private void addTeacher(Teacher teacher, final String desig) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Teacher").child(userKey);

        // add post data to firebase database
        teacher.setTeacherKey(userKey);

        myRef.setValue(teacher).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                addUserIdstoFirebase(desig,userKey);
                //move to home activity
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ProfileEditActivity.this, "User details updated!", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // something goes wrong
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                Toast.makeText(ProfileEditActivity.this,"User detail update failed!", Toast.LENGTH_LONG).show();


            }
        });
    }
    // TODO: Register Activity OnClick method

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        subscribeToUserTopic();
        super.onBackPressed();
        finish();
    }

    private void subscribeToUserTopic() {
        if(designation.equals("Student")){
            FirebaseMessaging.getInstance().subscribeToTopic(getSection());
        }
        FirebaseMessaging.getInstance().subscribeToTopic(currentUser.getUid().toString());
        FirebaseMessaging.getInstance().subscribeToTopic("announce");
        for(String subject: subjectList){
            FirebaseMessaging.getInstance().subscribeToTopic(subject+"");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.re_submit_button) {
                ReSubmitUser();
        }
    }

    /*@Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }*/
}
