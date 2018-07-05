package com.suliteos.sonusourav.poshan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ForgotPassword extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
    }


}
