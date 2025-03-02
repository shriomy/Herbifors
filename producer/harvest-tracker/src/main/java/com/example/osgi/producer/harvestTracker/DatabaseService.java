package com.example.osgi.producer.harvestTracker;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseService {

        public static void main(String[] args) {
            // Connection parameters
            String url = "jdbc:mysql://localhost:3306/harvest_db";  // Replace with your DB name
            String user = "root";  // Replace with your MySQL username
            String password = "Snfp2001*";  // Replace with your MySQL password

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

