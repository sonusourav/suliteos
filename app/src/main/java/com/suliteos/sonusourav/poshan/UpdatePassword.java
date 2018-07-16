package com.suliteos.sonusourav.poshan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UpdatePassword extends AppCompatActivity {

    private EditText newPassword;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference rootRef;
    private EditText oldPassword;
    private static SharedPreferences.Editor poshanEditor;
    private SharedPreferences poshanPref;
    public  String POSHAN_LOGIN_TYPE="PoshanLoginType";
    public  String POSHAN_PREFS_NAME = "mypref";
    public  String POSHAN_PREF_PASSWORD = "password";
    private ProgressBar updateProgressBar;
    private Button update;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_password);

         update = findViewById(R.id.btnUpdatePassword);
        newPassword = findViewById(R.id.etNewPassword);
        oldPassword = findViewById(R.id.etOldPassword);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        rootRef =firebaseDatabase.getReference().child("Users");
        assert firebaseUser != null;
        final String email = firebaseUser.getEmail();
        poshanPref = getApplicationContext().getSharedPreferences(POSHAN_PREFS_NAME, 0);
        poshanEditor = poshanPref.edit();
        updateProgressBar=findViewById(R.id.update_progress_bar);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateProgressBar.setVisibility(View.VISIBLE);
                update.setEnabled(false);
                String oldPass =oldPassword.getText().toString().trim();
                final String newPass =newPassword.getText().toString().trim();

                assert email != null;
                AuthCredential credential = EmailAuthProvider.getCredential(email,oldPass);


                firebaseUser.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                poshanEditor.putString(POSHAN_PREF_PASSWORD, newPass);
                                                poshanEditor.apply();


                                                if(Objects.equals(poshanPref.getString(POSHAN_LOGIN_TYPE, null), "donor")){
                                                     userRef=rootRef.child("Donor").child(encodeUserEmail(email)).getRef();
                                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.exists()){
                                                                dataSnapshot.getRef().child("donorPass").setValue(newPass);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            Toast.makeText(getApplicationContext(),"Database update failed",Toast.LENGTH_SHORT).show();
                                                            update.setEnabled(true);
                                                            finish();
                                                        }
                                                    });
                                                     }
                                                     else{
                                                    userRef=rootRef.child("Hospital").child(encodeUserEmail(email)).getRef();
                                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot.exists()){
                                                                dataSnapshot.getRef().child("hosPass").setValue(newPass);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            update.setEnabled(true);
                                                            Toast.makeText(getApplicationContext(),"Database update failed",Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        }
                                                    });
                                                }




                                                Toast.makeText(getApplicationContext(),"Password successfully updated",Toast.LENGTH_SHORT).show();
                                                Log.d("Update Password", "Password updated");
                                                startActivity(new Intent(UpdatePassword.this,WelcomeActivity.class));
                                            } else {
                                                Toast.makeText(getApplicationContext(),"Password update failed. Try Again!",Toast.LENGTH_SHORT).show();
                                                update.setEnabled(true);
                                                Log.d("Update Password", "Error password not updated");
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(),"Error : Wrong old password.",Toast.LENGTH_SHORT).show();
                                    update.setEnabled(true);
                                    Log.d("Update Password", "Error auth failed");
                                }

                            updateProgressBar.setVisibility(View.GONE);}

                        });



            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {

        ActionBar updateActionBar = getSupportActionBar();
        assert updateActionBar != null;
        updateActionBar.setHomeButtonEnabled(true);
        updateActionBar.setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


    @Override
    protected void onResume(){
        super.onResume();
        update.setEnabled(true);
    }
}
