package edu.northeastern.gymhub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 4000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen); // Create a new XML layout for the splash screen

        // Use a Handler to delay the opening of the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // After the timeout, start the main activity
                Intent intent = new Intent(MainActivity.this, ActualMain.class);
                startActivity(intent);

                // Close this activity
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
