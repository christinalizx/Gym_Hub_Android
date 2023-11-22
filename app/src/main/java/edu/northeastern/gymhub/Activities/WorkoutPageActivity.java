package edu.northeastern.gymhub.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import edu.northeastern.gymhub.R;

public class WorkoutPageActivity extends AppCompatActivity {

    private EditText editTextWeightChoice;
    private EditText editTextSetsChoice;
    private EditText editTextRepsChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_page);

        // Bottom button navigation
        ImageButton home = findViewById(R.id.imageButtonHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WorkoutPageActivity.this, HomepageActivity.class));
            }
        });

        // Set today's date
        TextView dateTextView = findViewById(R.id.textViewDate);
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(currentDate);
        dateTextView.setText(formattedDate);

        // Set up Spinner
        Spinner exerciseTypeSpinner = findViewById(R.id.spinnerExerciseType);
        String[] exerciseTypes = new String[]{
                "Cardiovascular Exercises",
                "Strength Training Exercises",
                "Flexibility and Mobility Exercises",
                "Functional Training Exercises",
                "High-Intensity Interval Training (HIIT)",
                "CrossFit Workouts",
                "Cardiovascular Machines",
                "Core Exercises",
                "Group Fitness Classes",
                "Sport-Specific Training"
        };
        ArrayAdapter<String> exerciseTypeAdapter = new ArrayAdapter<>(
                getApplicationContext(), R.layout.simple_dropdown_item, exerciseTypes);
        exerciseTypeSpinner.setAdapter(exerciseTypeAdapter);

        // Initialize EditText fields
        editTextWeightChoice = findViewById(R.id.editTextWeightChoice);
        editTextSetsChoice = findViewById(R.id.editTextSetsChoice);
        editTextRepsChoice = findViewById(R.id.editTextRepsChoice);

        // Set default hints for the EditText fields
        setDefaultHints();

        // Set up listener for the exercise type selection
        exerciseTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Update hints based on the selected exercise type
                updateHintsForExerciseType(exerciseTypes[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
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
            case "Flexibility and Mobility Exercises":
                editTextWeightChoice.setHint("Intensity");
                editTextSetsChoice.setHint("Duration (min)");
                editTextRepsChoice.setHint("Repetitions");
                break;
            case "Functional Training Exercises":
                editTextWeightChoice.setHint("EquipWeight");
                editTextSetsChoice.setHint("Sets");
                editTextRepsChoice.setHint("Reps");
                break;
            case "High-Intensity Interval Training (HIIT)":
                editTextWeightChoice.setHint("Intensity");
                editTextSetsChoice.setHint("Rounds");
                editTextRepsChoice.setHint("Work/Rest Time");
                break;
            case "CrossFit Workouts":
                editTextWeightChoice.setHint("Weight");
                editTextSetsChoice.setHint("Rounds");
                editTextRepsChoice.setHint("Reps");
                break;
            case "Cardiovascular Machines":
                editTextWeightChoice.setHint("Resistance");
                editTextSetsChoice.setHint("Duration (min)");
                editTextRepsChoice.setHint("Intensity Level");
                break;
            case "Core Exercises":
                editTextWeightChoice.setHint("Difficulty");
                editTextSetsChoice.setHint("Duration (min)");
                editTextRepsChoice.setHint("Repetitions");
                break;
            case "Group Fitness Classes":
                editTextWeightChoice.setHint("EquipWeight");
                editTextSetsChoice.setHint("Duration (min)");
                editTextRepsChoice.setHint("Intensity Level");
                break;
            case "Sport-Specific Training":
                editTextWeightChoice.setHint("EquipWeight");
                editTextSetsChoice.setHint("Duration (min)");
                editTextRepsChoice.setHint("Repetitions");
                break;
            default:
                // Reset to default hints
                setDefaultHints();
        }
    }
}
