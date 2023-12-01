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

import com.github.tlaabs.timetableview.Schedule;
import com.github.tlaabs.timetableview.Time;
import com.github.tlaabs.timetableview.TimetableView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.northeastern.gymhub.R;

public class ScheduleActivity extends AppCompatActivity {
    private String gymName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        TimetableView timetable = findViewById(R.id.timetable);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        gymName = preferences.getString("GymName", "Default Gym Name").toLowerCase();
        TextView gymNameTextView = findViewById(R.id.textViewGymName);
        gymNameTextView.setText(gymName);

        ImageButton home = findViewById(R.id.imageButtonHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScheduleActivity.this, HomepageActivity.class));
            }
        });

        fetchScheduleDataForThisWeek(new FirebaseCallback() {
            @Override
            public void onCallback(ArrayList<ArrayList<Schedule>> schedules) {
                // Flatten the list of lists into a single list
                ArrayList<Schedule> flattenedSchedules = new ArrayList<>();
                for (ArrayList<Schedule> daySchedules : schedules) {
                    timetable.add(daySchedules);
                }
            }
        });


        // Set the OnStickerSelectedListener for handling schedule selection
        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                // Handle the selected sticker (schedule)
                Schedule selectedSchedule = schedules.get(idx);

                // Extract information from the selected schedule
                String className = selectedSchedule.getClassTitle();
                String startTime = selectedSchedule.getStartTime().toString();
                String endTime = selectedSchedule.getEndTime().toString();

                // Example: Show a toast with the information
                String toastMessage = "Class: " + className +
                        "\nTime: " + startTime + " - " + endTime;

                Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchScheduleDataForThisWeek(FirebaseCallback callback) {
        DatabaseReference schedulesRef = FirebaseDatabase.getInstance().getReference("schedules").child(gymName);

        schedulesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<ArrayList<Schedule>> allSchedules = new ArrayList<>();

                // Get the current day
                String currentDay = getCurrentDay();
                int currentDayValue = mapFirebaseDay(currentDay); // Get the current day of the week

                // Iterate through the days in the snapshot
                for (DataSnapshot daySnapshot : dataSnapshot.getChildren()) {
                    String dayOfWeek = daySnapshot.getKey();
                    int firebaseDay = mapFirebaseDay(dayOfWeek);

                    // Calculate the difference in days to determine the actual date
                    int dayDifference = firebaseDay - currentDayValue;

                    // Check if the day is within the current week
                    if (Math.abs(dayDifference) < 7) {
                        ArrayList<Schedule> daySchedules = new ArrayList<>();
                        for (DataSnapshot timeSnapshot : daySnapshot.getChildren()) {
                            for (DataSnapshot classSnapshot : timeSnapshot.getChildren()) {
                                String className = classSnapshot.child("name").getValue(String.class);
                                String startTimeStr = classSnapshot.child("start_time").getValue(String.class);
                                String endTimeStr = classSnapshot.child("finish_time").getValue(String.class);

                                // Format start time
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                Date startTimeDate;
                                try {
                                    startTimeDate = sdf.parse(startTimeStr);
                                    Calendar startTimeCalendar = Calendar.getInstance();
                                    startTimeCalendar.setTime(startTimeDate);

                                    // Adjust start time based on the calculated day difference
                                    startTimeCalendar.add(Calendar.DAY_OF_WEEK, dayDifference);

                                    // Extract hours and minutes
                                    int startHour = startTimeCalendar.get(Calendar.HOUR_OF_DAY);
                                    int startMinute = startTimeCalendar.get(Calendar.MINUTE);

                                    // Create Time object
                                    Time startTime = new Time(startHour, startMinute);

                                    // Format end time
                                    Date endTimeDate = sdf.parse(endTimeStr);
                                    Calendar endTimeCalendar = Calendar.getInstance();
                                    endTimeCalendar.setTime(endTimeDate);

                                    // Adjust end time based on the calculated day difference
                                    endTimeCalendar.add(Calendar.DAY_OF_WEEK, dayDifference);

                                    // Extract hours and minutes
                                    int endHour = endTimeCalendar.get(Calendar.HOUR_OF_DAY);
                                    int endMinute = endTimeCalendar.get(Calendar.MINUTE);

                                    // Create Time object
                                    Time endTime = new Time(endHour, endMinute);

                                    // Create Schedule object
                                    Schedule schedule = new Schedule();
                                    schedule.setClassTitle(className);
                                    schedule.setStartTime(startTime);
                                    schedule.setEndTime(endTime);

                                    // Set the day of the week
                                    schedule.setDay(firebaseDay);

                                    // Add schedule to the day's list
                                    daySchedules.add(schedule);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        // Add the day's list to the overall list
                        allSchedules.add(daySchedules);
                    }
                }

                // Callback with the list of schedules for this week
                callback.onCallback(allSchedules);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }
    // Callback interface for Firebase asynchronous data fetching
    private interface FirebaseCallback {
        void onCallback(ArrayList<ArrayList<Schedule>> schedules);
    }

    private String getCurrentDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date d = new Date();
        return sdf.format(d);
    }

    private int mapFirebaseDay(String firebaseDay) {
        switch (firebaseDay.toLowerCase()) {
            case "monday":
                return 0;
            case "tuesday":
                return 1;
            case "wednesday":
                return 2;
            case "thursday":
                return 3;
            case "friday":
                return 4;
            case "saturday":
                return 5;
            default:
                return 6;
        }
    }
}