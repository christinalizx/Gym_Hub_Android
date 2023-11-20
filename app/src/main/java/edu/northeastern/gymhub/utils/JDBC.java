package edu.northeastern.gymhub.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JDBC {

    private static final String URL = "jdbc:mysql://database-1.cpqkz8uyycse.us-east-1.rds.amazonaws.com:3306/gymhubdb";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "WKkn3q3YaPWNW8NthFWU";
    private static JDBC instance;
    private Connection connection;

    /** Private constructor to prevent instantiation outside the class **/
    private JDBC() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    /** Static method to get the singleton instance **/
    public static JDBC getInstance() {
        if (instance == null) {
            instance = new JDBC();
        }
        return instance;
    }

    public List<GymUser> getAllGymUsers() {
        List<GymUser> gymUsers = new ArrayList<>();

        try {
            String query = "SELECT * FROM gym_users";
            try (PreparedStatement statement = instance.connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    GymUser gymUser = new GymUser();
                    gymUser.setUsername(resultSet.getString("username"));
                    gymUser.setPassword(resultSet.getString("password"));
                    gymUser.setAddress(resultSet.getString("address"));
                    gymUser.setGymId(resultSet.getInt("gym_id"));

                    gymUsers.add(gymUser);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gymUsers;
    }

    /** Adds a gym_user to the database **/
    public boolean addGymUser(GymUser gymUser) {
        try {
            String query = "INSERT INTO gym_users (username, password, address, gym_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = instance.connection.prepareStatement(query)) {
                statement.setString(1, gymUser.getUsername());
                statement.setString(2, gymUser.getPassword());
                statement.setString(3, gymUser.getAddress());
                statement.setInt(4, gymUser.getGymId());

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /** Gets gym_id based on gym_name **/
    public int getGymIdFromName(String gymName) {
        try {
            String query = "SELECT gym_id FROM gyms WHERE gym_name = ?";
            try (PreparedStatement statement = instance.connection.prepareStatement(query)) {
                statement.setString(1, gymName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("gym_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if gym ID is not found (you may want to handle this case appropriately)
    }

    /** Closes connection to the database **/
    public void closeConnection() {
        try {
            if (instance != null && instance.connection != null && !instance.connection.isClosed()) {
                instance.connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}