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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public static String PREFS_NAME = "mypref";
    public static String PREF_USERNAME = "username";
    public static String PREF_PASSWORD = "password";
    public static SharedPreferences.Editor editor;
    public static String isLoggedIn = "false";
    public static String TAG;
    private SharedPreferences pref;
    private static long back_pressed;
    private ActionBar signInActionar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private int counter = 5;




    protected void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }




        sign_in_username=findViewById(R.id.ed_user_name);
        sign_in_password=findViewById(R.id.ed_password);
        sign_in_button=findViewById(R.id.sign_in_btn);
        sign_in_chk_box=findViewById(R.id.sign_in_chk_box);
        forgot_pass=findViewById(R.id.sign_in_forgot_pass);
        sign_in_fb=findViewById(R.id.fb_sign_in_button);
        sign_in_google=findViewById(R.id.google_sign_in_button);
        register=findViewById(R.id.sign_in_register);
        pref = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        editor = pref.edit();
        editor.putString(isLoggedIn, "false");
        progressBar=findViewById(R.id.sign_in_progress_bar);
        firebaseAuth = FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();


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


                if (sign_in_chk_box.isChecked()) {
                    editor.putString(isLoggedIn, "true");
                    editor.putString(PREF_USERNAME, signInUsername);
                    editor.putString(PREF_PASSWORD, signInPassword);
                    editor.apply();
                    }

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
                forgot_pass.setTextColor(getResources().getColor(R.color.black));

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        signInActionar = getSupportActionBar();
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




    private void validate(String userName, String userPassword){

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    checkEmailVerification();
                    progressBar.setVisibility(View.GONE);

                }else{
                    Toast.makeText(SignInActivity.this, "Login Failed\n Please check your email or password", Toast.LENGTH_SHORT).show();
                    counter--;
                    Toast.makeText(SignInActivity.this, "No. of attempts remaining: " + counter, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    if(counter == 0){
                        sign_in_button.setEnabled(false);
                    }
                }
            }
        });
    }
    private void checkEmailVerification(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            Boolean emailFlag = firebaseUser.isEmailVerified();

            if (emailFlag){
                Toast.makeText(getApplicationContext(),"Signed In successfully",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(SignInActivity.this,MainActivity.class);
                intent.putExtra("userEmail",signInUsername);
                startActivity(intent);

                finish();
            }else {
                Toast.makeText(this, "Email verification is pending", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
            }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
