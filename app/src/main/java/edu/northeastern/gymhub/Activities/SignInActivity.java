package edu.northeastern.gymhub.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.northeastern.gymhub.R;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Button login = findViewById(R.id.imageButton2);
        Button register = findViewById(R.id.imageButton5);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if login credentials matched
                startActivity(new Intent(SignInActivity.this, HomepageActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
            }
        });
    }
}