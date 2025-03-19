package com.example.osgi.consumer.delivery;

import com.example.osgi.producer.delivery.DeliveryService;
import com.example.osgi.producer.delivery.DeliveryOrder;
import com.example.osgi.producer.sales.SalesService;
import com.example.osgi.producer.sales.Order;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;

public class DeliveryConsumer implements BundleActivator {
    private BufferedReader reader;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Delivery Client: Looking for Delivery and Sales Services...");

        // Initialize reader
        reader = new BufferedReader(new InputStreamReader(System.in));

        // Get service references
        ServiceReference<DeliveryService> deliveryRef = context.getServiceReference(DeliveryService.class);
        ServiceReference<SalesService> salesRef = context.getServiceReference(SalesService.class);

        DeliveryService deliveryService = context.getService(deliveryRef);
        SalesService salesService = context.getService(salesRef);

        if (deliveryService == null || salesService == null) {
            System.out.println("Delivery Client: Required services not available. Exiting...");
            return;
        }

        boolean running = true;

        while (running) {
            System.out.println("\n1. View all deliveries");
            System.out.println("2. Convert Sales Order to Delivery");
            System.out.println("3. Update Delivery Status");
            System.out.println("4. View Deliveries by Customer");
            System.out.println("5. Delete a Delivery");
            System.out.println("6. Exit");
            System.out.println(" ");

            Integer choice = getIntInput("Choose an option: ");
            if (choice == null) {
                System.out.println("⚠️ Input error detected. Returning to menu...");
                continue;
            }
            System.out.println(" ");

            switch (choice) {
                case 1:
                    viewAllDeliveries(deliveryService);
                    break;

                case 2:
                    convertSalesOrderToDelivery(salesService, deliveryService);
                    break;

                case 3:
                    updateDeliveryStatus(deliveryService);
                    break;

                case 4:
                    viewDeliveriesByCustomer(deliveryService);
                    break;

                case 5:
                    deleteDelivery(deliveryService);
                    break;

                case 6:
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }
    }
    private void viewAllDeliveries(DeliveryService deliveryService) {
        List<DeliveryOrder> deliveries = deliveryService.getDeliveries();
        if (deliveries.isEmpty()) {
            System.out.println("🚚 No deliveries found.");
        } else {
            System.out.println("\n📦 All Deliveries:");
            System.out.printf("%-5s | %-15s | %-30s | %-20s | %-12s | %-10s\n",
                    "ID", "Customer", "Address", "Item", "Date", "Status");
            System.out.println("-------------------------------------------------------------------------------------------------------");

            for (DeliveryOrder order : deliveries) {
                System.out.printf("%-5d | %-15s | %-30s | %-20s | %-12s | %-10s\n",
                        order.getId(), order.getCustomer(), order.getAddress(),
                        order.getItem(), order.getDeliveryDate(), order.getStatus());
            }
        }
    }



    private void convertSalesOrderToDelivery(SalesService salesService, DeliveryService deliveryService) {
        List<Order> salesOrders = salesService.getOrders();
        if (salesOrders.isEmpty()) {
            System.out.println("No sales orders available to convert.");
            return;
        }

        System.out.println("\n📋 Sales Orders:");
        System.out.printf("%-4s %-15s %-10s %-5s %-15s %-10s\n",
                "ID", "Customer", "Item", "Qty", "Selling Price", "Location");
        System.out.println("--------------------------------------------------------------");

        for (int i = 0; i < salesOrders.size(); i++) {
            Order order = salesOrders.get(i);
            System.out.printf("%-4d %-15s %-10s %-5d %-15.2f %-10s\n",
                    (i + 1), order.getCustomer(), order.getItem(),
                    order.getQuantity(), order.getSellingPrice(), order.getLocation());
        }
        System.out.println(" ");

        Integer orderIndex = getIntInput("Enter the number of the order to convert: ");
        if (orderIndex == null || orderIndex < 1 || orderIndex > salesOrders.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Order selectedOrder = salesOrders.get(orderIndex - 1);
        String deliveryDate = getStringInput("Enter delivery date (YYYY-MM-DD): ");

        DeliveryOrder newDelivery = new DeliveryOrder(
                selectedOrder.getCustomer(),
                selectedOrder.getLocation(),
                selectedOrder.getItem(),
                deliveryDate,
                "Pending"
        );

        deliveryService.addDelivery(newDelivery);
        System.out.println("✅ Delivery created successfully: " + newDelivery);
    }


    private void updateDeliveryStatus(DeliveryService deliveryService) {
        List<DeliveryOrder> allDeliveries = deliveryService.getDeliveries();
        if (allDeliveries.isEmpty()) {
            System.out.println("No deliveries found.");
            return;
        }

        System.out.println("\n📦 Available Deliveries:");
        System.out.printf("%-5s | %-15s | %-20s | %-12s\n", "ID", "Customer", "Item", "Status");
        System.out.println("--------------------------------------------------------");
        for (DeliveryOrder order : allDeliveries) {
            System.out.printf("%-5d | %-15s | %-20s | %-12s\n",
                    order.getId(), order.getCustomer(), order.getItem(), order.getStatus());
        }
        System.out.println(" ");

        Integer deliveryId = getIntInput("Enter Delivery ID to update: ");
        if (deliveryId == null) {
            return;
        }

        boolean validId = allDeliveries.stream().anyMatch(order -> order.getId() == deliveryId);
        if (!validId) {
            System.out.println("❌ Invalid Delivery ID.");
            return;
        }

        System.out.println("\n📌 Select a new status:");
        System.out.println("1. Pending");
        System.out.println("2. Dispatched");
        System.out.println("3. Delivered");
        System.out.println("4. Canceled");

        Integer statusChoice = getIntInput("Enter your choice: ");
        if (statusChoice == null) {
            return;
        }

        String newStatus = switch (statusChoice) {
            case 1 -> "Pending";
            case 2 -> "Dispatched";
            case 3 -> "Delivered";
            case 4 -> "Canceled";
            default -> {
                System.out.println("❌ Invalid choice. Keeping current status.");
                yield null;
            }
        };

        if (newStatus != null) {
            deliveryService.updateDeliveryStatus(deliveryId, newStatus);
            System.out.println("✅ Delivery status updated successfully!");
        }
    }


    private void viewDeliveriesByCustomer(DeliveryService deliveryService) {
        String customerName = getStringInput("🔍 Enter customer name: ");

        List<DeliveryOrder> customerDeliveries = deliveryService.getDeliveriesByCustomer(customerName);
        if (customerDeliveries.isEmpty()) {
            System.out.println("❌ No deliveries found for customer: " + customerName);
            return;
        }

        System.out.println("\n📦 Deliveries for " + customerName + ":");
        System.out.printf("%-5s | %-30s | %-20s | %-12s | %-10s\n",
                "ID", "Address", "Item", "Date", "Status");
        System.out.println("-------------------------------------------------------------------------------------");

        for (DeliveryOrder order : customerDeliveries) {
            System.out.printf("%-5d | %-30s | %-20s | %-12s | %-10s\n",
                    order.getId(), order.getAddress(), order.getItem(), order.getDeliveryDate(), order.getStatus());
        }
    }


    private void deleteDelivery(DeliveryService deliveryService) {
        Integer deleteId = getIntInput("Enter Delivery ID to delete: ");
        if (deleteId == null) {
            return;
        }
        deliveryService.deleteDelivery(deleteId);
        System.out.println("✅ Delivery deleted successfully!");
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        try {
            String input = reader.readLine();
            return (input == null || input.isEmpty()) ? "N/A" : input.trim();
        } catch (IOException e) {
            System.out.println("⚠️ Error reading input: " + e.getMessage());
            return "N/A";
        }
    }

    private Integer getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = reader.readLine();
                if (input == null) {
                    System.out.println("⚠️ Input error detected.");
                    return null;
                }
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException | IOException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Delivery Client: Stopping...");
        reader = null; // Don't close System.in, just clear reference
    }
}