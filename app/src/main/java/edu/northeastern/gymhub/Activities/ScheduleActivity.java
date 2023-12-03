package edu.northeastern.gymhub.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import edu.northeastern.gymhub.R;

public class ScheduleActivity extends AppCompatActivity {
    private String gymName;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        TimetableView timetable = findViewById(R.id.timetable);

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        gymName = preferences.getString("GymName", "Default Gym Name").toLowerCase();
        username = preferences.getString("Username", "Default User");
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
            public void onCallback(ArrayList<Schedule> schedules) {
                timetable.add(schedules);
            }
        });


        timetable.setOnStickerSelectEventListener(new TimetableView.OnStickerSelectedListener() {
            @Override
            public void OnStickerSelected(int idx, ArrayList<Schedule> schedules) {
                    // Handle the selected sticker (schedule)
                    Schedule selectedSchedule = schedules.get(0);

                    // Extract information from the selected schedule
                    String className = selectedSchedule.getClassTitle();
                    Time startTime = selectedSchedule.getStartTime();
                    Time endTime = selectedSchedule.getEndTime();
                    int selectedDayOfWeek = selectedSchedule.getDay()+2;
                    String nextOccurrenceDate = getNextOccurrenceDate(selectedDayOfWeek);

                // Show an AlertDialog to confirm registration
                    new AlertDialog.Builder(ScheduleActivity.this)
                            .setTitle("Register Course")
                            .setMessage("Do you want to register for the course?\nClass: " + className
                                    + "\nTime: " + getTimeString(startTime) + " - " + getTimeString(endTime)
                            + "\nDate: " + nextOccurrenceDate)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // User clicked "Yes", upload data to Firebase
                                    registerCourse(className, startTime, endTime, nextOccurrenceDate);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // User clicked "No" or cancelled, do nothing
                                    dialog.dismiss();
                                }
                            })
                            .show();
            }
        });
    }
    private String getNextOccurrenceDate(int selectedDayOfWeek) {
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int daysUntilNextOccurrence = (selectedDayOfWeek - currentDayOfWeek + 7) % 7;

        // If today is the selected day, add 7 days to get the next occurrence
        if (daysUntilNextOccurrence <= 0) {
            daysUntilNextOccurrence += 7;
        }

        calendar.add(Calendar.DAY_OF_YEAR, daysUntilNextOccurrence);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private String getTimeString(Time time) {
        return String.format(Locale.getDefault(), "%02d:%02d", time.getHour(), time.getMinute());
    }

    private void registerCourse(String className, Time startTime, Time endTime, String date) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(username).child("classScheduled");

            // Create a unique key for the registration entry
            String registrationKey = userRef.push().getKey();

            // Create a map with the data to be uploaded
            Map<String, Object> registrationData = new HashMap<>();
            registrationData.put("className", className);
            registrationData.put("startTime", startTime);
            registrationData.put("endTime", endTime);
            registrationData.put("date", date);

            // Upload the data
            userRef.child(registrationKey).setValue(registrationData);

            // Inform the user that the course has been registered (you can use a Toast or another UI element)
            Toast.makeText(getApplicationContext(), "Course registered!", Toast.LENGTH_SHORT).show();
        }


    private void fetchScheduleDataForThisWeek(FirebaseCallback callback) {
        DatabaseReference schedulesRef = FirebaseDatabase.getInstance().getReference("schedules").child(gymName);


        schedulesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                        for (DataSnapshot timeSnapshot : daySnapshot.getChildren()) {
                            for (DataSnapshot classSnapshot : timeSnapshot.getChildren()) {
                                String className = classSnapshot.child("name").getValue(String.class);
                                String startTimeStr = classSnapshot.child("start_time").getValue(String.class);
                                String endTimeStr = classSnapshot.child("finish_time").getValue(String.class);

                                // Format start time
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                Date startTimeDate;
                                try {
                                    ArrayList<Schedule> allSchedules = new ArrayList<>();
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
                                    schedule.setDay(firebaseDay);

                                    // Add schedule to the list
                                    allSchedules.add(schedule);
                                    callback.onCallback(allSchedules);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    // Callback interface for Firebase asynchronous data fetching
    private interface FirebaseCallback {
        void onCallback(ArrayList<Schedule> schedules);
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