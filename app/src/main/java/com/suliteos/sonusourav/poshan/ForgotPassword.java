package com.suliteos.sonusourav.poshan;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class ForgotPassword extends AppCompatActivity {

    private ActionBar forgotActionBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        forgotActionBar = getSupportActionBar();
        assert forgotActionBar != null;
        forgotActionBar.setHomeButtonEnabled(true);
        forgotActionBar.setDisplayHomeAsUpEnabled(true);
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

}
