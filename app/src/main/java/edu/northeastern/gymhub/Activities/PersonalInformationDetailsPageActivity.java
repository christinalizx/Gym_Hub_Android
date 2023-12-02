package edu.northeastern.gymhub.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import edu.northeastern.gymhub.R;

public class PersonalInformationDetailsPageActivity extends AppCompatActivity {

    private ImageButton profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information_details_page);


        // Set profile pic
        profilePic = findViewById(R.id.people_logo);

        ImageButton workOutButton = findViewById(R.id.imageButtonWorkout);
        workOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the settings activity
                Intent intent = new Intent(PersonalInformationDetailsPageActivity.this, WorkoutPageActivity.class);
                startActivity(intent);
            }
        });
        // Go to forum page
        ImageButton forum = findViewById(R.id.imageButtonForum);
        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalInformationDetailsPageActivity.this, ForumActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_right);
    }
}