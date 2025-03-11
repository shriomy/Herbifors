
package com.example.osgi.producer.delivery;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeliveryServiceImpl implements DeliveryService, BundleActivator {
    private ServiceRegistration<DeliveryService> registration;
    private Connection connection;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Delivery Producer started.");

        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/delivery", "root", "root");
        System.out.println("✅ Database connection established successfully!");

        registration = context.registerService(DeliveryService.class, this, null);
        System.out.println("✅ DeliveryService registered successfully!");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Delivery Producer stopped.");
        if (registration != null) registration.unregister();
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("❌ Database connection closed.");
        }
    }

    @Override
    public void addDelivery(DeliveryOrder order) {
        String query = "INSERT INTO deliveries (recipient, address, item, qty, delivery_date, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, order.getRecipient());
            stmt.setString(2, order.getAddress());
            stmt.setString(3, order.getItem());
            stmt.setInt(4, order.getQuantity());
            stmt.setString(5, order.getDeliveryDate());
            stmt.setString(6, order.getStatus());
            stmt.executeUpdate();
            System.out.println("✅ Delivery added successfully: " + order);
        } catch (SQLException e) {
            System.out.println("❌ Error adding delivery: " + e.getMessage());
        }
    }

    @Override
    public List<DeliveryOrder> getDeliveries() {
        List<DeliveryOrder> deliveries = new ArrayList<>();
        String query = "SELECT * FROM deliveries";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                DeliveryOrder order = new DeliveryOrder(
                        rs.getString("recipient"),
                        rs.getString("address"),
                        rs.getString("item"),
                        rs.getInt("qty"),
                        rs.getString("delivery_date"),
                        rs.getString("status")
                );
                deliveries.add(order);
            }
            System.out.println("📄 Fetched " + deliveries.size() + " deliveries from the database.");
        } catch (SQLException e) {
            System.out.println("❌ Error fetching deliveries: " + e.getMessage());
        }
        return deliveries;
    }
}
