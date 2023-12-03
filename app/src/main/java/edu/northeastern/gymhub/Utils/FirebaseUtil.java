package edu.northeastern.gymhub.Utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseUtil {

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference usersRef = database.getReference("users");

}
