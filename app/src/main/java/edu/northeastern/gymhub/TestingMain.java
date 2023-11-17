package edu.northeastern.gymhub;

import edu.northeastern.gymhub.utils.GymUser;
import edu.northeastern.gymhub.utils.JDBC;

public class TestingMain {
    public static void main(String[] args) {
        // Create a GymUserModel
        GymUser gymUser = new GymUser("userMP", "123", "123", 1);

        // Initialize GymUserHandler (assuming your credentials are correct)
        JDBC gymUserHandler = JDBC.getInstance();

        // Add the GymUser using addGymUser method
        boolean addedSuccessfully = gymUserHandler.addGymUser(gymUser);

        if (addedSuccessfully) {
            System.out.println("GymUser added successfully!");
        } else {
            System.out.println("Failed to add GymUser.");
        }

        // Close the connection when done (optional)
        gymUserHandler.closeConnection();
    }
}
