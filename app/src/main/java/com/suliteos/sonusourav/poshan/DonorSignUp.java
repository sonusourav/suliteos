package com.suliteos.sonusourav.poshan;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.*;

public class DonorSignUp extends AppCompatActivity {


    private EditText donorName , donorEmail , donorMobile,donorPassword , donorAddress , donorDOD;
    private Spinner donorBloodGrp;
    private CheckBox donorChkBox;
    private Button donorSignUpBtn;
    String sign_up_email;
    String sign_up_name;
    String sign_up_mobile;
    String sign_up_password;
    String sign_up_blood_group;
    String sign_up_address;
    String sign_up_dod;
    private ActionBar donorActionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_donor);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        donorName = findViewById(R.id.user_sign_up_name);
        donorEmail = findViewById(R.id.user_sign_up_ed_email);
        donorMobile = findViewById(R.id.user_sign_up_ed_mobile);
        donorPassword = findViewById(R.id.user_sign_up_ed_password);
        donorBloodGrp = findViewById(R.id.user_sign_up_spinner_blood);
        donorAddress = findViewById(R.id.user_sign_up_ed_address);
        donorDOD = findViewById(R.id.user_sign_up_dod);
        donorSignUpBtn = findViewById(R.id.user_sign_up_btn);
        donorChkBox = findViewById(R.id.user_sign_up_chk_box);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item, getResources().getStringArray(R.array.blood_group)
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down_item);
        donorBloodGrp.setAdapter(spinnerArrayAdapter);


        donorSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sign_up_name = donorName.getText().toString().trim();
                sign_up_email = donorEmail.getText().toString().trim();
                sign_up_mobile = donorMobile.getText().toString().trim();
                sign_up_blood_group = donorBloodGrp.getSelectedItem().toString();
                sign_up_address = donorAddress.getText().toString().trim();
                sign_up_password = donorPassword.getText().toString().trim();
                sign_up_dod = donorPassword.getText().toString().trim();

                if (TextUtils.isEmpty(sign_up_name) || (sign_up_name.equals("")) || (sign_up_name == null) || (sign_up_name.length() == 0)) {
                    Toast.makeText(getApplicationContext(), "Enter your kind name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(sign_up_email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!validateEmail(sign_up_email)) {
                    Toast.makeText(getApplicationContext(), "Invalid Email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(sign_up_mobile)) {
                    Toast.makeText(getApplicationContext(), "Enter your mobile number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sign_up_mobile.length() < 10) {
                    Toast.makeText(getApplicationContext(), "Mobile number should contain ten digits.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(sign_up_password)) {
                    Toast.makeText(getApplicationContext(), "Enter sign_up_password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sign_up_password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int actualPositionOfMySpinner = donorBloodGrp.getSelectedItemPosition();
                String selectedItemOfMySpinner = (String) donorBloodGrp.getItemAtPosition(actualPositionOfMySpinner);

                if (selectedItemOfMySpinner.isEmpty() || selectedItemOfMySpinner.equalsIgnoreCase("Blood Group")) {
                    Toast.makeText(getApplicationContext(), "Select your Blood group", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(sign_up_address) || (sign_up_address.equals("")) || (sign_up_address == null) || (sign_up_address.length() == 0)) {
                    Toast.makeText(getApplicationContext(), "Enter your address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(sign_up_dod)) {
                    Toast.makeText(getApplicationContext(), "Enter your last date of blood donation!", Toast.LENGTH_SHORT).show();
                    return;
                }




                if (!donorChkBox.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please agree to the terms and conditons", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent=new Intent(DonorSignUp.this,MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"Signed Up successfully",Toast.LENGTH_SHORT).show();


            }
            });
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        donorActionBar = getSupportActionBar();
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
