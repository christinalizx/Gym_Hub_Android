package edu.northeastern.gymhub.Activities;

import android.app.Activity;
import android.app.Person;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.northeastern.gymhub.Models.GymUser;
import edu.northeastern.gymhub.R;
import edu.northeastern.gymhub.Utils.AndroidUtil;
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
    private TextView textViewName;
    private TextView textViewUsername;
    private TextView textViewUserEmail;
    private TextView textViewUserGym;
    private LinearLayout emailLayout;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private String curPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information_details_page);

        // Get current user
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        curUsername = preferences.getString("Username", "Default User");

        // Connect to the database
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Get objects
        textViewName = findViewById(R.id.textViewName);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewUserEmail = findViewById(R.id.textViewUserEmail);
        textViewUserGym = findViewById(R.id.textViewUserGym);

        // Set edit email
        emailLayout = findViewById(R.id.linearLayoutEmail);
        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailDialog();
            }
        });

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

        // Go to home page
        ImageButton home = findViewById(R.id.imageButtonHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalInformationDetailsPageActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        getUserPic(curUsername);


        // Retrieve user data from Firebase
        usersRef.child(curUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String gym = dataSnapshot.child("gym").getValue(String.class);
                    curPassword = dataSnapshot.child("password").getValue(String.class);

                    // Update UI with retrieved data
                    textViewName.setText(name);
                    textViewUsername.setText(curUsername);
                    textViewUserEmail.setText(email);
                    textViewUserGym.setText(gym);
                } else {
                    // Handle the case where the username does not exist in the database
                    // You can show an error message or take appropriate action
                    AndroidUtil.showToast(PersonalInformationDetailsPageActivity.this, "User not found in the database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                AndroidUtil.handleDatabaseError(databaseError);
            }
        });
    }

    private void showEmailDialog() {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_editemail, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Find views in the inflated layout
        final EditText editTextNewEmail = dialogView.findViewById(R.id.editTextNewEmail);
        final EditText editPassword = dialogView.findViewById(R.id.editPassword);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdate);

        // Create and show the dialog
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Set click listeners for Cancel and Update buttons
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the new email and password
                String newEmail = editTextNewEmail.getText().toString().trim();
                String newPassword = editPassword.getText().toString().trim();

                // Check if the provided email is valid
                if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                    showToast("Please enter a valid email");
                    return;
                }

                // Check if new email and password are not empty
                if (!newEmail.isEmpty() && !newPassword.isEmpty()) {
                    // Check if the provided password matches the password retrieved from the database
                    if (newPassword.equals(curPassword)) {
                        // Passwords match, update email in the database
                        updateEmail(newEmail);
                        dialog.dismiss();
                    } else {
                        showToast("Incorrect password. Please enter the correct password.");
                    }
                } else {
                    AndroidUtil.showToast(PersonalInformationDetailsPageActivity.this, "Please fill out all fields.");
                }
            }
        });
    }

    private void updateEmail(String newEmail) {
        // Update email in Firebase
        usersRef.child(curUsername).child("email").setValue(newEmail);

        // Update the local UI
        textViewUserEmail.setText(newEmail);

        AndroidUtil.showToast(PersonalInformationDetailsPageActivity.this, "Email updated");
    }

    private void logoutUser() {
        // Clear SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        updateUserStatus();

        // Start HomepageActivity
        Intent intent = new Intent(PersonalInformationDetailsPageActivity.this, SignInActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity to prevent going back to it from the HomepageActivity
    }

    private void updateUserStatus() {
        AndroidUtil.logoutUserFromDB(getApplicationContext(), curUsername);
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