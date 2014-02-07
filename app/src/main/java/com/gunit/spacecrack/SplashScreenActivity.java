package com.gunit.spacecrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.Session;

public class SplashScreenActivity extends ActionBarActivity {

    private SpaceCrackApplication application;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        application = (SpaceCrackApplication) getApplication();

        //Check is an user is already logged in

        if (isLoggedIn()) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        //Close this activity so it won't show up again
        finish();
    }

    private boolean isLoggedIn() {
        Session session = Session.getActiveSession();
        SharedPreferences sharedPreferences = getSharedPreferences("Login", 0);
        return ((session != null && session.isOpened()) || sharedPreferences.getString("accessToken", null) != null);
    }

}
