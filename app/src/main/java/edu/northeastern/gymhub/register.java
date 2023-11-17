package edu.northeastern.gymhub;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageButton submit = findViewById(R.id.imageButton);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve user input
                TextView username = findViewById(R.id.editTextText2);
                TextView password = findViewById(R.id.editTextTextPassword2);
                TextView address = findViewById(R.id.editTextTextAddress);
                TextView gymId = findViewById(R.id.editTextTextGym);

                // Create GymUserModel object
                GymUserModel gymUser = new GymUserModel();
                gymUser.setUsername(username.getText().toString());
                gymUser.setPassword(password.getText().toString());
                gymUser.setAddress(address.getText().toString());
                gymUser.setGymId(Integer.parseInt(gymId.getText().toString()));

                // Insert the user into the database
                GymUserHandler userHandler = new GymUserHandler();
                boolean success = userHandler.addGymUser(gymUser);

                if (success) {
                    // If registration is successful, navigate to the home page
                    startActivity(new Intent(register.this, homePage.class));
                } else {
                    // Handle registration failure, e.g., display an error message
                    Toast.makeText(register.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
