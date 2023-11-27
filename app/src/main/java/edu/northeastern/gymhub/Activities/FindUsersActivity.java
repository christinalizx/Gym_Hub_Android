package edu.northeastern.gymhub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
    private FindUsersAdapter.RecyclerClickListener listener;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private String curUsername;
    private GymUser curUser;
    private ImageButton imageButtonBackArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);

        // Acquire user's username
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        curUsername = preferences.getString("Username", "Default User");

        // Connect to the database
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Recycler view
        usersList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewFindUsers);

        // Set buttons
        imageButtonBackArrow = findViewById(R.id.imageButtonBackArrow);
        imageButtonBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindUsersActivity.this, ForumActivity.class);
                startActivity(intent);
            }
        });

        // Go to forum page
        ImageButton forum = findViewById(R.id.imageButtonForum);
        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindUsersActivity.this, ForumActivity.class);
                startActivity(intent);
            }
        });

        // Go to workout page
        ImageButton workout = findViewById(R.id.imageButtonWorkout);
        workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the Workout activity
                Intent intent = new Intent(FindUsersActivity.this, WorkoutPageActivity.class);
                startActivity(intent);
            }
        });

        //getCurrentUser();
        setUserInfo();
        setAdapter();
    }

    private void setAdapter() {
        setOnClickListener();
        FindUsersAdapter adapter = new FindUsersAdapter(usersList, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {
        listener = new FindUsersAdapter.RecyclerClickListener() {
            @Override
            public void onFollowButtonClick(View v, int position) {
                // Perform actions based on the clicked user
                GymUser clickedUser = usersList.get(position);
                addNewConnection(clickedUser);

            }
        };
    }

    /** Adds the new connection to the users list in the firebase **/
    private void addNewConnection(GymUser connection) {
        if (curUsername != null && connection != null) {
            usersRef.orderByKey().equalTo(curUsername).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GymUser curUser = snapshot.getValue(GymUser.class);

                    if (curUser != null) {
                        curUser.addConnection(connection.getUsername());

                        // Update database
                        usersRef.child(curUsername).setValue(curUser)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        showToast("You are now following " + connection.getName());
                                    } else {
                                        showToast("Unable to follow user.");
                                    }
                                });

                    } else {
                        showToast("Failed to parse user data");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    handleDatabaseError(error);
                }
            });

        } else {
            showToast("Error finding user.");
        }

//            usersRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    GymUser curUser = snapshot.getValue(GymUser.class);
//                    showToast("user " + curUser.getName());
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    // Handle database error
//                    handleDatabaseError(error);
//                }
//            });

            // Update user connections list
//            curUser.addConnection(connection.getUsername());
//
//            showToast("following" + connection.getName());

//            // Update database
//            usersRef.child(curUsername).setValue(curUser)
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            showToast("You are now following " + connection.getName());
//                        } else {
//                            showToast("Unable to follow user.");
//                        }
//                    });

//            // Retrieve user data from Firebase
//            DatabaseReference currentUserRef = usersRef.child(curUsername);
//
//            currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.exists()) {
//                        // Add user to connections list
//                        GymUser curUser = dataSnapshot.getValue(GymUser.class);
//                        curUser.addConnection(connection.getUsername());
//
//                        // Update database
//                        usersRef.child(curUsername).setValue(curUser)
//                                .addOnCompleteListener(task -> {
//                                    if (task.isSuccessful()) {
//                                        showToast("You are now following " + connection.getName());
//                                    } else {
//                                        showToast("Unable to follow user.");
//                                    }
//                                });
//                    } else {
//                        // Handle the case where the username does not exist in the database
//                        // You can show an error message or take appropriate action
//                        showToast("User not found in the database");
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle database error
//                    handleDatabaseError(databaseError);
//                }
//            });
//        } else {
//            // Handle null cases
//            showToast("Invalid user or connection");
    }

    /** Set users to recycler view **/
    private void setUserInfo() {
    // Add a ValueEventListener to retrieve data from the "users" node
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing usersList to avoid duplicates
                usersList.clear();

//                GymUser gymUser = dataSnapshot.getValue(GymUser.class)

                // Loop through each child node under "users"
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve user data
                    String name = userSnapshot.child("name").getValue(String.class);
                    String username = userSnapshot.child("username").getValue(String.class);
                    String password = userSnapshot.child("password").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);
                    String gym = userSnapshot.child("gym").getValue(String.class);

                    // Create a new GymUser object
                    GymUser gymUser = new GymUser(name, username, password, email, gym);

                    if(!gymUser.getUsername().equals(curUsername)){
                        // Add the GymUser to the usersList
                        usersList.add(gymUser);
                    }
                }

                // Notify the adapter that the data set has changed
                recyclerView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                handleDatabaseError(databaseError);
            }
        });
    }


    /** Fetch current user from database as a GymUser object **/
    private void getCurrentUser() {
        if (curUsername != null) {
            usersRef.orderByKey().equalTo(curUsername).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    curUser = snapshot.getValue(GymUser.class);

                    if (curUser != null) {
                        showToast("User found: " + curUser.getName());
                    } else {
                        showToast("Failed to parse user data");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    handleDatabaseError(error);
                }
            });

        } else {
            showToast("Error finding user.");
        }
    }


    private void handleDatabaseError(DatabaseError databaseError) {
        Toast.makeText(FindUsersActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void showToast(String message) {
        Toast.makeText(FindUsersActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}