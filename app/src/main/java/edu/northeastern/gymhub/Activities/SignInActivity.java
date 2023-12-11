package edu.northeastern.gymhub.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import edu.northeastern.gymhub.R;

public class SignInActivity extends AppCompatActivity {

    private DatabaseReference usersDatabase;
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Disable ability to go back to any gymhub activity without logging in
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });

        usersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        usernameEditText = findViewById(R.id.editTextText);
        passwordEditText = findViewById(R.id.editTextTextPassword);

        Button login = findViewById(R.id.imageButton2);
        Button register = findViewById(R.id.imageButton5);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, RegisterActivity.class));
            }
        });
    }

    private void signIn() {
        final String username = usernameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(SignInActivity.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        usersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> usersIterator = dataSnapshot.getChildren().iterator();
                boolean matchFound = false;

                while (usersIterator.hasNext()) {
                    DataSnapshot userSnapshot = usersIterator.next();
                    String storedUsername = (String) userSnapshot.child("username").getValue();
                    String storedPassword = (String) userSnapshot.child("password").getValue();
                    String gymName = (String) userSnapshot.child("gym").getValue();

                    if (username.equals(storedUsername) && password.equals(storedPassword)) {
                        // Match found, proceed to HomepageActivity with gymName as an extra
                        matchFound = true;

                        // Save Gym Name to SharedPreferences
                        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("GymName", gymName);
                        editor.putString("Username", username);
                        editor.apply();

                        Intent intent = new Intent(SignInActivity.this, HomepageActivity.class);
                        startActivity(intent);
                        break;
                    }
                }

                if (!matchFound) {
                    // No match found
                    Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(SignInActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
