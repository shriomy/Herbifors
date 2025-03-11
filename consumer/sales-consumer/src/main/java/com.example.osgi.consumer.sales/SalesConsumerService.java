package com.example.osgi.consumer.sales;

import com.example.osgi.producer.sales.Order;
import com.example.osgi.producer.sales.SalesService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

public class SalesConsumerService implements BundleActivator {

    private ServiceReference<SalesService> salesServiceReference;
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void start(BundleContext context) {
        System.out.println("📦 Sales Consumer started.");

        // Fetch the SalesService
        salesServiceReference = context.getServiceReference(SalesService.class);
        if (salesServiceReference != null) {
            SalesService salesService = context.getService(salesServiceReference);
            if (salesService != null) {
                displayMenu(salesService);
            } else {
                System.out.println("❌ Failed to retrieve SalesService.");
            }
        } else {
            System.out.println("❌ SalesService is not available.");
        }
    }

    @Override
    public void stop(BundleContext context) {
        System.out.println("📦 Sales Consumer stopped.");
        if (salesServiceReference != null) {
            context.ungetService(salesServiceReference);
        }
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("❌ Error closing reader: " + e.getMessage());
        }
    }

    private void displayMenu(SalesService salesService) {
        while (true) {
            System.out.println("\n📌 Sales Consumer Menu:");
            System.out.println("1️. Add a new order");
            System.out.println("2️. View all orders");
            System.out.println("3️. Exit");
            int choice = getIntInput("➡️ Choose an option: ");
            switch (choice) {
                case 1:
                    addOrderThroughConsumer(salesService);
                    break;
                case 2:
                    fetchOrders(salesService);
                    break;
                case 3:
                    System.out.println("👋 Exiting Sales Consumer...");
                    return;
                default:
                    System.out.println("❌ Invalid option. Please try again.");
            }
        }
    }

    private void addOrderThroughConsumer(SalesService salesService) {
        System.out.println("\n📝 Enter order details:");

        String customer = getStringInput("Enter customer name: ");
        String item = getStringInput("Enter item name: ");
        int quantity = getIntInput("Enter quantity: ");
        double manufacturedPrice = getDoubleInput("Enter manufactured price: ");
        double sellingPrice = getDoubleInput("Enter selling price: ");
        String location = getStringInput("Enter location: ");

        Order order = new Order(customer, item, quantity, manufacturedPrice, sellingPrice, location);
        salesService.addOrder(order);
        System.out.println("✅ Order added successfully!");
    }

    private void fetchOrders(SalesService salesService) {
        System.out.println("\n📄 Fetching all orders from database...");
        List<Order> orders = salesService.getOrders();
        if (orders.isEmpty()) {
            System.out.println("⚠️ No orders found.");
        } else {
            System.out.println("\n📋 Order List:");
            System.out.println("+------------+--------+------+-----------------+--------------+------------+");
            System.out.println("| Customer   | Item   | Qty  | Manuf. Price    | Selling Price | Location  |");
            System.out.println("+------------+--------+------+-----------------+--------------+------------+");
            for (Order order : orders) {
                System.out.printf("| %-10s | %-6s | %-4d | %-15.2f | %-12.2f | %-10s |\n",
                        order.getCustomer(), order.getItem(), order.getQuantity(),
                        order.getManufacturedPrice(), order.getSellingPrice(), order.getLocation());
            }
            System.out.println("+------------+--------+------+-----------------+--------------+------------+");
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("❌ Error reading input. Try again.");
            return getStringInput(prompt);
        }
    }

    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(reader.readLine().trim());
            } catch (IOException | NumberFormatException e) {
                System.out.println("❌ Invalid number. Try again.");
            }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(reader.readLine().trim());
            } catch (IOException | NumberFormatException e) {
                System.out.println("❌ Invalid number. Try again.");
            }
        }
    }
}
