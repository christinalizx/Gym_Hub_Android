package edu.northeastern.gymhub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class ActualMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton login = findViewById(R.id.imageButton2);
        ImageButton register = findViewById(R.id.imageButton5);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if login credentials matched
                startActivity(new Intent(ActualMain.this, homePage.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActualMain.this, register.class));
            }
        });
    }
}