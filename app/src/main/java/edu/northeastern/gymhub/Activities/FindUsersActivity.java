package edu.northeastern.gymhub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.northeastern.gymhub.Adapters.FindUsersAdapter;
import edu.northeastern.gymhub.Models.GymUser;
import edu.northeastern.gymhub.R;

public class FindUsersActivity extends AppCompatActivity {

    private ArrayList<GymUser> usersList;
    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);

        usersList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewFindUsers);

        // Connect to the database
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        setAdapter();
        setUserInfo();

    }

    private void setAdapter() {
        FindUsersAdapter adapter = new FindUsersAdapter(usersList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }



//    private void setUserInfo() {
//        // Add a ValueEventListener to retrieve data from the "users" node
//        usersRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // Clear the existing usersList to avoid duplicates
//                usersList.clear();
//
//                // Loop through each child node under "users"
//                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
//                    // Retrieve user data
//                    String name = userSnapshot.child("name").getValue(String.class);
//                    String username = userSnapshot.child("username").getValue(String.class);
//                    String password = userSnapshot.child("password").getValue(String.class);
//                    String email = userSnapshot.child("email").getValue(String.class);
//                    String gym = userSnapshot.child("gym").getValue(String.class);
//
//                    // Create a new GymUser object
//                    GymUser gymUser = new GymUser(name, username, password, email, gym);
//
//                    // Add the GymUser to the usersList
//                    usersList.add(gymUser);
//                }
//
//                // Notify the adapter that the data set has changed
//                recyclerView.getAdapter().notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle database error
//                handleDatabaseError(databaseError);
//            }
//        });
//    }

        private void setUserInfo() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Use getValue with GymUser.class directly
                    GymUser gymUser = userSnapshot.getValue(GymUser.class);

                    // Add the GymUser to the usersList
                    usersList.add(gymUser);
                }

                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                handleDatabaseError(databaseError);
            }
        });
    }

    private void handleDatabaseError(DatabaseError databaseError) {
        Toast.makeText(FindUsersActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }

}