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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
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



public class DonorSignUp extends AppCompatActivity {


    private EditText donorName;
    private EditText donorEmail;
    private EditText donorMobile;
    private EditText donorPassword;
    private EditText donorAddress;
    private Spinner donorBloodGrp;
    private CheckBox donorChkBox;
    String sign_up_email;
    String sign_up_name;
    String sign_up_mobile;
    String sign_up_password;
    String sign_up_blood_group;
    String sign_up_address;
    String sign_up_dod;


    //firebase declaration

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference;
    private DatabaseReference donorReference;
    private DatabaseReference hospitalReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_donor);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }


        userReference= FirebaseDatabase.getInstance().getReference().child("Users");
        donorReference=userReference.child("Donor");
        hospitalReference=userReference.child("Hospital");
        firebaseAuth = FirebaseAuth.getInstance();


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        donorName = findViewById(R.id.user_sign_up_name);
        donorEmail = findViewById(R.id.user_sign_up_ed_email);
        donorMobile = findViewById(R.id.user_sign_up_ed_mobile);
        donorPassword = findViewById(R.id.user_sign_up_ed_password);
        donorBloodGrp = findViewById(R.id.user_sign_up_spinner_blood);
        donorAddress = findViewById(R.id.user_sign_up_ed_address);
        EditText donorDOD = findViewById(R.id.user_sign_up_dod);
        Button donorSignUpBtn = findViewById(R.id.user_sign_up_btn);
        donorChkBox = findViewById(R.id.user_sign_up_chk_box);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, getResources().getStringArray(R.array.blood_group)
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down_item);
        donorBloodGrp.setAdapter(spinnerArrayAdapter);


        donorSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validateInputs())
                {

                    firebaseAuth.createUserWithEmailAndPassword(sign_up_email, sign_up_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                sendEmailVerification();
                            }

                            else{
                                Toast.makeText(DonorSignUp.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
        });

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
                        Toast.makeText(DonorSignUp.this, "Successfully Registered, Verification mail sent!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(DonorSignUp.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                    startActivity(new Intent(DonorSignUp.this, SignInActivity.class));

                }
            });
        }
    }


    //send Userdata to database
    private void sendUserData(){


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootReference=firebaseDatabase.getReference();
        DatabaseReference userReference=rootReference.child("Users").child("Donor");
        DatabaseReference myRef = userReference.child(encodeUserEmail(sign_up_email));

        DonorClass userprofile =new DonorClass(sign_up_name,sign_up_email,sign_up_mobile,sign_up_password,sign_up_blood_group,sign_up_address,sign_up_dod);
        myRef.setValue(userprofile);
        Toast.makeText(getApplicationContext(),"sending userdata",Toast.LENGTH_SHORT).show();


    }



    static String encodeUserEmail(String userEmail) {
        return userEmail.replace(".", ",");
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

    //Validation of input credentials
    public boolean validateInputs(){
        sign_up_name = donorName.getText().toString().trim();
        sign_up_email = donorEmail.getText().toString().trim();
        sign_up_mobile = donorMobile.getText().toString().trim();
        sign_up_blood_group = donorBloodGrp.getSelectedItem().toString();
        sign_up_address = donorAddress.getText().toString().trim();
        sign_up_password = donorPassword.getText().toString().trim();
        sign_up_dod = donorPassword.getText().toString().trim();

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

        int actualPositionOfMySpinner = donorBloodGrp.getSelectedItemPosition();
        String selectedItemOfMySpinner = (String) donorBloodGrp.getItemAtPosition(actualPositionOfMySpinner);

        if (selectedItemOfMySpinner.isEmpty() || selectedItemOfMySpinner.equalsIgnoreCase("Blood Group")) {
            Toast.makeText(getApplicationContext(), "Select your Blood group", Toast.LENGTH_SHORT).show();
            return false;

        }
        if (TextUtils.isEmpty(sign_up_address) || (sign_up_address.equals("")) || (sign_up_address == null) || (sign_up_address.length() == 0)) {
            Toast.makeText(getApplicationContext(), "Enter your address!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(sign_up_dod)) {
            Toast.makeText(getApplicationContext(), "Enter your last date of blood donation!", Toast.LENGTH_SHORT).show();
            return false;
        }




        if (!donorChkBox.isChecked()) {
            Toast.makeText(getApplicationContext(), "Please agree to the terms and conditons", Toast.LENGTH_SHORT).show();
            return false;

        }

        return true;
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
