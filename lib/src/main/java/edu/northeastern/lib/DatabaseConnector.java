package edu.northeastern.lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnector {
    public static void main(String[] args) {
        String url = "jdbc:mysql://database-1.cpqkz8uyycse.us-east-1.rds.amazonaws.com:3306/gymhubdb";
        String username = "admin";
        String password = "WKkn3q3YaPWNW8NthFWU";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            /**


            String query = "INSERT INTO gym_users (username, password, address, gym_id) " +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement1 = connection.prepareStatement(query)) {
                statement1.setString(1, "userTest");
                statement1.setString(2, "12345");
                statement1.setString(3, "1234 Main St");
                statement1.setInt(4, 1);

                statement1.execute();
        }

            ResultSet resultSet = statement.executeQuery("select * from gym_users; ");
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " "
                + resultSet.getString(3) + " "+ resultSet.getInt(4) + " ");
            }
             **/

            List<String> gymNames = new ArrayList<>();

            try {
                String query1 = "SELECT gym_name FROM gyms";
                try (PreparedStatement statement2 = connection.prepareStatement(query1);
                     ResultSet resultSet1 = statement2.executeQuery()) {

                    while (resultSet1.next()) {
                        gymNames.add(resultSet1.getString("gym_name"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println(gymNames);

            /**
            try {
                String query = "SELECT gym_id FROM gyms WHERE gym_name = ?";
                try (PreparedStatement statement3 = connection.prepareStatement(query)) {
                    statement3.setString(1, "GymName1");
                    try (ResultSet resultSet2 = statement3.executeQuery()) {
                        if (resultSet2.next()) {
                            System.out.println(resultSet2.getInt("gym_id"));
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
             **/

            connection.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }
}