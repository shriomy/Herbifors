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
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    List<DeliveryOrder> deliveries = deliveryService.getDeliveries();
                    if (deliveries.isEmpty()) {
                        System.out.println("No deliveries found.");
                    } else {
                        for (DeliveryOrder order : deliveries) {
                            System.out.println(order);
                        }
                    }
                    break;

                case 2:
                    List<Order> orders = salesService.getOrders();
                    if (orders.isEmpty()) {
                        System.out.println("No sales orders found.");
                    } else {
                        for (int i = 0; i < orders.size(); i++) {
                            System.out.println((i + 1) + ". " + orders.get(i));
                        }
                    }
                    break;

                case 3:
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
                    break;
                case 4:
                    List<DeliveryOrder> allDeliveries = deliveryService.getDeliveries();
                    if (allDeliveries.isEmpty()) {
                        System.out.println("No deliveries found.");
                    } else {
                        System.out.println("Select a delivery to update status:");

                        // Display deliveries with IDs
                        for (DeliveryOrder order : allDeliveries) {
                            System.out.println("ID: " + order.getId() + " | Customer: " + order.getCustomer() + " | Item: " + order.getItem() + " | Status: " + order.getStatus());
                        }

                        System.out.print("Enter Delivery ID: ");
                        int deliveryId = scanner.nextInt();
                        scanner.nextLine();

                        // Validate ID
                        boolean validId = allDeliveries.stream().anyMatch(order -> order.getId() == deliveryId);
                        if (!validId) {
                            System.out.println("Invalid Delivery ID.");
                            break;
                        }

                        System.out.print("Enter new status (e.g., Dispatched, Delivered, Canceled): ");
                        String newStatus = scanner.nextLine();

                        deliveryService.updateDeliveryStatus(deliveryId, newStatus);
                        System.out.println("✅ Delivery status updated successfully!");
                    }
                    break;

                case 5:
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }

        // Release services
        if (deliveryRef != null) {
            context.ungetService(deliveryRef);
        }
        if (salesRef != null) {
            context.ungetService(salesRef);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Delivery Client: Stopping...");
    }
}
