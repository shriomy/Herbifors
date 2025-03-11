package com.example.osgi.consumer.delivery;

import com.example.osgi.producer.delivery.DeliveryService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.List;
import java.util.Scanner;

public class DeliveryConsumer implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Delivery Client: Looking for Delivery Service...");

        // Get the DeliveryService reference
        ServiceReference<DeliveryService> serviceRef = context.getServiceReference(DeliveryService.class);

        if (serviceRef != null) {
            DeliveryService service = context.getService(serviceRef);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n1. View all deliveries");
                System.out.println("2. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    List<com.example.osgi.producer.delivery.DeliveryOrder> deliveries = service.getDeliveries();
                    if (deliveries.isEmpty()) {
                        System.out.println("No deliveries found.");
                    } else {
                        for (com.example.osgi.producer.delivery.DeliveryOrder order : deliveries) {
                            System.out.println(order);
                        }
                    }
                } else if (choice == 2) {
                    break;
                } else {
                    System.out.println("Invalid option. Try again.");
                }
            }

            context.ungetService(serviceRef);
        } else {
            System.out.println("Delivery Client: Service not found.");
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Delivery Client: Stopping...");
    }
}
