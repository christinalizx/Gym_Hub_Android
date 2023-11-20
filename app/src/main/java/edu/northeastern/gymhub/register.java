package edu.northeastern.gymhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.northeastern.gymhub.utils.GymUser;
import edu.northeastern.gymhub.utils.JDBC;

public class register extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageButton submit = findViewById(R.id.imageButton);
        Spinner spinnerGym = findViewById(R.id.spinnerGym);
        String[] options = new String[]{"GymName1", "Power Gym B", "Iron Body Gym", "Flex Fitness",
                "Elite Wellness Center", "Peak Performance Gym", "Sculpt Fitness Club",
                "Golden Health Club", "Vitality Gym", "Epic Fitness Studio"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(), R.layout.simple_dropdown_item, options);
        spinnerGym.setAdapter(adapter);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Execute the database connection and registration on a separate thread
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        JDBC userHandler = JDBC.getInstance(); // Retrieve the JDBC instance once

                        try {
                            // Retrieve user input
                            TextView username = findViewById(R.id.editTextText2);
                            TextView password = findViewById(R.id.editTextTextPassword2);
                            TextView address = findViewById(R.id.editTextTextAddress);

                            // Get selected gym name
                            String selectedGymName = spinnerGym.getSelectedItem().toString();
                            // Retrieve gym_id based on selected gym name
                            int gymId = userHandler.getGymIdFromName(selectedGymName);

                            // Create GymUserModel object
                            GymUser gymUser = new GymUser();
                            gymUser.setUsername(username.getText().toString());
                            gymUser.setPassword(password.getText().toString());
                            gymUser.setAddress(address.getText().toString());
                            gymUser.setGymId(gymId);

                            // Insert the user into the database
                            boolean success = userHandler.addGymUser(gymUser);

                            // Update UI on the main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (success) {
                                        // If registration is successful, navigate to the home page
                                        startActivity(new Intent(register.this, homePage.class));
                                    } else {
                                        // Handle registration failure, e.g., display an error message
                                        Toast.makeText(register.this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            // Handle exceptions, log them, or display an error message
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(register.this, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } finally {
                            // Ensure that the JDBC instance is closed
                            userHandler.closeConnection();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shutdown the executor service when the activity is destroyed
        executorService.shutdown();
    }
}
