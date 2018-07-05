package com.suliteos.sonusourav.poshan;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.appus.splash.Splash;

import java.util.Random;

public class LauncherActivity extends AppCompatActivity {

    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_screen);
        imageView=findViewById(R.id.imageView1);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        Splash.Builder splash = new Splash.Builder(this, getSupportActionBar());
        splash.perform();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                final Intent mainIntent = new Intent(LauncherActivity.this, SignInActivity.class);
                LauncherActivity.this.startActivity(mainIntent);
                LauncherActivity.this.finish();
            }
        }, 3000);

    }
}
