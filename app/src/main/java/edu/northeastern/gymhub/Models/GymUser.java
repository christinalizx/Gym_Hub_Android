package edu.northeastern.gymhub.Models;

import java.util.ArrayList;
import java.util.List;

public class GymUser {
    private String username;
    private String password;
    private String address;
    private int gymId;
    private List<Workout> workoutLog;

    /** Default constructor
     */
    public GymUser(){};

    /**
     * Constructor with params.
     */
    public GymUser(String username, String password, String address, int gymId){
        this.username = username;
        this.password = password;
        this.address = address;
        this.gymId = gymId;
        this.workoutLog = new ArrayList<>();
    }

    public void addWorkout(Workout workout){
        this.workoutLog.add(workout);
    }

    public List<Workout> getWorkoutLog(){return this.workoutLog;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getGymId() {
        return gymId;
    }

    public void setGymId(int gymId) {
        this.gymId = gymId;
    }
}