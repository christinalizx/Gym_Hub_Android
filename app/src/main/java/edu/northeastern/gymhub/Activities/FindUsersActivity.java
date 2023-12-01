package edu.northeastern.gymhub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.gymhub.Views.FindUsersAdapter;
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


        setUserInfo();
        usersRef.child(curUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Add connection to user
                GymUser curUser = snapshot.getValue(GymUser.class);
                List<String> connections = curUser.getConnections();
                setAdapter(connections);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                handleDatabaseError(error);
            }
        });
    }

    private void setAdapter(List<String> connections) {
        setOnClickListener();
        FindUsersAdapter adapter = new FindUsersAdapter(this, usersList, listener, connections);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    /** Sets methods to adapter listener **/
    private void setOnClickListener() {
        listener = new FindUsersAdapter.RecyclerClickListener() {
            @Override
            public void onFollowButtonClick(View v, int position) {
                GymUser clickedUser = usersList.get(position);
                addNewConnection(clickedUser);

            }

            @Override
            public void onUnfollowButtonClick(View v, int position) {
                GymUser clickedUser = usersList.get(position);
                removeConnection(clickedUser);
            }
        };
    }

    /** Unfollows an user **/
    private void removeConnection(GymUser connection) {
        if(curUsername != null && connection != null && connection.getClass().equals(GymUser.class)){
            usersRef.child(curUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GymUser curUser = snapshot.getValue(GymUser.class);

                    if (curUser != null) {
                        List<String> connections = curUser.getConnections();
                        connections.remove(connection.getUsername());
                        curUser.setConnections(connections);

                        // Update database
                        usersRef.child(curUsername).setValue(curUser)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Notify the adapter that the data set has changed
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                        showToast("You are no longer following " + connection.getName() + ".");
                                    } else {
                                        showToast("Failed to unfollow");
                                    }
                                });
                    } else {
                        showToast("Failed to retrieve current user data");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    handleDatabaseError(error);
                }
            });
        } else {
            showToast("Error fetching user data.");
        }
    }

    /** Adds the new connection to the users list in the firebase **/
    private void addNewConnection(GymUser connection) {
        if (curUsername != null && connection != null && connection.getClass().equals(GymUser.class)) {

            usersRef.child(curUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Add connection to user
                    GymUser curUser = snapshot.getValue(GymUser.class);
                    if (curUser != null) {
                        curUser.addConnection(connection.getUsername());

                        // Update database
                        usersRef.child(curUsername).setValue(curUser)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Notify the adapter that the data set has changed
                                        recyclerView.getAdapter().notifyDataSetChanged();
                                        showToast("You are now following " + connection.getName() + ".");
                                    } else {
                                        showToast("Connection failed");
                                    }
                                });
                    } else {
                        showToast("Failed to retrieve current user data");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    handleDatabaseError(error);
                }
            });
        } else {
            showToast("Error fetching user data.");
        }
    }


    /** Set users to recycler view **/
    private void setUserInfo() {
    // Add a ValueEventListener to retrieve data from the "users" node

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear the existing usersList to avoid duplicates
                usersList.clear();

                // Loop through each child node under "users"
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Retrieve user data
                    GymUser gymUser = userSnapshot.getValue(GymUser.class);

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

    private void handleDatabaseError(DatabaseError databaseError) {
        Toast.makeText(FindUsersActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void showToast(String message) {
        Toast.makeText(FindUsersActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}