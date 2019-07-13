package com.example.aabi.stutech;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    EditText txtEmail, txtPassword;
    Button login_button;
    ProgressBar progressBar;
    private CheckBox checkBox;
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "PrefsFile";
    LinearLayout logiLayout, noInternetConnectionLayout;
    static String logoutValue ;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        logiLayout = findViewById(R.id.login_Layout);
        noInternetConnectionLayout = findViewById(R.id.no_internet_connection);
        logiLayout.setVisibility(View.VISIBLE);

        txtEmail = findViewById(R.id.activity_login_user);
        txtPassword = findViewById(R.id.activity_login_password);
        login_button = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressbarLogin);
        checkBox = (CheckBox) findViewById(R.id.checkBoxRememberMe);
        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = txtEmail.getText().toString().trim();
                final String password = txtPassword.getText().toString().trim();

               SignIn(email,password); //Calling sign in method to log in into the app

            }
        });

        logoutValue = getIntent().getExtras().getString("logout");
        getPreferencesData();

    }

    private void SignIn(final String email, final String password) {
        if (email.isEmpty()) {
            txtEmail.setError(getString(R.string.input_error_email));
            txtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError(getString(R.string.input_error_email_invalid));
            txtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            txtPassword.setError(getString(R.string.input_error_password));
            txtPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            txtPassword.setError(getString(R.string.input_error_password_length));
            txtPassword.requestFocus();
            return;
        }


        logiLayout.setVisibility(View.INVISIBLE);
        //Login user
        progressBar.setVisibility(View.VISIBLE);

        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        if (info == null){
            Toast.makeText(LoginActivity.this,"No Internet Connection",Toast.LENGTH_LONG).show();
            logiLayout.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            noInternetConnectionLayout.setVisibility(View.VISIBLE);
            noInternetConnectionLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noInternetConnectionLayout.setVisibility(View.INVISIBLE);
                    getPreferencesData();
                }
            });
        }else{

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            //if(mAuth.getCurrentUser().isEmailVerified()) {
                                //Saving preferences
                                Boolean bool = checkBox.isChecked();
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("pref_email",email);
                                editor.putString("pref_password",password);
                                editor.putBoolean("pref_checkBox",bool);
                                editor.apply();

                                FirebaseMessaging.getInstance().subscribeToTopic(mAuth.getCurrentUser().getUid().toString());

                                Intent subjectIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(subjectIntent);
                                finish();
                            /*}else{
                                Toast.makeText(LoginActivity.this, "Email not Verified Yet", Toast.LENGTH_LONG).show();
                                logiLayout.setVisibility(View.VISIBLE);
                            }*/


                                //Toast.makeText(getApplicationContext(),"Preferences Saved Successfully", Toast.LENGTH_SHORT).show();

                                //preferences.edit().clear().apply();
                           /* if(!checkBox.isChecked()){
                                txtEmail.setText(email);
                                txtPassword.setText(password);
                            }else{
                                txtEmail.getText().clear();
                                txtPassword.getText().clear();
                            }*/

                        } else {
                            logiLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }
    String em = "", p = "" ;
    Boolean ch = false;
    private void getPreferencesData(){
        //logiLayout.setVisibility(View.INVISIBLE);

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if(sp.contains("pref_email") && sp.contains("pref_password")){
            em = sp.getString("pref_email", "not found");
            p = sp.getString("pref_password", "not found");
            if(logoutValue.equals("n")){
                SignIn(em,p);
            }
        }
        if(sp.contains("pref_checkBox")){
            ch = sp.getBoolean("pref_checkBox",false);
            checkBox.setChecked(ch);
            if(ch){
                txtEmail.setText(em.toString());
                txtPassword.setText(p.toString());
            }
        }
        if(logoutValue.equals("y")){
            logiLayout.setVisibility(View.VISIBLE);
            if(ch){
                txtEmail.setText(em.toString());
                txtPassword.setText(p.toString());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("pref_email",em);
                editor.putString("pref_password",p);
                editor.putBoolean("pref_checkBox",ch);
                editor.apply();
            }else
            {
                sp.edit().clear().apply();
            }
        }

    }

    public void Register(View view) {
        Intent RegisterIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(RegisterIntent);
        finish();
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.moveTaskToBack(true);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        },2000);
    }
}
