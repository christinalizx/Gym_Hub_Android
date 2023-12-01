package edu.northeastern.gymhub.Models;

public class Workout {
    private String date;
    private String type;
    private float data1;
    private float data2;
    private float data3;
    private String notes;

    public float getData1() {
        return data1;
    }

    public float getData2() {
        return data2;
    }

    public float getData3() {
        return data3;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getDataInfo() {
        // Adjust this method based on your data structure
        if ("Strength Training Exercises".equals(type)) {
            return "Weight: " + data1 + ", Sets: " + data2 + ", Reps: " + data3;
        } else if ("Cardiovascular Exercises".equals(type)) {
            return "Duration: " + data1 + " min, Length: " + data2 + " km/miles, Pace: " + data3 + " mph/kph";
        } else {
            // Handle other types if needed
            return "";
        }
    }

    public String getNotes() {
        return notes;
    }
}

