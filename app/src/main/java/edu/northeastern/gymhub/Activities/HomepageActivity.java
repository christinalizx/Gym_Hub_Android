package edu.northeastern.gymhub.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import edu.northeastern.gymhub.R;

public class HomepageActivity extends AppCompatActivity {

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
                Log.i("aa","tempt to start forum page");
                Intent intent = new Intent(HomepageActivity.this, WorkoutPageActivity.class);
                startActivity(intent);
            }
        });

        // Go to forum page
        ImageButton imageButtonForum = findViewById(R.id.imageButtonForum);
        imageButtonForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the Workout activity
                Intent intent = new Intent(HomepageActivity.this, ForumActivity.class);
                startActivity(intent);
            }
        });
    }
}