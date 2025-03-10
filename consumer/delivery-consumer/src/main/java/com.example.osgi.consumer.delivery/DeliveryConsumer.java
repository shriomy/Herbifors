package com.example.osgi.consumer.delivery;

import com.example.osgi.producer.delivery.DeliveryService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Scanner;

public class DeliveryConsumer implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Delivery Client: Looking for Delivery Service...");

        // Get the combined service
        ServiceReference<DeliveryService> serviceRef = context.getServiceReference(DeliveryService.class);

        if (serviceRef != null) {
            DeliveryService service = context.getService(serviceRef);

            Scanner scanner = new Scanner(System.in);
            String trackingId;

            while (true) {
                System.out.println("Enter Order ID to check delivery status (or -1 to exit):");
                trackingId = scanner.nextLine();

                if ("-1".equals(trackingId)) {
                    break;
                }

                String status = service.getDeliveryStatus(trackingId);

                System.out.println("Delivery Status: " + status);
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
