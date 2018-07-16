package com.suliteos.sonusourav.poshan;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignInActivity extends AppCompatActivity {


    private EditText sign_in_username;
    private EditText sign_in_password;
    private Button sign_in_button;
    private CheckBox sign_in_chk_box;
    private TextView forgot_pass;
    private Button sign_in_fb;
    private Button sign_in_google;
    private TextView register;
    String signInUsername;
    String signInPassword;
    public  String POSHAN_PREFS_NAME = "mypref";
    public  String POSHAN_LOGIN_TYPE="PoshanLoginType";
    public  String POSHAN_PREF_USERNAME = "username";
    public  String POSHAN_PREF_PASSWORD = "password";
    public  String Poshan_isLoggedIn = "PoshanIsLoggedIn";
    public  SharedPreferences.Editor poshanEditor;
    public  String TAG;
    private  long back_pressed;
    private FirebaseAuth firebaseAuth;
    private ProgressBar signInProgressBar;
    private int counter = 5;
    private DatabaseReference donorRef;




    protected void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        donorRef = databaseReference.child("Donor").getRef();


        sign_in_username=findViewById(R.id.ed_user_name);
        sign_in_password=findViewById(R.id.ed_password);
        sign_in_button=findViewById(R.id.sign_in_btn);
        sign_in_chk_box=findViewById(R.id.sign_in_chk_box);
        forgot_pass=findViewById(R.id.sign_in_forgot_pass);
        sign_in_fb=findViewById(R.id.fb_sign_in_button);
        sign_in_google=findViewById(R.id.google_sign_in_button);
        register=findViewById(R.id.sign_in_register);
        signInProgressBar =findViewById(R.id.sign_in_progress_bar);
        firebaseAuth = FirebaseAuth.getInstance();


        SharedPreferences poshanPref = getSharedPreferences(POSHAN_PREFS_NAME, 0);
        poshanEditor = poshanPref.edit();
        poshanEditor.putString(Poshan_isLoggedIn, "false");

        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                signInUsername = sign_in_username.getText().toString().trim();
                signInPassword = sign_in_password.getText().toString().trim();

                if ((signInUsername == null) || signInUsername.equals("") || TextUtils.isEmpty(signInUsername) || (signInUsername.length() == 0)) {
                    Toast.makeText(getApplicationContext(), "Enter your username", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (signInPassword.equals("") || (signInPassword == null) || TextUtils.isEmpty(signInPassword) || signInPassword.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Enter your password", Toast.LENGTH_SHORT).show();
                    return;
                }


                signInProgressBar.setVisibility(View.VISIBLE);


                    validate(signInUsername,signInPassword);

                            }
                            });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.setTextColor(getResources().getColor(R.color.black));


                AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
                builder.setTitle("Register").setMessage("Sign up as ")
                        .setCancelable(true)
                        .setPositiveButton("Donor", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent=new Intent(SignInActivity.this,DonorSignUp.class);
                                startActivity(intent);
                                dialog.cancel();

                            }
                        })
                        .setNegativeButton("Hospital", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent=new Intent(SignInActivity.this,HospitalSignUp.class);
                                startActivity(intent);
                                dialog.cancel();

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgot_pass.setTextColor(getResources().getColor(R.color.black));
                Intent intent=new Intent(SignInActivity.this,ForgotPassword.class);
                startActivity(intent);
            }
        });


        sign_in_chk_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_in_chk_box.setTextColor(getResources().getColor(R.color.black));

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        ActionBar signInActionar = getSupportActionBar();
        assert signInActionar != null;
        signInActionar.setHomeButtonEnabled(true);
        signInActionar.setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }



    private void validate(final String userName, String userPassword){


        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    donorRef.child(encodeUserEmail(userName)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                            poshanEditor.putString(POSHAN_LOGIN_TYPE,"donor");
                            poshanEditor.apply();
                            Log.d(POSHAN_LOGIN_TYPE,"donor");
                                sign_in_button.setEnabled(false);

                            }else {
                                poshanEditor.putString(POSHAN_LOGIN_TYPE,"hospital");
                                poshanEditor.apply();
                                Log.d(POSHAN_LOGIN_TYPE,"hospital");

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(SignInActivity.this, "Login Failed "+ databaseError + "\nPlease try again.", Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            counter--;


                        }

                    });


                    if (sign_in_chk_box.isChecked()) {
                        Log.d("Shared Pref","working");
                        poshanEditor.putString(Poshan_isLoggedIn, "true");
                        poshanEditor.putString(POSHAN_PREF_USERNAME, signInUsername);
                        poshanEditor.putString(POSHAN_PREF_PASSWORD, signInPassword);
                        poshanEditor.apply();
                    }
                    checkEmailVerification();
                    signInProgressBar.setVisibility(View.GONE);

                }else{
                    Toast.makeText(SignInActivity.this, "Login Failed\n Please check your email or password", Toast.LENGTH_SHORT).show();
                    counter--;
                    Toast.makeText(SignInActivity.this, "No. of attempts remaining: " + counter, Toast.LENGTH_SHORT).show();
                    if(counter == 0){
                        sign_in_button.setEnabled(false);
                    }
                }
                signInProgressBar.setVisibility(View.GONE);

            }
        });
    }
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        assert firebaseUser != null;
        Boolean emailFlag = firebaseUser.isEmailVerified();

            if (emailFlag){
                Toast.makeText(getApplicationContext(),"Signed In successfully",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(SignInActivity.this,WelcomeActivity.class);
                startActivity(intent);

                finish();
            }else {
                Toast.makeText(this, "Email verification is pending", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            }


    }

    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true);
        } else
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }


    @Override
    protected void onResume() {
        super.onResume();
        forgot_pass.setTextColor(getResources().getColor(R.color.grey));
        register.setTextColor(getResources().getColor(R.color.grey));
        sign_in_chk_box.setTextColor(getResources().getColor(R.color.grey));
        sign_in_button.setEnabled(true);
    }

}
