package com.example.osgi.producer.harvestTracker;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

public class HarvestTrackingServiceImpl implements HarvestTrackingService, BundleActivator {

    private ServiceRegistration<HarvestTrackingService> registration;
    private Connection connection;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Harvest Tracker Producer started.");

        // Load MySQL driver explicitly
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Initialize database connection
        String url = "jdbc:mysql://localhost:3306/harvest_db";
        String user = "root";
        String password = "Snfp2001*";
        connection = DriverManager.getConnection(url, user, password);
        System.out.println(" Database connection established successfully!");

        // Register the HarvestTrackingService as an OSGi service
        registration = context.registerService(HarvestTrackingService.class, this, null);
        System.out.println(" HarvestTrackingService registered successfully!");

        // Display available service functions
        displayServiceFunctions();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Harvest Tracker Producer stopped.");

        if (registration != null) {
            registration.unregister();
        }

        closeConnection();
    }

    @Override
    public void addCrop(int cropId, String name, int quantity, double price, String weatherType) {
        String query = "INSERT INTO crops (crop_id, crop_name, crop_qty, crop_uprice, weather_type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cropId);
            stmt.setString(2, name);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, price);
            stmt.setString(5, weatherType);
            stmt.executeUpdate();
            System.out.println(" Crop added successfully: " + name);
        } catch (SQLException e) {
            System.out.println(" Error adding crop: " + e.getMessage());
        }
    }

    @Override
    public Map<Integer, Crop> getCropDetails() {
        Map<Integer, Crop> crops = new HashMap<>();
        String query = "SELECT * FROM crops";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Crop crop = new Crop(
                        rs.getInt("crop_id"),
                        rs.getString("crop_name"),
                        rs.getInt("crop_qty"),
                        rs.getDouble("crop_uprice"),
                        rs.getString("weather_type")
                );
                crops.put(crop.getCropId(), crop);
            }
            System.out.println(" Fetched " + crops.size() + " crops from the database.");
        } catch (SQLException e) {
            System.out.println(" Error fetching crops: " + e.getMessage());
        }
        return crops;
    }

    @Override
    public void updateCrop(int cropId, int quantity, double price, String weatherType) {
        String query = "UPDATE crops SET crop_qty = ?, crop_uprice = ?, weather_type = ? WHERE crop_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setDouble(2, price);
            stmt.setString(3, weatherType);
            stmt.setInt(4, cropId);
            stmt.executeUpdate();
            System.out.println(" Crop updated successfully: " + cropId);
        } catch (SQLException e) {
            System.out.println(" Error updating crop: " + e.getMessage());
        }
    }

    @Override
    public void deleteCrop(int cropId) {
        String query = "DELETE FROM crops WHERE crop_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cropId);
            stmt.executeUpdate();
            System.out.println(" Crop deleted successfully with ID: " + cropId);
        } catch (SQLException e) {
            System.out.println(" Error deleting crop: " + e.getMessage());
        }
    }

    @Override
    public List<Crop> getSortedCrops(String sortBy, boolean ascending) {
        List<Crop> crops = new ArrayList<>(getCropDetails().values());
        Comparator<Crop> comparator = switch (sortBy.toLowerCase()) {
            case "name" -> Comparator.comparing(Crop::getName);
            case "price" -> Comparator.comparing(Crop::getPrice);
            case "quantity" -> Comparator.comparing(Crop::getQuantity);
            default -> Comparator.comparing(Crop::getCropId);
        };
        crops.sort(ascending ? comparator : comparator.reversed());
        return crops;
    }

    @Override
    public List<String> recommendCrops(String currentWeather) {
        List<String> recommendations = new ArrayList<>();
        String query = "SELECT crop_name FROM crops WHERE weather_type = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, currentWeather);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                recommendations.add(rs.getString("crop_name"));
            }
        } catch (SQLException e) {
            System.out.println(" Error getting recommendations: " + e.getMessage());
        }
        return recommendations;
    }

    @Override
    public void exportCropDataToCSV(String filePath) {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            writer.println("CropID,Name,Quantity,Price,WeatherType");
            for (Crop crop : getCropDetails().values()) {
                writer.printf("%d,%s,%d,%.2f,%s\n", crop.getCropId(), crop.getName(), crop.getQuantity(), crop.getPrice(), crop.getWeatherType());
            }
            System.out.println(" CSV exported successfully to " + filePath);
        } catch (FileNotFoundException e) {
            System.out.println(" Error exporting CSV: " + e.getMessage());
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(" Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println(" Error closing connection: " + e.getMessage());
        }
    }

    private void displayServiceFunctions() {
        System.out.println("\n Available HarvestTrackingService Functions:");
        System.out.println("+---------------------------+-------------------------------------------+");
        System.out.println("| Function Name             | Description                               |");
        System.out.println("+---------------------------+-------------------------------------------+");
        System.out.println("| addCrop                   | Adds a new crop to the database           |");
        System.out.println("| getCropDetails            | Retrieves all crop details                |");
        System.out.println("| updateCrop                | Updates an existing crop by ID            |");
        System.out.println("| deleteCrop                | Deletes a crop by ID                      |");
        System.out.println("| getSortedCrops            | Retrieves crops sorted by a given field   |");
        System.out.println("| recommendCrops            | Recommends crops based on weather         |");
        System.out.println("| exportCropDataToCSV       | Exports crop data to a CSV file           |");
        System.out.println("| displayCropData           | Displays formatted crop inventory         |");
        System.out.println("| getTotalCropValue         | Calculates total crop market value        |");
        System.out.println("| closeConnection           | Closes the database connection            |");
        System.out.println("+---------------------------+-------------------------------------------+\n");
    }

}
