package com.suliteos.sonusourav.poshan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appus.splash.Splash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class LauncherActivity extends AppCompatActivity implements Animation.AnimationListener {
    public static SharedPreferences.Editor poshanEditor;
    SharedPreferences poshanPref;
    public static String TAG=LauncherActivity.class.getCanonicalName();
    private DatabaseReference databaseReference;
    private DatabaseReference donorRef;
    Animation animFadeIn;
    public  String POSHAN_LOGIN_TYPE="PoshanLoginType";
    public  String POSHAN_PREFS_NAME = "mypref";
    public  String POSHAN_PREF_USERNAME = "username";
    public  String POSHAN_PREF_PASSWORD = "password";
    public  String Poshan_isLoggedIn = "PoshanIsLoggedIn";

    LinearLayout linearLayout;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_screen);



        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        // set animation listener
        animFadeIn.setAnimationListener(this);
        // animation for image
        linearLayout = (LinearLayout) findViewById(R.id.launcher_linear_layout);
        // start the animation
        linearLayout.startAnimation(animFadeIn);



      /*  Splash.Builder splash = new Splash.Builder(this, getSupportActionBar());
        splash.perform();*/

        poshanPref = getApplicationContext().getSharedPreferences(POSHAN_PREFS_NAME, 0);
        poshanEditor = poshanPref.edit();
        poshanEditor.putString(Poshan_isLoggedIn, "false");
        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        donorRef =databaseReference.child("Donor").getRef();

//login_in_code


    }
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


    @Override
    public void onAnimationStart(Animation animation) {

        if (Objects.equals(poshanPref.getString(Poshan_isLoggedIn, null), "true")) {
            Log.d(TAG, "Launcher:onCreate: Poshan_isLoggedIn=true");
            final String launcherSignInUsername = poshanPref.getString(POSHAN_PREF_USERNAME, null);
            String launcherSignInPassword = poshanPref.getString(POSHAN_PREF_PASSWORD, null);


            assert launcherSignInUsername != null;
            assert launcherSignInPassword != null;
            (firebaseAuth.signInWithEmailAndPassword(launcherSignInUsername, launcherSignInPassword))
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                donorRef.child(encodeUserEmail(launcherSignInUsername)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            poshanEditor.putString(POSHAN_LOGIN_TYPE,"donor");
                                            poshanEditor.apply();
                                            Log.d(POSHAN_LOGIN_TYPE,"donor");
                                        }else {
                                            poshanEditor.putString(POSHAN_LOGIN_TYPE,"hospital");
                                            Log.d(POSHAN_LOGIN_TYPE,"hospital");
                                            poshanEditor.apply();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        FirebaseAuth.getInstance().signOut();
                                        startActivity(new Intent(LauncherActivity.this,SignInActivity.class));
                                        Toast.makeText(LauncherActivity.this, "Login Failed\n Please try again.", Toast.LENGTH_SHORT).show();


                                    }
                                });
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Intent mainIntent = new Intent(LauncherActivity.this, WelcomeActivity.class);
                                        LauncherActivity.this.startActivity(mainIntent);
                                        LauncherActivity.this.finish();
                                    }
                                }, 3500);


                            } else {
                                Log.e("ERROR", task.getException().toString());
                                Log.d(TAG, "onCreateError: Poshan_isLoggedIn=false");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Intent mainIntent = new Intent(LauncherActivity.this, SignInActivity.class);
                                        LauncherActivity.this.startActivity(mainIntent);
                                        LauncherActivity.this.finish();
                                    }
                                }, 3500);
                                Toast.makeText(LauncherActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        } else {
            /*Intent i = new Intent(LauncherActivity.this, SignInActivity.class);
            startActivity(i);*/
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Intent mainIntent = new Intent(LauncherActivity.this, SignInActivity.class);
                    LauncherActivity.this.startActivity(mainIntent);
                    LauncherActivity.this.finish();
                }
            }, 3500);

            Log.d(TAG, "onCreate: Poshan_isLoggedIn=false");

        }

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
