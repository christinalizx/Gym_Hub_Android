package edu.northeastern.gymhub.Activities;

import android.app.Activity;
import android.app.Person;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.northeastern.gymhub.R;
import edu.northeastern.gymhub.Utils.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class PersonalInformationDetailsPageActivity extends AppCompatActivity {

    private ImageView profilePic;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;
    private Button updateButton;
    private Button logoutButton;
    private String curUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information_details_page);

        // Get current user
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        curUsername = preferences.getString("Username", "Default User");

        // Set profile pic
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data != null && data.getData() != null){
                            selectedImageUri = data.getData();
                            setProfilePic(PersonalInformationDetailsPageActivity.this, selectedImageUri, profilePic);
                        }
                    }
                }
        );
        profilePic = findViewById(R.id.people_logo);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(PersonalInformationDetailsPageActivity.this).cropSquare().compress(512).maxResultSize(512, 512)
                        .createIntent(new Function1<Intent, Unit>() {
                            @Override
                            public Unit invoke(Intent intent) {
                                imagePickerLauncher.launch(intent);
                                return null;
                            }
                        });

            }
        });

        // Logout button
        logoutButton = findViewById(R.id.buttonLogout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        // Update button
        updateButton = findViewById(R.id.buttonUpdateProfile);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });


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


        getUserPic(curUsername);
    }

    private void logoutUser() {
        // Clear SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Start HomepageActivity
        Intent intent = new Intent(PersonalInformationDetailsPageActivity.this, SignInActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity to prevent going back to it from the HomepageActivity
    }


    private void getUserPic(String curUsername) {
        StorageReference picRef = FirebaseStorage.getInstance().getReference().child("profile_pics").child(curUsername);
        picRef.getDownloadUrl().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Uri uri = task.getResult();
                setProfilePic(PersonalInformationDetailsPageActivity.this, uri, profilePic);
            }
        });
    }

    private void updateProfile() {
        StorageReference picRef = FirebaseStorage.getInstance().getReference().child("profile_pics")
                .child(curUsername);

        if(selectedImageUri != null){
            picRef.putFile(selectedImageUri).addOnCompleteListener(task -> {
                showToast("Successfully updated profile pic.");
            });
        }

    }

    private void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    private void showToast(String message) {
        Toast.makeText(PersonalInformationDetailsPageActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_right);
    }
}