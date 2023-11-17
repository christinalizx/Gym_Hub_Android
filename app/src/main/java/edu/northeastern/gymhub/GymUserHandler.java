package edu.northeastern.gymhub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GymUserHandler {

    private static final String URL = "jdbc:mysql://database-1.cpqkz8uyycse.us-east-1.rds.amazonaws.com:3306/gymhubdb";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "WKkn3q3YaPWNW8NthFWU";

    public List<GymUserModel> getAllGymUsers() {
        List<GymUserModel> gymUsers = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM gym_users";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    GymUserModel gymUser = new GymUserModel();
                    gymUser.setUsername(resultSet.getString("username"));
                    gymUser.setPassword(resultSet.getString("password"));
                    gymUser.setAddress(resultSet.getString("address"));
                    gymUser.setGymId(resultSet.getInt("gym_id"));

                    gymUsers.add(gymUser);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return gymUsers;
    }

    public boolean addGymUser(GymUserModel gymUser) {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "INSERT INTO gym_users (username, password, address, gym_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, gymUser.getUsername());
                statement.setString(2, gymUser.getPassword());
                statement.setString(3, gymUser.getAddress());
                statement.setInt(4, gymUser.getGymId());

                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}
