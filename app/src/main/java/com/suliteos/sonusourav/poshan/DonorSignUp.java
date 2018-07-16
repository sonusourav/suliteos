package com.suliteos.sonusourav.poshan;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class DonorSignUp extends AppCompatActivity {


    private EditText donorName;
    private EditText donorEmail;
    private EditText donorMobile;
    private EditText donorPassword;
    private EditText donorAddress;
    private EditText donorDOD;

    private Spinner donorBloodGrp;
    private CheckBox donorChkBox;
    String sign_up_email;
    String sign_up_name;
    String sign_up_mobile;
    String sign_up_password;
    String sign_up_blood_group;
    String sign_up_address;
    String sign_up_dod;
    private ProgressBar donorProgressBar;


    //firebase declaration

    private FirebaseAuth firebaseAuth;
    private StorageReference donorReference;
    private String imagePath;
//image upload
    private Uri mImageUri;



    private static Bitmap Image = null;
    private CircleImageView userProfilePic;
    private static final int GALLERY = 1;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_donor);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }


        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference();
        donorReference= storageReference.child("Users").child("Donor");

        firebaseAuth = FirebaseAuth.getInstance();


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        donorName = findViewById(R.id.user_sign_up_name);
        donorEmail = findViewById(R.id.user_sign_up_ed_email);
        donorMobile = findViewById(R.id.user_sign_up_ed_mobile);
        donorPassword = findViewById(R.id.user_sign_up_ed_password);
        donorBloodGrp = findViewById(R.id.user_sign_up_spinner_blood);
        donorAddress = findViewById(R.id.user_sign_up_ed_address);
        donorDOD = findViewById(R.id.user_sign_up_dod);
        Button donorSignUpBtn = findViewById(R.id.user_sign_up_btn);
        donorChkBox = findViewById(R.id.user_sign_up_chk_box);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, getResources().getStringArray(R.array.blood_group)
        );
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down_item);
        donorBloodGrp.setAdapter(spinnerArrayAdapter);
        userProfilePic =findViewById(R.id.login_profile_pic);
        donorProgressBar=findViewById(R.id.donor_sign_up_progress_bar);

        donorSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                donorProgressBar.setVisibility(View.VISIBLE);
                if(validateInputs())
                {

                    firebaseAuth.createUserWithEmailAndPassword(sign_up_email, sign_up_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                sendEmailVerification();
                            }

                            else{
                                donorProgressBar.setVisibility(View.GONE);
                                Toast.makeText(DonorSignUp.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
        });


        userProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfilePic.setImageBitmap(null);
                if (Image != null)
                    Image.recycle();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY);
            }
        });



    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY && resultCode != 0) {
            mImageUri = data.getData();
            try {
                Image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                userProfilePic.setImageBitmap(Image);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }





    //send email Verification
    private  void  sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(DonorSignUp.this, "Successfully Registered, Verification mail sent!", Toast.LENGTH_SHORT).show();
                        sendUserData();
                    }else {
                        Toast.makeText(DonorSignUp.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                    startActivity(new Intent(DonorSignUp.this, SignInActivity.class));
                    donorProgressBar.setVisibility(View.GONE);

                }
            });
        }
    }


    private void uploadImage() {

        if( mImageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = donorReference.child(encodeUserEmail(sign_up_email)).child("images");
            UploadTask uploadTask = ref.putFile(mImageUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            imagePath = Objects.requireNonNull(taskSnapshot.getMetadata()).getPath();
                            Log.d("donorImagePath",imagePath);

                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference rootReference=firebaseDatabase.getReference();
                            DatabaseReference userReference=rootReference.child("Users").child("Donor");
                            DatabaseReference myRef = userReference.child(encodeUserEmail(sign_up_email));

                            myRef.child("donorImagePath").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSnapshot.getRef().setValue(imagePath);
                                    Toast.makeText(getApplicationContext(),"image Path done",Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(),"image Path cancelled",Toast.LENGTH_SHORT).show();

                                }
                            });


                            progressDialog.dismiss();
                            Toast.makeText(DonorSignUp.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(DonorSignUp.this, "Image upload Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("Upload failure",e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploading "+(int)progress+"%");
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

        DonorClass userProfile =new DonorClass(sign_up_name,sign_up_email,sign_up_mobile,sign_up_password,sign_up_blood_group,sign_up_address,sign_up_dod);
        myRef.setValue(userProfile);
        Toast.makeText(getApplicationContext(),"sending userdata",Toast.LENGTH_SHORT).show();
        uploadImage();

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
        sign_up_dod = donorDOD.getText().toString().trim();

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
