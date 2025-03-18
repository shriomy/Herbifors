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
            System.out.println("2. View all sales orders");
            System.out.println("3. Convert Sales Order to Delivery");
            System.out.println("4. Update Delivery Status");
            System.out.println("5. View Deliveries by Customer");
            System.out.println("6. Delete a Delivery");
            System.out.println("7. Exit");
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
                    viewAllSalesOrders(salesService);
                    break;

                case 3:
                    convertSalesOrderToDelivery(salesService, deliveryService);
                    break;

                case 4:
                    updateDeliveryStatus(deliveryService);
                    break;

                case 5:
                    viewDeliveriesByCustomer(deliveryService);
                    break;

                case 6:
                    deleteDelivery(deliveryService);
                    break;

                case 7:
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
            System.out.println("No deliveries found.");
        } else {
            System.out.println("Deliveries:");
            for (DeliveryOrder order : deliveries) {
                System.out.println("ID: " + order.getId());
                System.out.println("Customer: " + order.getCustomer());
                System.out.println("Address: " + order.getAddress());
                System.out.println("Item: " + order.getItem());
                System.out.println("Date: " + order.getDeliveryDate());
                System.out.println("Status: " + order.getStatus());
                System.out.println("------------------------------------------------------------");
            }
        }
    }

    private void viewAllSalesOrders(SalesService salesService) {
        List<Order> orders = salesService.getOrders();
        if (orders.isEmpty()) {
            System.out.println("No sales orders found.");
        } else {
            System.out.println("Sales Orders:");
            for (Order order : orders) {
                System.out.println("ID: " + order.getId());
                System.out.println("Customer: " + order.getCustomer());
                System.out.println("Item: " + order.getItem());
                System.out.println("Quantity: " + order.getQuantity());
                System.out.println("Location: " + order.getLocation());
                System.out.println("Selling Price: " + order.getSellingPrice());
                System.out.println("------------------------------------------------------------");
            }
        }
    }

    private void convertSalesOrderToDelivery(SalesService salesService, DeliveryService deliveryService) {
        List<Order> salesOrders = salesService.getOrders();
        if (salesOrders.isEmpty()) {
            System.out.println("No sales orders available to convert.");
        } else {
            System.out.println("Select a sales order to convert into a delivery:");
            for (int i = 0; i < salesOrders.size(); i++) {
                System.out.println((i + 1) + ". " + salesOrders.get(i));
            }

            Integer orderIndex = getIntInput("Enter the number: ");
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
    }

    private void updateDeliveryStatus(DeliveryService deliveryService) {
        List<DeliveryOrder> allDeliveries = deliveryService.getDeliveries();
        if (allDeliveries.isEmpty()) {
            System.out.println("No deliveries found.");
            return;
        }

        Integer deliveryId = getIntInput("Enter Delivery ID: ");
        if (deliveryId == null) {
            return;
        }

        boolean validId = allDeliveries.stream().anyMatch(order -> order.getId() == deliveryId);
        if (!validId) {
            System.out.println("Invalid Delivery ID.");
            return;
        }

        System.out.println("Select a new status:");
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
                System.out.println("Invalid choice. Keeping current status.");
                yield null;
            }
        };

        if (newStatus != null) {
            deliveryService.updateDeliveryStatus(deliveryId, newStatus);
            System.out.println("✅ Delivery status updated successfully!");
        }
    }

    private void viewDeliveriesByCustomer(DeliveryService deliveryService) {
        String customerName = getStringInput("Enter customer name: ");

        List<DeliveryOrder> customerDeliveries = deliveryService.getDeliveriesByCustomer(customerName);
        if (customerDeliveries.isEmpty()) {
            System.out.println("No deliveries found for customer: " + customerName);
        } else {
            System.out.println("Deliveries for " + customerName + ":");
            for (DeliveryOrder order : customerDeliveries) {
                System.out.println("ID: " + order.getId());
                System.out.println("Address: " + order.getAddress());
                System.out.println("Item: " + order.getItem());
                System.out.println("Date: " + order.getDeliveryDate());
                System.out.println("Status: " + order.getStatus());
                System.out.println("------------------------------------------------------------");
            }
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