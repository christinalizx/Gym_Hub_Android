package edu.northeastern.gymhub.Utils;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.northeastern.gymhub.Activities.FindUsersActivity;
import edu.northeastern.gymhub.Activities.PersonalInformationDetailsPageActivity;
import edu.northeastern.gymhub.Models.GymUser;
import edu.northeastern.gymhub.R;

public class AndroidUtil {
    public static void handleDatabaseError( DatabaseError databaseError) {
        Log.e(TAG, "Database Error: " + databaseError.getMessage());
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    public static void showClickedUserDialogBox(Context context, GymUser gymUser) {
        // Inflate the dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_user_info, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Find views in the inflated layout
        final TextView userName = dialogView.findViewById(R.id.textViewUserName);
        final TextView username = dialogView.findViewById(R.id.textViewUsername);
        final TextView gym = dialogView.findViewById(R.id.textViewGym);
        final TextView status = dialogView.findViewById(R.id.textViewStatus);
        final ImageView pic = dialogView.findViewById(R.id.imageViewUser);
        final Button buttonOK = dialogView.findViewById(R.id.buttonOK);

        // Create and show the dialog
        final AlertDialog dialog = builder.create();
        dialog.show();

        // Set click listeners for Cancel and Update buttons
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Set info
        userName.setText(gymUser.getName());
        username.setText(gymUser.getUsername());
        gym.setText(gymUser.getGym());
        if(gymUser.getStatus()){
            status.setText("Currently working out!");
        }else{
            status.setText("Not currently at gym.");
        }

        // Set profile image
        FirebaseStorage.getInstance().getReference().child("profile_pics")
                .child(gymUser.getUsername()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(context, uri, pic);
                    }
                });


    }
}
