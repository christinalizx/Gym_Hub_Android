package edu.northeastern.gymhub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.gymhub.Utils.AndroidUtil;
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
    private ImageButton imageButtonBackArrow;
    private FindUsersAdapter adapter;
    private ValueEventListener valueEventListener;

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
                finish();
            }
        });



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
        setUserInfo();
    }

    private void setAdapter(List<String> connections) {
        setOnClickListener();
        adapter = new FindUsersAdapter(this, usersList, listener, connections);
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
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onUnfollowButtonClick(View v, int position) {
                GymUser clickedUser = usersList.get(position);
                removeConnection(clickedUser);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onUserClicked(View v, int position) {
                GymUser clickedUser = usersList.get(position);
                showClickedUser(clickedUser);
            }


        };
    }

    private void showClickedUser(GymUser gymUser) {

        AndroidUtil.showClickedUserDialogBox(this, gymUser);

//        // Inflate the dialog layout
//        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_user_info, null);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(dialogView);
//
//        // Find views in the inflated layout
//        final TextView userName = dialogView.findViewById(R.id.textViewUserName);
//        final TextView username = dialogView.findViewById(R.id.textViewUsername);
//        final TextView gym = dialogView.findViewById(R.id.textViewGym);
//        final TextView status = dialogView.findViewById(R.id.textViewStatus);
//        final ImageView pic = dialogView.findViewById(R.id.imageViewUser);
//        final Button buttonOK = dialogView.findViewById(R.id.buttonOK);
//
//        // Create and show the dialog
//        final AlertDialog dialog = builder.create();
//        dialog.show();
//
//        // Set click listeners for Cancel and Update buttons
//        buttonOK.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        // Set info
//        userName.setText(gymUser.getName());
//        username.setText(gymUser.getUsername());
//        gym.setText(gymUser.getGym());
//        if(gymUser.getStatus()){
//            status.setText("Currently working out!");
//        }else{
//            status.setText("Not currently at gym.");
//        }
//
//        // Set profile image
//        FirebaseStorage.getInstance().getReference().child("profile_pics")
//                .child(gymUser.getUsername()).getDownloadUrl()
//                .addOnCompleteListener(task -> {
//                    if(task.isSuccessful()){
//                        Uri uri = task.getResult();
//                        AndroidUtil.setProfilePic(this, uri, pic);
//                    }
//                });
//

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
                                        adapter.updateConnections(curUser.getConnections());
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
                                        adapter.updateConnections(curUser.getConnections());
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

        valueEventListener = new ValueEventListener() {
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
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                handleDatabaseError(databaseError);
            }
        };

        usersRef.addValueEventListener(valueEventListener);
    }

    private void handleDatabaseError(DatabaseError databaseError) {
        Toast.makeText(FindUsersActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void showToast(String message) {
        Toast.makeText(FindUsersActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the ValueEventListener to prevent memory leaks
        if (usersRef != null) {
            usersRef.removeEventListener(valueEventListener);
        }
    }




}