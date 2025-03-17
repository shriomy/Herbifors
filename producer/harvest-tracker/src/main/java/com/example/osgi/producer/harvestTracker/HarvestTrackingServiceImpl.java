// HarvestTrackingServiceImpl.java
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
    private static final String DB_URL = "jdbc:mysql://localhost:3306/harvest_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Snfp2001*";
    private Connection connection;
    private ServiceRegistration<HarvestTrackingService> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Harvest Tracker Producer started.");
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        System.out.println("Database connection established successfully!");
        registration = context.registerService(HarvestTrackingService.class, this, null);
        System.out.println("HarvestTrackingService registered successfully!");
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

    private void displayServiceFunctions() {
        System.out.println("\n Available HarvestTrackingService Functions:");
        System.out.println("+-------------------+----------------------------------------+");
        System.out.println("| Function Name     | Description                            |");
        System.out.println("+-------------------+----------------------------------------+");
        System.out.println("| addCrop          | Adds a new crop to the database        |");
        System.out.println("| getCropDetails   | Retrieves all crops from database      |");
        System.out.println("| updateCrop       | Updates a crop by ID                   |");
        System.out.println("| deleteCrop       | Deletes a crop by ID                   |");
        System.out.println("| getSortedCrops   | Get crops sorted by specified field    |");
        System.out.println("| recommendCrops   | Get crop recommendations by weather    |");
        System.out.println("| exportCropDataToCSV | Export crop data to CSV file       |");
        System.out.println("| displayCropData  | Display formatted crop inventory       |");
        System.out.println("+-------------------+----------------------------------------+\n");
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
            System.out.println("Crop added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding crop: " + e.getMessage());
        }
    }

    @Override
    public Map<Integer, Crop> getCropDetails() {
        Map<Integer, Crop> crops = new HashMap<>();
        String query = "SELECT * FROM crops";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int cropId = rs.getInt("crop_id");
                String name = rs.getString("crop_name");
                int quantity = rs.getInt("crop_qty");
                double unitPrice = rs.getDouble("crop_uprice");
                String weatherType = rs.getString("weather_type");
                crops.put(cropId, new Crop(cropId, name, quantity, unitPrice, weatherType));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching crops: " + e.getMessage());
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
            System.out.println("Crop updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error updating crop: " + e.getMessage());
        }
    }

    @Override
    public void deleteCrop(int cropId) {
        String query = "DELETE FROM crops WHERE crop_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cropId);
            stmt.executeUpdate();
            System.out.println("Crop deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error deleting crop: " + e.getMessage());
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
            System.out.println("Error getting recommendations: " + e.getMessage());
        }
        return recommendations;
    }

    @Override
    public void exportCropDataToCSV(String filePath) {
        try (PrintWriter writer = new PrintWriter(new File(filePath))) {
            StringBuilder sb = new StringBuilder();
            sb.append("CropID,Name,Quantity,Price,WeatherType\n");

            getCropDetails().values().forEach(crop -> {
                sb.append(String.format("%d,%s,%d,%.2f,%s\n",
                        crop.getCropId(),
                        crop.getName(),
                        crop.getQuantity(),
                        crop.getPrice(),
                        crop.getWeatherType()));
            });

            writer.write(sb.toString());
            System.out.println("CSV exported successfully!");
        } catch (FileNotFoundException e) {
            System.out.println("Error exporting CSV: " + e.getMessage());
        }
    }

    @Override
    public void displayCropData() {
        System.out.println("\nCurrent Crop Inventory:");
        System.out.printf("%-8s %-15s %-10s %-10s %-15s%n",
                "ID", "Name", "Quantity", "Price", "Weather");
        System.out.println("-".repeat(60));

        getCropDetails().values().forEach(crop -> {
            System.out.printf("%-8d %-15s %-10d %-10.2f %-15s%n",
                    crop.getCropId(),
                    crop.getName(),
                    crop.getQuantity(),
                    crop.getPrice(),
                    crop.getWeatherType());
        });
    }

    @Override
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}