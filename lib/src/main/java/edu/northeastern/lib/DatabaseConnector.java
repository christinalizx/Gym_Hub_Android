package edu.northeastern.lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnector {
    public static void main(String[] args) {
        String url = "jdbc:mysql://database-1.cpqkz8uyycse.us-east-1.rds.amazonaws.com:3306/gymhubdb";
        String username = "admin";
        String password = "WKkn3q3YaPWNW8NthFWU";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select * from gym_users; ");
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " "
                + resultSet.getString(3) + " "+ resultSet.getInt(4) + " ");
            }


            connection.close();
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }
}