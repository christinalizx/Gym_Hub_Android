package edu.northeastern.gymhub.Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Workout {
    private int duration;
    private List<Exercise> exercises;
    private LocalDateTime datetime;

    public Workout(){
        this.exercises = new ArrayList<>();
    }

    public int getDuration() {
        return duration;
    }

    public List<Exercise> getExercises() {
        return new ArrayList<>(exercises);
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public void addExercise(Exercise exercise) {
        this.exercises.add(exercise);
    }
}
