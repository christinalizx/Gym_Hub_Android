package edu.northeastern.gymhub.Activities;

import android.app.Activity;
import android.app.Person;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;

import edu.northeastern.gymhub.R;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class PersonalInformationDetailsPageActivity extends AppCompatActivity {

    private ImageView profilePic;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information_details_page);


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

    private void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }




    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_right);
    }
}