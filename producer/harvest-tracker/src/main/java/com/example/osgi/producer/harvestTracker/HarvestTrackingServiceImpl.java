package com.example.osgi.producer.harvestTracker;

import com.example.osgi.producer.harvestTracker.HarvestTrackingService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class HarvestTrackingServiceImpl implements HarvestTrackingService, BundleActivator {
    private Connection connection;
    private ServiceRegistration<?> registration;

    @Override
    public void start(BundleContext context) {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/harvest_db?useSSL=false&serverTimezone=UTC",
                    "root",
                    "Snfp2001*"
            );
            registration = context.registerService(HarvestTrackingService.class, this, null);
            System.out.println("✅ HarvestTrackingService started and connected to DB.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop(BundleContext context) {
        try {
            if (connection != null) connection.close();
            if (registration != null) registration.unregister();
            System.out.println("🛑 HarvestTrackingService stopped and DB connection closed.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addCrop(String name, int quantity, double price, String weatherType, int cropID) {
        String query = "INSERT INTO crops (crop_id, name, quantity, price, weather_type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cropID);
            stmt.setString(2, name);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            stmt.setString(5, weatherType);
            stmt.executeUpdate();
            System.out.println("🌱 Crop added: " + name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCrop(int cropID, int quantity, double price, String weatherType) {
        String query = "UPDATE crops SET quantity = ?, price = ?, weather_type = ? WHERE crop_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setDouble(2, price);
            stmt.setString(3, weatherType);
            stmt.setInt(4, cropID);
            stmt.executeUpdate();
            System.out.println("🔄 Crop updated: ID " + cropID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCrop(int cropID) {
        String query = "DELETE FROM crops WHERE crop_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cropID);
            stmt.executeUpdate();
            System.out.println("❌ Crop deleted: ID " + cropID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<Integer, String> getCropDetails() {
        Map<Integer, String> crops = new HashMap<>();
        String query = "SELECT * FROM crops";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("crop_id");
                String details = rs.getString("name") + ", " + rs.getInt("quantity") + "kg, $" + rs.getDouble("price") + ", " + rs.getString("weather_type");
                crops.put(id, details);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crops;
    }

    @Override
    public Map<Integer, String> getSortedCrops(String sortBy, boolean ascending) {
        Map<Integer, String> crops = new HashMap<>();
        String order = ascending ? "ASC" : "DESC";
        String query = "SELECT * FROM crops ORDER BY " + sortBy + " " + order;

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("crop_id");
                String details = rs.getString("name") + ", " + rs.getInt("quantity") + "kg, $" + rs.getDouble("price");
                crops.put(id, details);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return crops;
    }

    @Override
    public List<String> recommendCrops(String currentWeather) {
        List<String> recommendedCrops = new ArrayList<>();
        String query = "SELECT name FROM crops WHERE weather_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, currentWeather);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                recommendedCrops.add("✅ " + rs.getString("name") + " grows well in " + currentWeather);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recommendedCrops;
    }

    @Override
    public void exportCropDataToCSV(String filePath) {
        String query = "SELECT * FROM crops";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query);
             BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            writer.write("Crop ID, Name, Quantity, Price, Weather Type\n");
            while (rs.next()) {
                writer.write(rs.getInt("crop_id") + "," + rs.getString("name") + ","
                        + rs.getInt("quantity") + "," + rs.getDouble("price") + ","
                        + rs.getString("weather_type") + "\n");
            }
            System.out.println("✅ Crop data exported to " + filePath);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}