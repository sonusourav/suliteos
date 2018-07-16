package com.suliteos.sonusourav.poshan;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.Objects;


public class WelcomeActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    public static SharedPreferences.Editor editor;
    private ImageView navImage;
    private String imagepath;
    private String loginType;
    public  String POSHAN_LOGIN_TYPE="PoshanLoginType";
    public  String POSHAN_PREFS_NAME = "mypref";
    public  String POSHAN_PREF_USERNAME = "username";
    public  String POSHAN_PREF_PASSWORD = "password";
    public  String Poshan_isLoggedIn = "PoshanIsLoggedIn";
    SharedPreferences pref;



    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        final String email= firebaseUser.getEmail();

         pref = getSharedPreferences(POSHAN_PREFS_NAME, 0);
        editor = pref.edit();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerContainer = navigationView.getHeaderView(0);
        TextView navUserEmail = headerContainer.findViewById(R.id.nav_header_email);
        navUserEmail.setText(email);
        navImage=headerContainer.findViewById(R.id.nav_header_imageView);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);


        toggle.syncState();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
         loginType = (pref.getString(POSHAN_LOGIN_TYPE, null));
        StorageReference storageReference;
        assert loginType != null;
        if(loginType.trim().equals("donor")){
            assert email != null;
            storageReference = firebaseStorage.getReferenceFromUrl(getString(R.string.bucketURL)).child("Users/Donor/"+encodeUserEmail(email)+"/images");
        }else{
            assert email != null;
            storageReference = firebaseStorage.getReferenceFromUrl(getString(R.string.bucketURL)).child("Users/Hospital/"+encodeUserEmail(email)+"/images");

        }


        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("hospital_URL",uri.toString());
                imagepath=uri.toString();
                Glide.with(getApplicationContext())
                        .load(imagepath)
                         .into(navImage);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("imageURL","failure");
            }
        });




        navUserEmail.setText(email);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        Toast.makeText(WelcomeActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;
                        case R.id.nav_profile:
                            if(loginType != null) {
                                if(loginType.trim().equals("donor")){
                                    Intent intent=new Intent(WelcomeActivity.this,UpdateProfileDonor.class);
                                    startActivity(intent);
                                    Log.d(POSHAN_LOGIN_TYPE,"donor");
                                }else{
                                    Intent intent=new Intent(WelcomeActivity.this,UpdateProfileHospital.class);
                                    startActivity(intent);
                                    Log.d(POSHAN_LOGIN_TYPE,"hospital");

                                }
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
                                builder.setTitle("Profile Type ").setMessage("You profile belongs to which type ")
                                        .setCancelable(true)
                                        .setPositiveButton("Donor", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent=new Intent(WelcomeActivity.this,UpdateProfileDonor.class);
                                                startActivity(intent);
                                                dialog.cancel();

                                            }
                                        })
                                        .setNegativeButton("Hospital", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent=new Intent(WelcomeActivity.this,UpdateProfileHospital.class);
                                                startActivity(intent);
                                                dialog.cancel();

                                            }
                                        });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }




                        break;
                    case R.id.nav_Logout:
                        firebaseAuth.signOut();
                        editor.putString(Poshan_isLoggedIn, "false");
                        editor.remove(POSHAN_PREF_USERNAME);
                        editor.remove(POSHAN_PREF_PASSWORD);
                        editor.apply();
                        Toast.makeText(getApplicationContext(),"Signed out successfully",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(WelcomeActivity.this,SignInActivity.class));
                        finish();
                        break;

                    case R.id.nav_password:
                        startActivity(new Intent(WelcomeActivity.this,UpdatePassword.class));
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

    }


    static String encodeUserEmail(String userEmail) { return userEmail.replace(".", ",");
    }

}
