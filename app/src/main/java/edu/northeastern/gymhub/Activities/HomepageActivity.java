package edu.northeastern.gymhub.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.northeastern.gymhub.Models.GymUser;
import edu.northeastern.gymhub.Models.ScheduleItem;
import edu.northeastern.gymhub.R;
import edu.northeastern.gymhub.Utils.AndroidUtil;
import edu.northeastern.gymhub.Views.ScheduleAdapter;

public class HomepageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private DatabaseReference schedulesRef;
    private DatabaseReference trafficRef;
    private DatabaseReference userRef;
    private BarChart barChart;

    private String gymName;
    private String curUsername;
    private Button scanInButton;


    // for recycler view
    private List<String> connections;

    // from video
    RecyclerView horizontalRV;
    ArrayList<String> dataSource;
    LinearLayoutManager linearLayoutManager;
    HorizontalRVAdapter horizontalRVAdapter;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private String getUsername;
    private List<String> userConnections;

    Uri selectedImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Inside onCreate() method, after setting the content view
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        gymName = preferences.getString("GymName", "Default Gym Name").toLowerCase();
        curUsername = preferences.getString("Username", "Default User");
        TextView gymNameTextView = findViewById(R.id.textViewGymName);
        gymNameTextView.setText(gymName);







        // connect database
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference().child("users");

        // Profile pic recycler view
        horizontalRV = findViewById(R.id.horizontalRecyclerView);

        //Setting the data source
        //getUserConnections();
        fetchData();
















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

        // Go to workout page
        ImageButton workout = findViewById(R.id.imageButtonWorkout);
        workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomepageActivity.this, WorkoutPageActivity.class));
            }
        });

        // Go to forum page
        ImageButton forum = findViewById(R.id.imageButtonForum);
        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomepageActivity.this, ForumActivity.class));
            }
        });

        // Go to person page
        ImageButton personPage = findViewById(R.id.imageButtonInbox);
        personPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomepageActivity.this, PersonalInformationDetailsPageActivity.class));
            }
        });

        // Scan in user to gym
        scanInButton = findViewById(R.id.scanInButton);
        userRef = FirebaseDatabase.getInstance().getReference("users").child(curUsername);
        scanInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanInCurUser();
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

    private void getUserConnections() {
        database.getReference("users").child(curUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GymUser curUser = snapshot.getValue(GymUser.class);
                userConnections = curUser.getConnections();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                AndroidUtil.handleDatabaseError(error);
            }
        });
    }

    private void fetchData() {
        final ArrayList<String> userConnections = new ArrayList<>();

        database.getReference("users").child(curUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GymUser curUser = snapshot.getValue(GymUser.class);
                List<String> connections = curUser.getConnections();

                for(String connection : connections){
                    if(!connection.equals("") || !connection.isEmpty()){
                        userConnections.add(connection);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                AndroidUtil.handleDatabaseError(error);
            }
        });


        final ArrayList<String> usernamesList = new ArrayList<>();
        final ArrayList<String> nameList = new ArrayList<>();
        final ArrayList<String> imageUris = new ArrayList<>();

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Get username, status, and name from each user
                    GymUser user = userSnapshot.getValue(GymUser.class);


                    String username = user.getUsername();
                    Boolean status = user.getStatus();
                    String name = user.getName();


                    // Check if the user is at gym and is a connection
                    if (status && userConnections.contains(username)) {
                        // Add data to respective lists
                        usernamesList.add(username);
                        nameList.add(name);

                        StorageReference picRef = FirebaseStorage.getInstance().getReference().child("profile_pics").child(username);
                        picRef.getDownloadUrl().addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Uri uri = task.getResult();
                                imageUris.add(uri.toString());
                            } else {
                                String defaultUri = "https://firebasestorage.googleapis.com/v0/b/gymhub-7a220.appspot.com/o/profile_pics%2Fhead_img.jpeg?alt=media&token=16842243-c72c-490e-8f0c-52bc9a7d1024";
                                imageUris.add(defaultUri);
                            }
                        });


                    }
                }

                setHorizontalRV(usernamesList, nameList, imageUris);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                AndroidUtil.handleDatabaseError(databaseError);
            }
        });
    }

    private void setHorizontalRV(ArrayList<String> usernamesList, ArrayList<String> nameList, ArrayList<String> imageUris) {
        linearLayoutManager = new LinearLayoutManager(HomepageActivity.this, LinearLayoutManager.HORIZONTAL, false);
        horizontalRVAdapter = new HorizontalRVAdapter(HomepageActivity.this, usernamesList, nameList, imageUris);
        horizontalRV.setLayoutManager(linearLayoutManager);
        horizontalRV.setAdapter(horizontalRVAdapter);
    }



    private void scanInCurUser() {

        DatabaseReference statusRef = userRef.child("status");
        statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean status = snapshot.getValue(Boolean.class);

                // If not scanned in
                if(!status){
                    statusRef.setValue(true);
                    scanInButton.setText("Scan Out");
                    int color = ContextCompat.getColor(HomepageActivity.this, R.color.lightRed);
                    scanInButton.setBackgroundColor(color);
                    showToast("You have scanned in.");

                // If scanned out
                } else{
                    statusRef.setValue(false);
                    scanInButton.setText("Scan In");
                    int color = ContextCompat.getColor(HomepageActivity.this, R.color.green);
                    scanInButton.setBackgroundColor(color);
                    showToast("You have scanned out.");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showToast("Error scanning in.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(HomepageActivity.this, message, Toast.LENGTH_SHORT).show();
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









    class HorizontalRVAdapter extends RecyclerView.Adapter<HorizontalRVAdapter.MyHolder> {
        ArrayList<String> names;
        ArrayList<String> usernames;
        ArrayList<String> imageUris;
        Context context;

        public HorizontalRVAdapter(Context context, ArrayList<String> usernames, ArrayList<String> names, ArrayList imageUris ) {
            this.context = context;
            this.names = names;
            this.usernames = usernames;
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(HomepageActivity.this).inflate(R.layout.horizontal_rv_item, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            holder.tvTitle.setText(names.get(position));

            showToast(usernames.get(position));
            FirebaseStorage.getInstance().getReference().child("profile_pics")
                    .child(usernames.get(position)).getDownloadUrl()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Uri uri = task.getResult();
                            AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return names.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            ImageView profilePic;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvTitle);
                profilePic = itemView.findViewById(R.id.horizontalRvPic);
            }
        }

    }

}