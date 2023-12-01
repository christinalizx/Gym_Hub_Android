package edu.northeastern.gymhub.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.northeastern.gymhub.Models.ScheduleItem;
import edu.northeastern.gymhub.R;
import edu.northeastern.gymhub.Views.ScheduleAdapter;

public class HomepageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private DatabaseReference schedulesRef;
    private DatabaseReference trafficRef;
    private BarChart barChart;

    private String gymName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Inside onCreate() method, after setting the content view
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        gymName = preferences.getString("GymName", "Default Gym Name").toLowerCase();
        TextView gymNameTextView = findViewById(R.id.textViewGymName);
        gymNameTextView.setText(gymName);

        // Settings page
        ImageButton settingsButton = findViewById(R.id.imageButtonSettings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the settings activity
                Intent intent = new Intent(HomepageActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // Go to forum page
        ImageButton forum = findViewById(R.id.imageButtonForum);
        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomepageActivity.this, ForumActivity.class);
                startActivity(intent);
            }
        });


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

        Button schedule = findViewById(R.id.buttonCheckThisWeek);
        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomepageActivity.this, ScheduleActivity.class));
            }
        });

        // Connect to the schedules node in the database
        schedulesRef = FirebaseDatabase.getInstance().getReference("schedules").child(gymName);
        trafficRef = FirebaseDatabase.getInstance().getReference("gyms").child(gymName);

        // Initialize RecyclerView and its adapter
        recyclerView = findViewById(R.id.recyclerViewSchedule);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scheduleAdapter = new ScheduleAdapter();
        recyclerView.setAdapter(scheduleAdapter);


        // Fetch and display today's schedule
        fetchAndDisplayTodaySchedule();

        barChart = findViewById(R.id.barChart);
        fetchAndDisplayHourlyTraffic();

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

    private void fetchAndDisplayHourlyTraffic() {
        // Get the current day
        String currentDay = getCurrentDay();

        // Add a ValueEventListener to fetch data from Firebase for today's hourly traffic
        trafficRef.child("hourly_traffic").child(currentDay).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Call the method to handle hourly traffic data changes
                handleHourlyTrafficChange(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomepageActivity.this, "Failed to load hourly traffic data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to handle hourly traffic data changes
    private void handleHourlyTrafficChange(DataSnapshot dataSnapshot) {
        List<Integer> hours = new ArrayList<>();
        List<Integer> trafficValues = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        // Iterate through days (e.g., "Monday", "Tuesday", etc.)
        for (DataSnapshot daySnapshot : dataSnapshot.getChildren()) {
            if (daySnapshot.hasChildren()) {
                // Extracting the traffic value for each hour
                int hour = daySnapshot.child("hour").getValue(Integer.class);
                int traffic = daySnapshot.child("traffic").getValue(Integer.class);
                int color = determineColor(traffic);

                // Append data to arrays
                hours.add(hour);
                trafficValues.add(traffic);
                colors.add(color);
            }
        }
        // Now you have the list of hours and traffic values for all days
        setupBarChart(hours, trafficValues, colors);
    }

    private int determineColor(int traffic) {
        // Customize the color based on the number of people
        if (traffic > 200) {
            return Color.rgb(255, 69, 0); // Dark Red
        } else if (traffic >= 150 && traffic <= 200) {
            return Color.rgb(255, 99, 71); // Red
        } else if (traffic >= 100 && traffic < 150) {
            return Color.rgb(255, 165, 0); // Orange
        } else {
            return Color.rgb(144, 238, 144); // Light Green
        }
    }


    private void setupBarChart(List<Integer> hours, List<Integer> trafficValues, List<Integer> colors) {
        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < hours.size(); i++) {
            entries.add(new BarEntry(hours.get(i), trafficValues.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Hourly Traffic");
        dataSet.setColors(colors);

        BarData data = new BarData(dataSet);

        // Set the data to the chart
        barChart.setData(data);

        // Customize the appearance of the chart if needed

        // Refresh the chart to update the display
        barChart.invalidate();
    }

}