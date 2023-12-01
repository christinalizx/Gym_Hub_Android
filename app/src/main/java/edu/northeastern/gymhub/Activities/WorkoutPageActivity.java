package edu.northeastern.gymhub.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import edu.northeastern.gymhub.R;

public class WorkoutPageActivity extends AppCompatActivity {

    private EditText editTextWeightChoice;
    private EditText editTextSetsChoice;
    private EditText editTextRepsChoice;
    private EditText editTextNotes;
    private float data1;
    private float data2;
    private float data3;
    private String userName;
    private String notes;
    private String exerciseType;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_page);
        // Connect to the database
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userName = preferences.getString("Username", "Default User");

        // Bottom button navigation
        ImageButton home = findViewById(R.id.imageButtonHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WorkoutPageActivity.this, HomepageActivity.class));
            }
        });

        ImageButton forum = findViewById(R.id.imageButtonForum);
        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WorkoutPageActivity.this, ForumActivity.class));
            }
        });

        // Set today's date
        TextView dateTextView = findViewById(R.id.textViewDate);
        String formattedDate = getCurrentDate();
        dateTextView.setText(formattedDate);

        // Set up Spinner
        Spinner exerciseTypeSpinner = findViewById(R.id.spinnerExerciseType);
        String[] exerciseTypes = new String[]{
                "Cardiovascular Exercises",
                "Strength Training Exercises"
        };
        ArrayAdapter<String> exerciseTypeAdapter = new ArrayAdapter<>(
                getApplicationContext(), R.layout.simple_dropdown_item, exerciseTypes);
        exerciseTypeSpinner.setAdapter(exerciseTypeAdapter);
        exerciseType = exerciseTypeSpinner.getSelectedItem().toString();

        // Initialize EditText fields
        editTextWeightChoice = findViewById(R.id.editTextWeightChoice);
        editTextSetsChoice = findViewById(R.id.editTextSetsChoice);
        editTextRepsChoice = findViewById(R.id.editTextRepsChoice);
        editTextNotes = findViewById(R.id.editTextNotes);

        // Set default hints for the EditText fields
        setDefaultHints();

        // Set up listener for the exercise type selection
        exerciseTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Update hints based on the selected exercise type
                updateHintsForExerciseType(exerciseTypes[position]);
                exerciseType = exerciseTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        // Set up "Save" button
        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataForExerciseType();
                uploadDataToFirebase();
            }
        });
    }


    // Set default hints for the EditText fields
    private void setDefaultHints() {
        editTextWeightChoice.setHint("Weight");
        editTextSetsChoice.setHint("Sets");
        editTextRepsChoice.setHint("Reps");
    }

    // Update hints based on the selected exercise type
    private void updateHintsForExerciseType(String selectedExerciseType) {
        switch (selectedExerciseType) {
            case "Cardiovascular Exercises":
                editTextWeightChoice.setHint("Duration (min)");
                editTextSetsChoice.setHint("Length (km/miles)");
                editTextRepsChoice.setHint("Pace (mph/kph)");
                break;
            case "Strength Training Exercises":
                editTextWeightChoice.setHint("Weight");
                editTextSetsChoice.setHint("Sets");
                editTextRepsChoice.setHint("Reps");
                break;
            default:
                // Reset to default hints
                setDefaultHints();
        }


    }

    private void updateDataForExerciseType(){
        data1 = Float.parseFloat(editTextWeightChoice.getText().toString());
        data2 = Float.parseFloat(editTextSetsChoice.getText().toString());
        data3 = Float.parseFloat(editTextRepsChoice.getText().toString());
        notes = editTextNotes.getText().toString();
    }

    private void uploadDataToFirebase() {
        DatabaseReference userRef = usersRef.child(userName);
        userRef.child("workoutLogs").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long counter = dataSnapshot.exists() ? (long) dataSnapshot.getValue() : 0;
                counter++; // Increment the counter

                // Update the counter in the database
                userRef.child("workoutLogs").child("count").setValue(counter);

                // Create a map to represent the workout log data
                Map<String, Object> workoutData = new HashMap<>();
                workoutData.put("data1", data1);
                workoutData.put("data2", data2);
                workoutData.put("data3", data3);
                workoutData.put("notes", notes);
                workoutData.put("type", exerciseType);
                workoutData.put("date", getCurrentDate());

                // Upload the data to the workoutLogs
                userRef.child("workoutLogs").child("log" + counter).setValue(workoutData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Data successfully uploaded
                                Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                Toast.makeText(getApplicationContext(), "Failed to upload data to workout log", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
                Toast.makeText(getApplicationContext(), "Failed to update workout log counter", Toast.LENGTH_SHORT).show();
            }
        });
    }



    // Get the current date
    private String getCurrentDate() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(currentDate);
    }



}
