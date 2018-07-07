package com.suliteos.sonusourav.poshan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HospitalSignUp extends AppCompatActivity {


    private EditText hosName, hosEmail, hosMobile, hosPassword, hosAddress, hosBlood , hosIncharge;
    private CheckBox hosChkBox;
    String sign_up_email;
    String sign_up_name;
    String sign_up_mobile;
    String sign_up_password;
    String sign_up_blood;
    String sign_up_address;
    String sign_up_incharge;

    private ProgressBar hosProgressBar;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference;
    private DatabaseReference donorReference;
    private DatabaseReference hospitalReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_hospital);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        userReference= FirebaseDatabase.getInstance().getReference().child("Users");
        donorReference=userReference.child("Donor");
        hospitalReference=userReference.child("Hospital");
        firebaseAuth = FirebaseAuth.getInstance();



        hosName = findViewById(R.id.hos_sign_up_ed_name);
        hosEmail = findViewById(R.id.hos_sign_up_ed_email);
        hosMobile = findViewById(R.id.hos_sign_up_ed_mobile);
        hosPassword = findViewById(R.id.hos_sign_up_ed_password);
        hosAddress = findViewById(R.id.hos_sign_up_ed_address);
        hosBlood = findViewById(R.id.hos_sign_up_ed_blood);
        hosIncharge=findViewById(R.id.hos_sign_up_ed_incharge);
        Button hosSignUpBtn = findViewById(R.id.hos_sign_up_btn);
        hosChkBox = findViewById(R.id.hos_sign_up_chk_box);
        hosProgressBar=findViewById(R.id.sign_up_progress_bar);


        hosSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hosProgressBar.setVisibility(View.VISIBLE);

                if(validateInputs())
                {

                    firebaseAuth.createUserWithEmailAndPassword(sign_up_email, sign_up_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                sendEmailVerification();
                            }

                            else{
                                hosProgressBar.setVisibility(View.GONE);
                                Toast.makeText(HospitalSignUp.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }


            }
        });
    }


    public boolean validateInputs(){

        sign_up_name = hosName.getText().toString().trim();
        sign_up_email = hosEmail.getText().toString().trim();
        sign_up_mobile = hosMobile.getText().toString().trim();
        sign_up_address = hosAddress.getText().toString().trim();
        sign_up_password = hosPassword.getText().toString().trim();
        sign_up_blood=hosBlood.getText().toString().trim();
        sign_up_incharge = hosIncharge.getText().toString().trim();


        if (TextUtils.isEmpty(sign_up_name) || (sign_up_name.equals("")) || (sign_up_name == null) || (sign_up_name.length() == 0)) {
            Toast.makeText(getApplicationContext(), "Enter your kind name!", Toast.LENGTH_SHORT).show();
            return false;

        }

        if (TextUtils.isEmpty(sign_up_email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!validateEmail(sign_up_email)) {
            Toast.makeText(getApplicationContext(), "Invalid Email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(sign_up_mobile)) {
            Toast.makeText(getApplicationContext(), "Enter your mobile number!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sign_up_mobile.length() < 10) {
            Toast.makeText(getApplicationContext(), "Mobile number should contain ten digits.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(sign_up_password)) {
            Toast.makeText(getApplicationContext(), "Enter sign_up_password!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (sign_up_password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (TextUtils.isEmpty(sign_up_blood) || (sign_up_blood.equals("")) || (sign_up_blood == null) || (sign_up_blood.length() == 0)) {
            Toast.makeText(getApplicationContext(), "Enter amount of monthly blood requirement in litres !", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(sign_up_address) || (sign_up_address.equals("")) || (sign_up_address == null) || (sign_up_address.length() == 0)) {
            Toast.makeText(getApplicationContext(), "Enter your address!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(sign_up_incharge)) {
            Toast.makeText(getApplicationContext(), "Enter the name of Incharge !", Toast.LENGTH_SHORT).show();
            return false;
        }




        if (!hosChkBox.isChecked()) {
            Toast.makeText(getApplicationContext(), "Please agree to the terms and conditons", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }


    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }


    //send Userdata to database
    private void sendUserData(){


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootReference=firebaseDatabase.getReference();
        DatabaseReference userReference=rootReference.child("Users").child("Hospital");
        DatabaseReference myRef = userReference.child(encodeUserEmail(sign_up_email));

        HospitalClass userprofile =new HospitalClass(sign_up_name,sign_up_email,sign_up_mobile,sign_up_password,sign_up_blood,sign_up_address,sign_up_incharge);
        myRef.setValue(userprofile);
        Toast.makeText(getApplicationContext(),"sending userdata",Toast.LENGTH_SHORT).show();


    }

    //send email Verification
    private  void  sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        sendUserData();
                        firebaseAuth.signOut();

                        Toast.makeText(HospitalSignUp.this, "Successfully Registered, Verification mail sent!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(HospitalSignUp.this, "Verification mail hasn't been sent!", Toast.LENGTH_SHORT).show();
                    }

                    startActivity(new Intent(HospitalSignUp.this, SignInActivity.class));
                    hosProgressBar.setVisibility(View.GONE);

                }
            });
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        ActionBar donorActionBar = getSupportActionBar();
        assert donorActionBar != null;
        donorActionBar.setHomeButtonEnabled(true);
        donorActionBar.setDisplayHomeAsUpEnabled(true);
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

    //validateEmailMethod
    public boolean validateEmail(String email) {

        Pattern pattern;
        Matcher matcher;
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();

    }




    @Override
    protected void onResume() {
        super.onResume();
    }



}


