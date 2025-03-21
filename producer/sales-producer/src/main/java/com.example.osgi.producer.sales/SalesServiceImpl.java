package com.example.osgi.producer.sales;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalesServiceImpl implements SalesService, BundleActivator {

    private ServiceRegistration<SalesService> registration;
    private Connection connection;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("📦 Sales Producer started.");

        // Load MySQL driver explicitly
        Class.forName("com.mysql.cj.jdbc.Driver");

        // Initialize database connection
        String url = "jdbc:mysql://localhost:3306/salesdb";
        String user = "root";
        String password = "bilz123";
        connection = DriverManager.getConnection(url, user, password);
        System.out.println("✅ Database connection established successfully!");

        // Register the SalesService as an OSGi service
        registration = context.registerService(SalesService.class, this, null);
        System.out.println("✅ SalesService registered successfully!");

        // Display available service functions
        displayServiceFunctions();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("📦 Sales Producer stopped.");

        if (registration != null) {
            registration.unregister();
        }

        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("❌ Database connection closed.");
        }
    }

    @Override
    public void addOrder(Order order) {
        String query = "INSERT INTO orders (customer, item, qty, manufactured_price, selling_price, location) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, order.getCustomer());
            stmt.setString(2, order.getItem());
            stmt.setInt(3, order.getQuantity());
            stmt.setDouble(4, order.getManufacturedPrice());
            stmt.setDouble(5, order.getSellingPrice());
            stmt.setString(6, order.getLocation());
            stmt.executeUpdate();
            System.out.println("✅ Order added successfully: " + order);
        } catch (SQLException e) {
            System.out.println("❌ Error adding order: " + e.getMessage());
        }
    }

    @Override
    public List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Order order = new Order(
                        rs.getString("customer"),
                        rs.getString("item"),
                        rs.getInt("qty"),
                        rs.getDouble("manufactured_price"),
                        rs.getDouble("selling_price"),
                        rs.getString("location")
                );
                orders.add(order);
            }
            System.out.println("📄 Fetched " + orders.size() + " orders from the database.");
        } catch (SQLException e) {
            System.out.println("❌ Error fetching orders: " + e.getMessage());
        }
        return orders;
    }


    @Override
    public void updateOrder(int orderId, Order order) {
        String query = "UPDATE orders SET customer = ?, item = ?, qty = ?, manufactured_price = ?, selling_price = ?, location = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, order.getCustomer());
            stmt.setString(2, order.getItem());
            stmt.setInt(3, order.getQuantity());
            stmt.setDouble(4, order.getManufacturedPrice());
            stmt.setDouble(5, order.getSellingPrice());
            stmt.setString(6, order.getLocation());
            stmt.setInt(7, orderId);
            stmt.executeUpdate();
            System.out.println("✅ Order updated successfully: " + order);
        } catch (SQLException e) {
            System.out.println("❌ Error updating order: " + e.getMessage());
        }
    }

    @Override
    public void deleteOrder(int orderId) {
        String query = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
            System.out.println("✅ Order deleted successfully with ID: " + orderId);
        } catch (SQLException e) {
            System.out.println("❌ Error deleting order: " + e.getMessage());
        }
    }

    @Override
    public double getTotalManufacturingExpenses() {
        String query = "SELECT SUM(qty * manufactured_price) FROM orders";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error calculating total manufacturing cost: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public double getTotalSales() {
        String query = "SELECT SUM(qty * selling_price) FROM orders";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error calculating total selling cost: " + e.getMessage());
        }
        return 0;
    }

    private void displayServiceFunctions() {
        System.out.println("\n📌 Available SalesService Functions:");
        System.out.println("+-------------------------------+-------------------------------------+");
        System.out.println("| Function Name                 | Description                         |");
        System.out.println("+-------------------------------+-------------------------------------+");
        System.out.println("| addOrder                      | Adds a new order to the database    |");
        System.out.println("| getOrders                     | Retrieves all orders from database  |");
        System.out.println("| updateOrder                   | Updates an order by ID              |");
        System.out.println("| deleteOrder                   | Deletes an order by ID              |");
        System.out.println("| getTotalManufacturingExpenses | Total manufacturing expenses        |");
        System.out.println("| getRevenue                    | Generated revenue                   |");
        System.out.println("+-------------------------------+-------------------------------------+\n");
    }
}
