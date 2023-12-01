package edu.northeastern.gymhub.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.gymhub.R;

public class SettingsActivity extends AppCompatActivity {

    // TODO ADD LOGOUT FUNCTIONALITY

    private ImageButton imageButtonBackArrow;
    private TextView textViewName;
    private TextView textViewUsername;
    private TextView textViewUserEmail;
    private TextView textViewUserGym;
    private LinearLayout emailLayout;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private String curUsername;
    private String curPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Acquire user's username
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        curUsername = preferences.getString("Username", "Default User");

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

        // Set edit email
        emailLayout = findViewById(R.id.linearLayoutEmail);
        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailDialog();
            }
        });

        // Connect to the database
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Retrieve user data from Firebase
        usersRef.child(curUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String gym = dataSnapshot.child("gym").getValue(String.class);
                    curPassword = dataSnapshot.child("password").getValue(String.class);

                    // Update UI with retrieved data
                    textViewName.setText(name);
                    textViewUsername.setText(curUsername);
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

    private void showEmailDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editemail, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Find views in the inflated layout
        final EditText editTextNewEmail = dialogView.findViewById(R.id.editTextNewEmail);
        final EditText editPassword = dialogView.findViewById(R.id.editPassword);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdate);

        // Create and show the dialog
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Set click listeners for Cancel and Update buttons
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the new email and password
                String newEmail = editTextNewEmail.getText().toString().trim();
                String newPassword = editPassword.getText().toString().trim();

                // Check if the provided email is valid
                if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    showToast("Please enter a valid email");
                    return;
                }

                // Check if new email and password are not empty
                if (!newEmail.isEmpty() && !newPassword.isEmpty()) {
                    // Check if the provided password matches the password retrieved from the database
                    if (newPassword.equals(curPassword)) {
                        // Passwords match, update email in the database
                        updateEmail(newEmail);
                        dialog.dismiss();
                    } else {
                        showToast("Incorrect password. Please enter the correct password.");
                    }
                } else {
                    Toast.makeText(SettingsActivity.this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateEmail(String newEmail) {
        // Update email in Firebase
        usersRef.child(curUsername).child("email").setValue(newEmail);

        // Update the local UI
        textViewUserEmail.setText(newEmail);

        Toast.makeText(SettingsActivity.this, "Email updated", Toast.LENGTH_SHORT).show();
    }

    private void handleDatabaseError(DatabaseError databaseError) {
        Toast.makeText(SettingsActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void showToast(String message) {
        Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
