package edu.northeastern.gymhub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.github.tlaabs.timetableview.Schedule;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import edu.northeastern.gymhub.R;

public class RegisterClassActivity extends AppCompatActivity {
    private String gymName;

    private String className;
    private String startTime;
    private String endTime;
    private String userName;
    private Schedule selectedSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_class);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        gymName = preferences.getString("GymName", "Default Gym Name").toLowerCase();
        userName = preferences.getString("UserName","User Name").toLowerCase();
        TextView gymNameTextView = findViewById(R.id.textViewGymName);
        gymNameTextView.setText(gymName);

        TextView middleGym = findViewById(R.id.textViewMiddleGymName);
        middleGym.setText(gymName);

        // Retrieve data from Intent
        Intent intent = getIntent();
        selectedSchedule = (Schedule) intent.getSerializableExtra("selectedSchedule");

        // Access data from the Schedule object
        String className = selectedSchedule.getClassTitle();
        String startTime = selectedSchedule.getStartTime().toString();
        String endTime = selectedSchedule.getEndTime().toString();
        TextView start_time = findViewById(R.id.textViewTime);
        start_time.setText(formatTime(startTime, endTime));

        TextView classRegister = findViewById(R.id.textViewClassName);
        classRegister.setText(className);

        Button register = findViewById(R.id.buttonRegister);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                // Create a map to represent the data
                Map<String, Object> data = new HashMap<>();
                data.put("className", className);
                // Upload the data to the specific user and classRegistered
                usersRef.child(userName).child(className).setValue(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data successfully uploaded
                                Toast.makeText(getApplicationContext(), "Data uploaded successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                Toast.makeText(getApplicationContext(), "Failed to upload data", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        ImageButton home = findViewById(R.id.imageButtonHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterClassActivity.this, HomepageActivity.class));
            }
        });

        // Go to forum page
        ImageButton forum = findViewById(R.id.imageButtonForum);
        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterClassActivity.this, ForumActivity.class);
                startActivity(intent);
            }
        });


        // Go to workout page
        ImageButton workout = findViewById(R.id.imageButtonWorkout);
        workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the Workout activity
                Intent intent = new Intent(RegisterClassActivity.this, WorkoutPageActivity.class);
                startActivity(intent);
            }
        });
    }
    private String formatTime(String startTime, String finishTime) {
        return startTime + " - " + finishTime;
    }
}