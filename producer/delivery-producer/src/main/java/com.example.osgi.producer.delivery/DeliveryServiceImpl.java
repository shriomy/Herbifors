package com.example.osgi.producer.delivery;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DeliveryServiceImpl implements DeliveryService, BundleActivator {
    private ServiceRegistration<DeliveryService> serviceRegistration;
    private final Set<String> deliveredPackages = new HashSet<>();

    private final List<String> orders = List.of(
            "ORD001, John Doe, Laptop, $1000, $800, 123 Main St, NY",
            "ORD002, Jane Smith, Phone, $500, $350, 456 Elm St, CA",
            "ORD003, Alice Johnson, Tablet, $300, $200, 789 Pine St, TX",
            "ORD004, Bob Brown, Headphones, $150, $100, 321 Oak St, FL",
            "ORD005, Charlie White, Smartwatch, $250, $180, 654 Maple St, IL",
            "ORD006, Daniel Green, Monitor, $400, $320, 987 Birch St, WA",
            "ORD007, Ella Black, Keyboard, $120, $80, 741 Cedar St, CO",
            "ORD008, Frank Harris, Mouse, $60, $40, 852 Walnut St, NV",
            "ORD009, Grace Lewis, Printer, $350, $270, 963 Aspen St, AZ",
            "ORD010, Henry Scott, Speakers, $200, $150, 159 Redwood St, OR"
    );


    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Delivery Service: Starting...");

        deliveredPackages.add("ORD001");
        deliveredPackages.add("ORD002");

        serviceRegistration = context.registerService(DeliveryService.class, this, null);

        System.out.println("Delivery Service: Registered.");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Delivery Service: Stopping...");

        if (serviceRegistration != null) {
            serviceRegistration.unregister();
        }

        System.out.println("Delivery Service: Unregistered.");
    }


    @Override
    public String getDeliveryStatus(String trackingId) {
        if (deliveredPackages.contains(trackingId)) {
            return "Delivered";
        }
        return "Not delivered";
    }

    @Override
    public void sendNotification(String message) {
        System.out.println("Notification Sent: " + message);
    }
}
