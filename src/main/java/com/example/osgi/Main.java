package com.example.osgi;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnection {
    public static void main(String[] args) {
        // Connection parameters
        String url = "jdbc:mysql://localhost:3306/your_database_name";  // Replace with your DB name
        String user = "your_username";  // Replace with your MySQL username
        String password = "your_password";  // Replace with your MySQL password

        try {
            // Establish connection
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful!");

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
