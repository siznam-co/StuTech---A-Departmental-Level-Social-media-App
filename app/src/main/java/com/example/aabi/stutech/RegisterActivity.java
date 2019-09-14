package com.example.aabi.stutech;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.SubMenu;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    ImageView userPhoto, SelectImageBtn;
    static int PReqCode = 1 ;
    static int REQUESCODE = 1 ;
    Uri pickedImgUri;
    private EditText editTextName, editTextEmail, editTextPassword, editStudentRollno, editTeacherIdNo;
    private String semester, batch, section;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    LinearLayout SubjectCheckListView, SectionCheckListView;
    List<String> subjectList = new ArrayList<>();
    List<String> teacherSectionList = new ArrayList<>();
    TextView textViewPanel, sectionViewPanel;
    RelativeLayout relativeLayoutStudent, relativeLayoutTeacher;

    ScrollView scrollView;

    Spinner spinner1, spinner2, spinner3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

       // getSupportActionBar().hide();

        scrollView = findViewById(R.id.register_scroll_view);

        userPhoto = findViewById(R.id.RegisterLogo);
        SelectImageBtn = findViewById(R.id.img_button_click);

        imageToUpload();
        editTextName = findViewById(R.id.RegisterUserName);
        editTextEmail = findViewById(R.id.RegisterEmailText);
        editTextPassword = findViewById(R.id.RegisterPasswordText);
        radioGroup = findViewById(R.id.RegisterDesignation);
        SubjectCheckListView = findViewById(R.id.register_subjects_checklist);
        textViewPanel = findViewById(R.id.register_subject_panel);
        SectionCheckListView = findViewById(R.id.register_section_checklist);
        sectionViewPanel = findViewById(R.id.register_section_panel);

        spinnerForSemester();
        spinnerForBatch();
        spinnerForSection();
        setSectionCheckBoxText();

        editStudentRollno = findViewById(R.id.Register_roll_no);
        editTeacherIdNo = findViewById(R.id.Register_Teacher_id_no);
        relativeLayoutStudent = (RelativeLayout) findViewById(R.id.Register_Student_form);
        relativeLayoutTeacher = (RelativeLayout)findViewById(R.id.Register_Teacher_form);

        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.Register_button).setOnClickListener(this);
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
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(RegisterActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            }

            else
            {
                ActivityCompat.requestPermissions(RegisterActivity.this,
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
    //method to select STUDENT or TEACHER form, based on radio button "Designation".....
    public void selectDesignation(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()){
            //Teachers Registration Form
            case R.id.radioTeacher:
                if(checked)
                    relativeLayoutStudent.setVisibility(View.GONE);
                    relativeLayoutTeacher.setVisibility(View.VISIBLE);
                break;
            //Student Registration Form
            case R.id.radioStudent:
                if(checked)
                    relativeLayoutTeacher.setVisibility(View.GONE);
                    relativeLayoutStudent.setVisibility(View.VISIBLE);
                break;
        }

    }

    private void spinnerForSemester() {
        spinner1 = findViewById(R.id.semester_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.all_semester, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
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
                    textViewPanel.setVisibility(View.VISIBLE);
                    if(subjectList.contains(subjectName)) {
                        showMessage("Already Added! "+subjectName);
                    } else {
                        subjectList.add(subjectName);
                        textViewPanel.setText(Arrays.toString(new List[]{subjectList}));
                    }

                }
                else{
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
    private void spinnerForBatch() {
        spinner2 = findViewById(R.id.batch_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.all_batch, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                setBatch(text);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setBatch(String text) { this.batch = text; }
    private String getBatch(){
        return batch;
    }

    private void spinnerForSection() {
        spinner3 = findViewById(R.id.section_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.all_sections, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter);
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

     @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle already registered user
            showMessage("App not connected to Firebase Yet.");
        }
    }


    private void registerUser() {

        int radioId = radioGroup.getCheckedRadioButtonId();  //get integer address of selected radio
        radioButton = findViewById(radioId);

        final String name = editTextName.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String designation = radioButton.getText().toString().trim();
        final String Rollno = editStudentRollno.getText().toString().trim();
        final String TeacherId = editTeacherIdNo.getText().toString().trim();
        final String UserSemester = getSemester();
        final String StudentBatch = getBatch();
        final String section = getSection();
        // Toast.makeText(RegisterActivity.this, designation, Toast.LENGTH_LONG).show();
        String password = editTextPassword.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError(getString(R.string.input_error_name));
            editTextName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.input_error_email));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.input_error_email_invalid));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.input_error_password));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            editTextPassword.setError(getString(R.string.input_error_password_length));
            editTextPassword.requestFocus();
            return;
        }

        if(!(radioButton.isChecked())){
            radioButton.setError("Please choose your designation");
            radioButton.requestFocus();
            return;
        }

        if (UserSemester.equals("Select Semester")) {
            showMessage("Please select your semester");
            spinner1.requestFocus();
            return;
        }


        if(designation.equals("Student")) {
            if (StudentBatch.equals("Select Session")) {
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
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
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
                                                Student student = new Student(name, email, designation, Rollno, UserSemester, StudentBatch, section,
                                                        currentUser.getUid(),
                                                        imageDownlaodLink, subjectList);
                                                addStudent(student,designation);
                                            } else {
                                                Teacher teacher = new Teacher(name, email, designation, TeacherId, currentUser.getUid(), teacherSectionList, imageDownlaodLink, UserSemester, subjectList);
                                                addTeacher(teacher, designation);
                                            }

                                            UserProfileChangeRequest profleUpdate = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(name)
                                                    .setPhotoUri(uri)
                                                    .build();
                                            currentUser.updateProfile(profleUpdate);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // something goes wrong uploading picture

                                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            progressBar.setVisibility(View.GONE);
                                            scrollView.setVisibility(View.VISIBLE);

                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Please verify all input fields and choose Post Image", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);

                        }
                    }
                });

    }
    // TODO: Adding student to firebase

    private void addStudent(Student student, final String desig) {

        FirebaseMessaging.getInstance().subscribeToTopic(getSection());

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Student").push();

        // get post unique ID and upadte post key
        final String key = myRef.getKey();
        student.setStudentKey(key);



        // add student data to firebase database

        myRef.setValue(student).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //move to home activity
                addUserIdstoFirebase(desig,key);


                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Register Complete", Toast.LENGTH_LONG).show();
                subscribeToUserTopic();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("logout","y");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                getApplicationContext().startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // something goes wrong uploading picture

                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                Toast.makeText(RegisterActivity.this, getString(R.string.Registration_fail), Toast.LENGTH_LONG).show();

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
                Toast.makeText(RegisterActivity.this, "Email Verification is sent to "+ currentUser.getEmail(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // TODO: Adding Teacher to firebase
    private void addTeacher(Teacher teacher, final String desig) {

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Teacher").push();

        // get post unique ID and upadte post key
        final String key = myRef.getKey();
        teacher.setTeacherKey(key);


        // add post data to firebase database

        myRef.setValue(teacher).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                addUserIdstoFirebase(desig,key);
                //move to home activity
                progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Register Complete", Toast.LENGTH_LONG).show();
                subscribeToUserTopic();
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("logout","y");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                getApplicationContext().startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // something goes wrong
                progressBar.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                Toast.makeText(RegisterActivity.this, getString(R.string.Registration_fail), Toast.LENGTH_LONG).show();


            }
        });
    }
        // TODO: Register Activity OnClick method
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Register_button:
                registerUser();
                break;
           // case R.id.radioStudent: case R.id.radioTeacher:
             //   submitRegistrationForm();
               // break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        intent.putExtra("logout","y");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        getApplicationContext().startActivity(intent);
        finish();
    }

    private void subscribeToUserTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(currentUser.getUid().toString());
        FirebaseMessaging.getInstance().subscribeToTopic("announce");
        for(String subject: subjectList){
            FirebaseMessaging.getInstance().subscribeToTopic(subject+"");
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}