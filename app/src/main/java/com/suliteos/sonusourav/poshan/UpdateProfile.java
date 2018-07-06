package com.suliteos.sonusourav.poshan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;

/**
 * Created by SONU SOURAV on 3/17/2018.
 */

public class UpdateProfile extends AppCompatActivity {

    android.support.v7.app.ActionBar settingsActionbar;
    Button editButton;
    MenuItem save;
    Calendar profileCalender;
    FirebaseUser user;
    private EditText profileName, profileEmail, profilePhone, profileAddress, profileDOD;
    private Spinner profileBlood;
    private FirebaseAuth profileauth;
    private FirebaseDatabase profilefirebaseInstance;
    private FirebaseStorage profilefirestoreInstance;
    private DatabaseReference profilerootRef;
    private DatabaseReference profileuserIdRef;
    private DatabaseReference profileEmailRef;
    private String userEmail;
    private ImageView verifyImage;
    ProgressBar profileProgressbar;


    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);
        profileName = findViewById(R.id.update_name);
        profileEmail = findViewById(R.id.update_email);
        profilePhone = findViewById(R.id.update_mobile);
        profileBlood = findViewById(R.id.update_blood);
        profileAddress = findViewById(R.id.update_address);
        profileDOD = findViewById(R.id.update_DOD);

        editButton = findViewById(R.id.edit_button);
        profileProgressbar=findViewById(R.id.update_progress_bar);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        profileProgressbar.setVisibility(View.VISIBLE);
        profileBlood.setEnabled(false);
        profileauth = FirebaseAuth.getInstance();
        profilefirestoreInstance = FirebaseStorage.getInstance();
        profilefirebaseInstance = FirebaseDatabase.getInstance();
        profilerootRef = profilefirebaseInstance.getReference("Users");
        profileuserIdRef = profilerootRef.child("Donor");
        user = FirebaseAuth.getInstance().getCurrentUser();
        String testEmail = encodeUserEmail(user.getEmail());
        profileEmailRef = profileuserIdRef.child(testEmail).getRef();


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileName.setEnabled(true);
                profileName.setFocusable(true);
                profileEmail.setEnabled(true);
                profileEmail.setFocusable(true);
                profileAddress.setFocusable(true);
                profileDOD.setFocusable(true);
                profilePhone.setFocusable(true);
                profileBlood.setFocusable(true);
                profilePhone.setFocusable(true);
                profileBlood.setEnabled(true);
                profileAddress.setEnabled(true);
                profileDOD.setEnabled(true);
                save.setEnabled(true);
            }
        });







        profileEmailRef.child("donorName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nvalue = dataSnapshot.getValue(String.class);
                profileName.setText(nvalue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        profileEmailRef.child("donorEmail").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dvalue = dataSnapshot.getValue(String.class);
                profileEmail.setText(dvalue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        profileEmailRef.child("donorBloodgroup").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gvalue = dataSnapshot.getValue(String.class);

                assert gvalue != null;
                if (gvalue.equals("O+"))
                    profileBlood.setSelection(1);
                else if(gvalue.equals("A+"))
                    profileBlood.setSelection(2);
                else if(gvalue.equals("B+"))
                    profileBlood.setSelection(3);
                else if(gvalue.equals("AB+"))
                    profileBlood.setSelection(4);
                else if(gvalue.equals("O-"))
                    profileBlood.setSelection(5);
                else if(gvalue.equals("A-"))
                    profileBlood.setSelection(6);
                else if(gvalue.equals("B-"))
                    profileBlood.setSelection(7);
                else if(gvalue.equals("AB-"))
                    profileBlood.setSelection(8);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        profileEmailRef.child("donorMobile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String evalue = dataSnapshot.getValue(String.class);
                profilePhone.setText(evalue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        profileEmailRef.child("donorAddress").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String pvalue = dataSnapshot.getValue(String.class);
                profileAddress.setText(pvalue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        profileEmailRef.child("donorDOD").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mvalue = dataSnapshot.getValue(String.class);
                profileDOD.setText(mvalue);
                profileProgressbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                profileProgressbar.setVisibility(View.INVISIBLE);

            }
        });


    }



    public boolean onCreateOptionsMenu(Menu menu) {
       /* MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_button_profile, menu);*/
        assert settingsActionbar != null;
        settingsActionbar = getSupportActionBar();
        settingsActionbar.setHomeButtonEnabled(true);
        settingsActionbar.setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {

            case android.R.id.home:
                this.finish();
                return true;
            case R.id.profile_save:
                Update(item);
                Toast.makeText(getBaseContext(), "Successfully saved", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_button_profile, menu);

        save = menu.getItem(0);
        menu.getItem(0).setEnabled(false); // here pass the index of save menu item
        return super.onPrepareOptionsMenu(menu);

    }

    public void Update(MenuItem item) {

        final String pname = profileName.getText().toString().trim();
        final String pemail = profileEmail.getText().toString().trim();
        final String pmobile = profilePhone.getText().toString().trim();
        final String pblood = profileBlood.getSelectedItem().toString();
        final String paddress = profileAddress.getText().toString().trim();
        final String pdod = profileDOD.getText().toString().trim();

        if (profileauth.getCurrentUser() != null) {

            userEmail = user.getEmail();


            profileEmailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    dataSnapshot.getRef().child("donorName").setValue(pname);
                    dataSnapshot.getRef().child("donorEmail").setValue(pemail);
                    dataSnapshot.getRef().child("donorMobile").setValue(pmobile);
                    dataSnapshot.getRef().child("donorBlood").setValue(pblood);
                    dataSnapshot.getRef().child("donorAddress").setValue(paddress);
                    dataSnapshot.getRef().child("donorDOD").setValue(pdod);

                    profileName.setEnabled(false);
                    profileEmail.setEnabled(false);
                    profilePhone.setEnabled(false);
                    profileBlood.setEnabled(false);
                    profileAddress.setEnabled(false);
                    profileDOD.setEnabled(false);

                    save.setEnabled(false);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("User", databaseError.getMessage());
                }

            });


            Intent intent = new Intent(UpdateProfile.this, MainActivity.class);
            startActivity(intent);

        }


    }

}
