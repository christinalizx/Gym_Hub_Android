package edu.northeastern.gymhub.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stone.vega.library.VegaLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.List;

import edu.northeastern.gymhub.Models.ScheduleItem;
import edu.northeastern.gymhub.R;
import edu.northeastern.gymhub.Views.ScheduleAdapter;

public class HomepageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private DatabaseReference schedulesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Inside onCreate() method, after setting the content view
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String gymName = preferences.getString("GymName", "Default Gym Name");
        TextView gymNameTextView = findViewById(R.id.textViewGymName);
        gymNameTextView.setText(gymName);

        // Go to workout page
        ImageButton workout = findViewById(R.id.imageButtonWorkout);
        workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the Workout activity
                Intent intent = new Intent(HomepageActivity.this, WorkoutPageActivity.class);
                startActivity(intent);
            }
        });

        // Connect to the schedules node in the database
        schedulesRef = FirebaseDatabase.getInstance().getReference("schedules").child(gymName);

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerViewSchedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter = new ScheduleAdapter();
        recyclerView.setAdapter(scheduleAdapter);

        // Fetch and display today's schedule
        fetchAndDisplayTodaySchedule();
    }

    private void fetchAndDisplayTodaySchedule() {
        // Get the current day
        String currentDay = getCurrentDay();

        // Add a ValueEventListener to fetch data from Firebase for today's classes
        schedulesRef.child(currentDay).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Call the method to handle data changes
                handleDataChange(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomepageActivity.this, "Failed to load today's schedule.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to handle data changes in onDataChange
    private void handleDataChange(DataSnapshot dataSnapshot) {
        // Clear the previous data
        List<ScheduleItem> todayScheduleList = new ArrayList<>();

        // Iterate through the classes in the current day
        for (DataSnapshot timeSnapshot : dataSnapshot.getChildren()) {
            // Check if the snapshot has children (classes)
            if (timeSnapshot.hasChildren()) {
                for (DataSnapshot classSnapshot : timeSnapshot.getChildren()) {
                    String className = classSnapshot.child("name").getValue(String.class);
                    String startTime = classSnapshot.child("start_time").getValue(String.class);
                    String finishTime = classSnapshot.child("finish_time").getValue(String.class);

                    // Assuming you have a method to convert time to a readable format
                    String formattedTime = formatTime(startTime, finishTime);

                    // Add the data to the list
                    todayScheduleList.add(new ScheduleItem(className, formattedTime));
                }
            }
        }

        // Clear the previous data in the adapter
        scheduleAdapter.clearSchedule();

        // Add the today's schedule to the adapter
        scheduleAdapter.setScheduleList(todayScheduleList);

        // Notify the adapter that the data has changed
        scheduleAdapter.notifyDataSetChanged();
    }


    private String getCurrentDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date d = new Date();
        return sdf.format(d);
    }

    private String formatTime(String startTime, String finishTime) {
        // You may need to adjust the formatting based on your needs
        return startTime + " - " + finishTime;
    }
}
