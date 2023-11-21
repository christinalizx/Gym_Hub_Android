package edu.northeastern.gymhub.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.northeastern.gymhub.Models.GymUser;
import edu.northeastern.gymhub.R;
import edu.northeastern.gymhub.Utils.Callback;

public class RegisterActivity extends AppCompatActivity implements Callback {

    private TextView username;
    private TextView password;
    private TextView address;
    private TextView gymId;
    private Button registerButton;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Connect to database
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Get inputs
        username = findViewById(R.id.editTextText2);
        password = findViewById(R.id.editTextTextPassword2);
        address = findViewById(R.id.editTextTextAddress);
        gymId = findViewById(R.id.editTextTextGym);
        registerButton = findViewById(R.id.buttonRegister);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInputs();
            }
        });

    }

    /** Method from Callback interface, registers user if all inputs are correct **/
    @Override
    public void onContinue() {
        registerUser();
    }

    /** Method from Callback interface to handle in event of an error **/
    @Override
    public void onError() {

    }

    private void registerUser(){
        GymUser newUser = new GymUser(
                username.getText().toString(),
                password.getText().toString(),
                address.getText().toString(),
                Integer.valueOf(gymId.getText().toString())
        );

        // Write to firebase
        usersRef.child(newUser.getUsername()).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("You are now a GymHub member.");
                    } else {
                        showToast("There has been an error with your registration.");
                    }
                });

    }

    /** Checks inputs. First checks for any empty fields, then checks username doesn't already exist **/
    private void checkInputs() {
        // Check if any field is empty
        if (
                username.getText().toString().isEmpty() ||
                        password.getText().toString().isEmpty() ||
                        address.getText().toString().isEmpty() ||
                        gymId.getText().toString().isEmpty()
        ) {
            showToast("Please fill out all fields.");
            return;
        }

        // If all fields full, check if the username already exists in the database
        String inputUsername = username.getText().toString();
        usersRef.child(inputUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username already exists
                    showToast("Username already exists. Please choose a different username.");
                } else {
                    onContinue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors that may occur
                showToast("An error occurred. Please try again.");
            }
        });

    }

    private void showToast(String message){
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
    }
}