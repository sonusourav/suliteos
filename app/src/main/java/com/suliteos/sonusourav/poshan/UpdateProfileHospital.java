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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class UpdateProfileHospital extends AppCompatActivity {
    android.support.v7.app.ActionBar UpdateHosActionBar;
    private Button editButton;
    private MenuItem save;
    private FirebaseUser user;
    private EditText profileHosName, profileHosEmail, profileHosPhone, profileHosAddress, profileHosIncharge;
    private EditText profileHosBlood;
    private FirebaseAuth profileauth;
    private FirebaseDatabase profilefirebaseInstance;
    private FirebaseStorage profilefirestoreInstance;
    private DatabaseReference profilerootRef;
    private DatabaseReference profileuserIdRef;
    private DatabaseReference profileEmailRef;
    private String userEmail;
    private ProgressBar profileHosProgressbar;


    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_hospital);
        profileHosName = findViewById(R.id.update_name_hospital);
        profileHosEmail = findViewById(R.id.update_email_hospital);
        profileHosPhone = findViewById(R.id.update_mobile_hospital);
        profileHosBlood = findViewById(R.id.update_blood_hospital);
        profileHosAddress = findViewById(R.id.update_address_hospital);
        profileHosIncharge = findViewById(R.id.update_incharge_hospital);
        editButton = findViewById(R.id.edit_button_hospital);
        profileHosProgressbar =findViewById(R.id.update_progress_bar_hospital);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        profileHosProgressbar.setVisibility(View.VISIBLE);
        profileauth = FirebaseAuth.getInstance();
        profilefirestoreInstance = FirebaseStorage.getInstance();
        profilefirebaseInstance = FirebaseDatabase.getInstance();
        profilerootRef = profilefirebaseInstance.getReference("Users");
        profileuserIdRef = profilerootRef.child("Hospital");
        user = FirebaseAuth.getInstance().getCurrentUser();
        String testEmail = encodeUserEmail(user.getEmail());
        profileEmailRef = profileuserIdRef.child(testEmail).getRef();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileHosName.setEnabled(true);
                profileHosEmail.setEnabled(true);
                profileHosEmail.setFocusable(false);
                profileHosBlood.setEnabled(true);
                profileHosAddress.setEnabled(true);
                profileHosIncharge.setEnabled(true);
                save.setEnabled(true);
            }
        });







        profileEmailRef.child("hosName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nvalue = dataSnapshot.getValue(String.class);
                profileHosName.setText(nvalue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        profileEmailRef.child("hosEmail").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dvalue = dataSnapshot.getValue(String.class);
                profileHosEmail.setText(dvalue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        profileEmailRef.child("hosBlood").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String gvalue = dataSnapshot.getValue(String.class);
                profileHosBlood.setText(gvalue);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        profileEmailRef.child("hosMobile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String evalue = dataSnapshot.getValue(String.class);
                profileHosPhone.setText(evalue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        profileEmailRef.child("donorAddress").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String pvalue = dataSnapshot.getValue(String.class);
                profileHosAddress.setText(pvalue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        profileEmailRef.child("hosIncharge").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String mvalue = dataSnapshot.getValue(String.class);
                profileHosIncharge.setText(mvalue);
                profileHosProgressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                profileHosProgressbar.setVisibility(View.GONE);

            }
        });


    }



    public boolean onCreateOptionsMenu(Menu menu) {

        assert UpdateHosActionBar != null;
        UpdateHosActionBar = getSupportActionBar();
        UpdateHosActionBar.setHomeButtonEnabled(true);
        UpdateHosActionBar.setDisplayHomeAsUpEnabled(true);
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

        final String pname = profileHosName.getText().toString().trim();
        final String pemail = profileHosEmail.getText().toString().trim();
        final String pmobile = profileHosPhone.getText().toString().trim();
        final String pblood = profileHosBlood.getText().toString().trim();
        final String paddress = profileHosAddress.getText().toString().trim();
        final String pincharge = profileHosIncharge.getText().toString().trim();

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
                    dataSnapshot.getRef().child("donorDOD").setValue(pincharge);

                    profileHosName.setEnabled(false);
                    profileHosEmail.setEnabled(false);
                    profileHosPhone.setEnabled(false);
                    profileHosBlood.setEnabled(false);
                    profileHosAddress.setEnabled(false);
                    profileHosIncharge.setEnabled(false);

                    save.setEnabled(false);
                    profileHosProgressbar.setVisibility(View.GONE);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("User", databaseError.getMessage());
                    profileHosProgressbar.setVisibility(View.GONE);

                }

            });


            Intent intent = new Intent(UpdateProfileHospital.this, MainActivity.class);
            startActivity(intent);

        }


    }

}
