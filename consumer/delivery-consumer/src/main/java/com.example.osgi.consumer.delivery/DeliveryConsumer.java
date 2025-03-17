package com.example.osgi.consumer.delivery;

import com.example.osgi.producer.delivery.DeliveryService;
import com.example.osgi.producer.delivery.DeliveryOrder;
import com.example.osgi.producer.sales.SalesService;
import com.example.osgi.producer.sales.Order;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.List;
import java.util.Scanner;

public class DeliveryConsumer implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Delivery Client: Looking for Delivery and Sales Services...");

        // Get service references
        ServiceReference<DeliveryService> deliveryRef = context.getServiceReference(DeliveryService.class);
        ServiceReference<SalesService> salesRef = context.getServiceReference(SalesService.class);

        DeliveryService deliveryService = context.getService(deliveryRef);
        SalesService salesService = context.getService(salesRef);

        if (deliveryService == null || salesService == null) {
            System.out.println("Delivery Client: Required services not available. Exiting...");
            return;
        }

        Scanner scanner = new Scanner(System.in);
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
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            System.out.println(" ");
            switch (choice) {
                case 1:
                    viewAllDeliveries(deliveryService);
                    break;

                case 2:
                    viewAllSalesOrders(salesService);
                    break;

                case 3:
                    convertSalesOrderToDelivery(scanner, salesService, deliveryService);
                    break;

                case 4:
                    updateDeliveryStatus(scanner, deliveryService);
                    break;

                case 5:
                    viewDeliveriesByCustomer(scanner, deliveryService);
                    break;

                case 6:
                    deleteDelivery(scanner, deliveryService);
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

    private void convertSalesOrderToDelivery(Scanner scanner, SalesService salesService, DeliveryService deliveryService) {
        List<Order> salesOrders = salesService.getOrders();
        if (salesOrders.isEmpty()) {
            System.out.println("No sales orders available to convert.");
        } else {
            System.out.println("Select a sales order to convert into a delivery:");
            for (int i = 0; i < salesOrders.size(); i++) {
                System.out.println((i + 1) + ". " + salesOrders.get(i));
            }
            System.out.print("Enter the number: ");
            int orderIndex = scanner.nextInt();
            scanner.nextLine();

            if (orderIndex < 1 || orderIndex > salesOrders.size()) {
                System.out.println("Invalid selection.");
            } else {
                Order selectedOrder = salesOrders.get(orderIndex - 1);

                System.out.print("Enter delivery date (YYYY-MM-DD): ");
                String deliveryDate = scanner.nextLine();

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
    }

    private void updateDeliveryStatus(Scanner scanner, DeliveryService deliveryService) {
        List<DeliveryOrder> allDeliveries = deliveryService.getDeliveries();
        if (allDeliveries.isEmpty()) {
            System.out.println("No deliveries found.");
        } else {
            System.out.println("Select a delivery to update status:");
            for (DeliveryOrder order : allDeliveries) {
                System.out.println("ID: " + order.getId() + " | Customer: " + order.getCustomer() + " | Item: " + order.getItem() + " | Status: " + order.getStatus());
            }

            System.out.print("Enter Delivery ID: ");
            int deliveryId = scanner.nextInt();
            scanner.nextLine();

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
            System.out.print("Enter your choice: ");
            int statusChoice = scanner.nextInt();
            scanner.nextLine();

            String newStatus = "";
            switch (statusChoice) {
                case 1:
                    newStatus = "Pending";
                    break;
                case 2:
                    newStatus = "Dispatched";
                    break;
                case 3:
                    newStatus = "Delivered";
                    break;
                case 4:
                    newStatus = "Canceled";
                    break;
                default:
                    System.out.println("Invalid choice. Keeping current status.");
                    return;
            }

            deliveryService.updateDeliveryStatus(deliveryId, newStatus);
            System.out.println("✅ Delivery status updated successfully!");
        }
    }

    private void viewDeliveriesByCustomer(Scanner scanner, DeliveryService deliveryService) {
        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine();
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

    private void deleteDelivery(Scanner scanner, DeliveryService deliveryService) {
        System.out.print("Enter Delivery ID to delete: ");
        int deleteId = scanner.nextInt();
        scanner.nextLine();
        deliveryService.deleteDelivery(deleteId);
        System.out.println("✅ Delivery deleted successfully!");
        System.out.println(" ");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Delivery Client: Stopping...");
    }
}
