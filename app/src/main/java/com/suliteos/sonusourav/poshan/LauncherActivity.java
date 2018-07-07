package com.suliteos.sonusourav.poshan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import static com.suliteos.sonusourav.poshan.SignInActivity.POSHAN_LOGIN_TYPE;
import static com.suliteos.sonusourav.poshan.SignInActivity.POSHAN_PREFS_NAME;
import static com.suliteos.sonusourav.poshan.SignInActivity.POSHAN_PREF_PASSWORD;
import static com.suliteos.sonusourav.poshan.SignInActivity.POSHAN_PREF_USERNAME;
import static com.suliteos.sonusourav.poshan.SignInActivity.Poshan_isLoggedIn;

public class LauncherActivity extends AppCompatActivity {
    public static SharedPreferences.Editor poshanEditor;
    SharedPreferences poshanPref;
    public static String TAG=LauncherActivity.class.getCanonicalName();
    private DatabaseReference databaseReference;
    private DatabaseReference donorRef;
    public static String POSHAN_LOGIN_TYPE="donor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_screen);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        Splash.Builder splash = new Splash.Builder(this, getSupportActionBar());
        splash.perform();

        poshanPref = getApplicationContext().getSharedPreferences(POSHAN_PREFS_NAME, 0);
        poshanEditor = poshanPref.edit();
        poshanEditor.putString(Poshan_isLoggedIn, "false");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users");
        donorRef =databaseReference.child("Donor").getRef();

//login_in_code

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
                                        final Intent mainIntent = new Intent(LauncherActivity.this, MainActivity.class);
                                        LauncherActivity.this.startActivity(mainIntent);
                                        LauncherActivity.this.finish();
                                    }
                                }, 1000);


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
                                }, 1000);
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
            }, 1000);

            Log.d(TAG, "onCreate: Poshan_isLoggedIn=false");

        }


    }
    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


}
