package com.suliteos.sonusourav.poshan;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import static com.suliteos.sonusourav.poshan.SignInActivity.POSHAN_LOGIN_TYPE;
import static com.suliteos.sonusourav.poshan.SignInActivity.POSHAN_PREFS_NAME;

public class MainActivity extends AppCompatActivity {
    private Spinner inputBloodType;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private android.support.v7.widget.Toolbar toolbar;
    private NavigationView navigationView;
    private TextView navUserEmail;
    private Intent intent;
    private  String userEmail;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    public static SharedPreferences.Editor editor;
    private SharedPreferences pref;



    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        final String email=firebaseUser.getEmail();

        pref = getSharedPreferences(POSHAN_PREFS_NAME, 0);
        editor = pref.edit();
        navigationView=findViewById(R.id.nav_view);
        View headerContainer = navigationView.getHeaderView(0);
        navUserEmail=headerContainer.findViewById(R.id.nav_header_email);
        navUserEmail.setText(email);


        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);


        toggle.syncState();


        navUserEmail.setText(email);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;
                        case R.id.nav_profile:
                            String login_type = (pref.getString(POSHAN_LOGIN_TYPE, null));
                            if(login_type != null) {
                                if(login_type.trim().equals("donor")){
                                    Intent intent=new Intent(MainActivity.this,UpdateProfileDonor.class);
                                    startActivity(intent);
                                    Log.d(POSHAN_LOGIN_TYPE,"donor");
                                }else{
                                    Intent intent=new Intent(MainActivity.this,UpdateProfileHospital.class);
                                    startActivity(intent);
                                    Log.d(POSHAN_LOGIN_TYPE,"hospital");

                                }
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Profile Type ").setMessage("You profile belongs to which type ")
                                        .setCancelable(true)
                                        .setPositiveButton("Donor", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent=new Intent(MainActivity.this,UpdateProfileDonor.class);
                                                startActivity(intent);
                                                dialog.cancel();

                                            }
                                        })
                                        .setNegativeButton("Hospital", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent intent=new Intent(MainActivity.this,UpdateProfileHospital.class);
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
                        editor.putString(SignInActivity.Poshan_isLoggedIn, "false");
                        editor.remove(SignInActivity.POSHAN_PREF_USERNAME);
                        editor.remove(SignInActivity.POSHAN_PREF_PASSWORD);
                        editor.apply();
                        Toast.makeText(getApplicationContext(),"Signed out successfully",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,SignInActivity.class));
                        finish();
                        break;

                    case R.id.nav_password:
                        startActivity(new Intent(MainActivity.this,UpdatePassword.class));
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

    }
}
