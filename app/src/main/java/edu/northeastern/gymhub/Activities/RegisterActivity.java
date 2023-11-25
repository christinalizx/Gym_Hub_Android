package edu.northeastern.gymhub.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import edu.northeastern.gymhub.Models.GymUser;
import edu.northeastern.gymhub.R;
import edu.northeastern.gymhub.Utils.Callback;

public class RegisterActivity extends AppCompatActivity implements Callback {

    private EditText firstName; // Changed from TextView to EditText
    private EditText lastName; // Changed from TextView to EditText
    private EditText username;
    private EditText password;
    private EditText email;
    private Spinner gym;
    private Button registerButton;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Connect to the database
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Get input fields
        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        username = findViewById(R.id.editTextUsername);
        password = findViewById(R.id.editTextPassword);
        email = findViewById(R.id.editTextEmail);
        gym = findViewById(R.id.spinnerGym);
        registerButton = findViewById(R.id.buttonRegister);

        String[] options = new String[]{"Marino","IronFit", "PowerGym", "FitsZone", "FlexWell"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(), R.layout.simple_dropdown_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        gym.setAdapter(adapter);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputs();
            }
        });

    }

    // Callback method
    @Override
    public void onContinue() {
        registerUser();
    }

    // Callback method
    @Override
    public void onError() {
        // Handle error if needed
    }

    private void registerUser() {
        GymUser newUser = new GymUser(
                firstName.getText().toString() + " " + lastName.getText().toString(),
                username.getText().toString(),
                password.getText().toString(),
                email.getText().toString(),
                gym.getSelectedItem().toString()
        );

        // Write to Firebase
        usersRef.child(newUser.getUsername()).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("You are now a GymHub member.");
                        startActivity(new Intent(RegisterActivity.this, HomepageActivity.class));
                    } else {
                        showToast("There has been an error with your registration.");
                    }
                });
    }

    private void checkInputs() {
        if (username.getText().toString().isEmpty() ||
                password.getText().toString().isEmpty() ||
                email.getText().toString().isEmpty() ||
                gym.getSelectedItem().toString().isEmpty()) {
            showToast("Please fill out all fields.");
            return;
        }

        String inputUsername = username.getText().toString();
        usersRef.child(inputUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    showToast("Username already exists. Please choose a different username.");
                } else {
                    onContinue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showToast("An error occurred. Please try again.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
