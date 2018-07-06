package com.suliteos.sonusourav.poshan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

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


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();


        navigationView=findViewById(R.id.nav_view);
        View headerContainer = navigationView.getHeaderView(0); // This returns the container layout in nav_drawer_header.xml (e.g., your RelativeLayout or LinearLayout)
        navUserEmail=headerContainer.findViewById(R.id.nav_header_email);


        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);


        toggle.syncState();

        intent=getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null){
            userEmail = bundle.getString("String");
        }else
            userEmail="UserEmail";

        navUserEmail.setText(userEmail);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.nav_home:
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        break;
                        case R.id.nav_profile:

                            startActivity(new Intent(MainActivity.this,UpdateProfile.class));
                        break;
                    case R.id.nav_Logout:
                        firebaseAuth.signOut();
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
