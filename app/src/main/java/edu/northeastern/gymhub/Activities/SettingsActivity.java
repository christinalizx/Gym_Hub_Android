package edu.northeastern.gymhub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.northeastern.gymhub.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton imageButtonBackArrow;
    private TextView textViewName;
    private TextView textViewUsername;
    private TextView textViewUserEmail;
    private TextView textViewUserGym;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        imageButtonBackArrow = findViewById(R.id.imageButtonBackArrow);
        textViewName = findViewById(R.id.textViewName);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewUserEmail = findViewById(R.id.textViewUserEmail);
        textViewUserGym = findViewById(R.id.textViewUserGym);

        // Set back arrow
        imageButtonBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        // Connect to the database
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Add user data to view



    }
}
