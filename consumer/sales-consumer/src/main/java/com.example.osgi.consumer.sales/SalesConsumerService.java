package com.example.osgi.consumer.sales;

import com.example.osgi.producer.sales.Order;
import com.example.osgi.producer.sales.SalesService;
import com.example.osgi.producer.harvestTracker.HarvestTrackingService;
import com.example.osgi.producer.harvestTracker.Crop;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SalesConsumerService implements BundleActivator {

    private ServiceReference<SalesService> salesServiceReference;
    private ServiceReference<HarvestTrackingService> harvestServiceReference;
    private BufferedReader reader;

    @Override
    public void start(BundleContext context) {
        System.out.println("📦 Sales Consumer started.");
        reader = new BufferedReader(new InputStreamReader(System.in));

        // Fetch HarvestTrackingService and display crop details
        harvestServiceReference = context.getServiceReference(HarvestTrackingService.class);
        if (harvestServiceReference != null) {
            HarvestTrackingService harvestService = context.getService(harvestServiceReference);
            if (harvestService != null) {
                displayCropDetails(harvestService);
            } else {
                System.out.println("❌ Failed to retrieve HarvestTrackingService.");
            }
        } else {
            System.out.println("❌ HarvestTrackingService is not available.");
        }

        // Fetch SalesService
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
        if (harvestServiceReference != null) {
            context.ungetService(harvestServiceReference);
        }
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            System.out.println("❌ Error closing reader: " + e.getMessage());
        }
    }

    private void displayMenu(SalesService salesService) {
        while (true) {
            System.out.println("\n📌 Sales Consumer Menu:");
            System.out.println("1️. Add a new order");
            System.out.println("2️. View all orders");
            System.out.println("3️. Update an order");
            System.out.println("4️. Delete an order");
            System.out.println("5️. Exit");

            int choice = getIntInput("➡️ Choose an option: ");
            switch (choice) {
                case 1:
                    addOrderThroughConsumer(salesService);
                    break;
                case 2:
                    fetchOrders(salesService);
                    break;
                case 3:
                    updateOrder(salesService);
                    break;
                case 4:
                    deleteOrder(salesService);
                    break;
                case 5:
                    System.out.println("👋 Exiting Sales Consumer...");
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        System.out.println("❌ Error closing reader: " + e.getMessage());
                    }
                    return;
                default:
                    System.out.println("❌ Invalid option. Please try again.");
            }
        }
    }

    private void displayCropDetails(HarvestTrackingService harvestService) {
        System.out.println("\n📊 Available Crops from Harvest Tracker:");
        Map<Integer, Crop> crops = harvestService.getCropDetails();

        if (crops == null || crops.isEmpty()) {
            System.out.println("⚠️ No crops found in the database.");
            return;
        }

        System.out.println("+--------+------------------+----------+------------+------------------+");
        System.out.println("| Crop ID| Name             | Quantity | Price      | Weather Type     |");
        System.out.println("+--------+------------------+----------+------------+------------------+");
        for (Crop crop : crops.values()) {
            System.out.printf("| %-6d | %-16s | %-8d | $%-9.2f | %-16s |\n",
                    crop.getCropId(), crop.getName(), crop.getQuantity(),
                    crop.getPrice(), crop.getWeatherType());
        }
        System.out.println("+--------+------------------+----------+------------+------------------+");
    }

    private void fetchOrders(SalesService salesService) {
        System.out.println("\n📄 Fetching all orders from database...");
        try {
            List<Order> orders = salesService.getOrders();
            if (orders == null || orders.isEmpty()) {
                System.out.println("⚠️ No orders found.");
                return;
            }

            System.out.println("+--------+------------+--------+------+-----------------+--------------+------------+");
            System.out.println("| OrderID| Customer   | Item   | Qty  | Manuf. Price    | Selling Price | Location  |");
            System.out.println("+--------+------------+--------+------+-----------------+--------------+------------+");

            for (Order order : orders) {
                if (order != null) {
                    System.out.printf("| %-6d | %-10s | %-6s | %-4d | %-15.2f | %-12.2f | %-10s |\n",
                            order.getId(), order.getCustomer(), order.getItem(), order.getQuantity(),
                            order.getManufacturedPrice(), order.getSellingPrice(), order.getLocation());
                } else {
                    System.out.println("⚠️ Skipping a null order entry.");
                }
            }
            System.out.println("+--------+------------+--------+------+-----------------+--------------+------------+");
        } catch (Exception e) {
            System.out.println("❌ Error fetching orders: " + e.getMessage());
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

    private void updateOrder(SalesService salesService) {
        System.out.println("\n✏️ Update an order:");
        int orderId = getIntInput("Enter order ID to update: ");
        String customer = getStringInput("Enter new customer name: ");
        String item = getStringInput("Enter new item name: ");
        int quantity = getIntInput("Enter new quantity: ");
        double manufacturedPrice = getDoubleInput("Enter new manufactured price: ");
        double sellingPrice = getDoubleInput("Enter new selling price: ");
        String location = getStringInput("Enter new location: ");

        Order updatedOrder = new Order(customer, item, quantity, manufacturedPrice, sellingPrice, location);
        salesService.updateOrder(orderId, updatedOrder);
        System.out.println("✅ Order updated successfully!");
    }

    private void deleteOrder(SalesService salesService) {
        System.out.println("\n🗑️ Delete an order:");
        int orderId = getIntInput("Enter order ID to delete: ");
        salesService.deleteOrder(orderId);
        System.out.println("✅ Order deleted successfully!");
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        try {
            return reader.readLine().trim();
        } catch (IOException e) {
            System.out.println("❌ Error reading input. Try again.");
            return getStringInput(prompt);
        }
    }

    private int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = reader.readLine();
                if (input == null) {
                    System.out.println("\n❌ Input stream closed. Exiting...");
                    System.exit(1);
                }
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number. Please enter a valid number.");
            } catch (IOException e) {
                System.out.println("❌ Error reading input. Exiting...");
                System.exit(1);
            }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = reader.readLine();
                if (input == null) {
                    System.out.println("\n❌ Input stream closed. Exiting...");
                    System.exit(1);
                }
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid number. Please enter a valid number.");
            } catch (IOException e) {
                System.out.println("❌ Error reading input. Exiting...");
                System.exit(1);
            }
        }
    }

}
