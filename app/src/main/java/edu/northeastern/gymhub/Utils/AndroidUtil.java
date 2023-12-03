package edu.northeastern.gymhub.Utils;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.northeastern.gymhub.Activities.FindUsersActivity;
import edu.northeastern.gymhub.Activities.PersonalInformationDetailsPageActivity;

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
}
