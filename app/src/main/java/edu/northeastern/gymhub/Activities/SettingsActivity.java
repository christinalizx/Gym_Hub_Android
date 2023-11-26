package edu.northeastern.gymhub.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.gymhub.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton imageButtonBackArrow;
    private TextView textViewName;
    private TextView textViewUsername;
    private TextView textViewUserEmail;
    private TextView textViewUserGym;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Acquire user's username
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        username = preferences.getString("Username", "Default User");

        // Get objects
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

        // Retrieve user data from Firebase
        usersRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String gym = dataSnapshot.child("gym").getValue(String.class);

                    // Update UI with retrieved data
                    textViewName.setText(name);
                    textViewUsername.setText(username);
                    textViewUserEmail.setText(email);
                    textViewUserGym.setText(gym);
                } else {
                    // Handle the case where the username does not exist in the database
                    // You can show an error message or take appropriate action
                    Toast.makeText(SettingsActivity.this, "User not found in the database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                handleDatabaseError(databaseError);
            }
        });
    }

    private void handleDatabaseError(DatabaseError databaseError) {
        Toast.makeText(SettingsActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
