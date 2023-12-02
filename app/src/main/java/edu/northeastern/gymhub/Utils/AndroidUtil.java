package edu.northeastern.gymhub.Utils;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;

import edu.northeastern.gymhub.Activities.FindUsersActivity;

public class AndroidUtil {
    public static void handleDatabaseError( DatabaseError databaseError) {
        Log.e(TAG, "Database Error: " + databaseError.getMessage());
    }

    private static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
