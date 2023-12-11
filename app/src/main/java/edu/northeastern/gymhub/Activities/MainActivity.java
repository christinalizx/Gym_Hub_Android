package edu.northeastern.gymhub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.northeastern.gymhub.R;

/** Activity that shows app splash screen to user **/
public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Create a new XML layout for the splash screen


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // After the timeout, start the main activity
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);

                // Close this activity
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}