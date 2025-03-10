package com.example.osgi.consumer.sales;

import com.example.osgi.producer.sales.Order;
import com.example.osgi.producer.sales.SalesService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.List;

public class SalesConsumerService implements BundleActivator {

    private BundleContext context;
    private ServiceReference<SalesService> salesServiceReference;

    @Override
    public void start(BundleContext context) {
        this.context = context;
        System.out.println("Sales Consumer started.");

        // Consume sales data
        consumeSales();
    }

    @Override
    public void stop(BundleContext context) {
        System.out.println("Sales Consumer stopped.");
        if (salesServiceReference != null) {
            context.ungetService(salesServiceReference);
        }
    }

    public void consumeSales() {
        salesServiceReference = context.getServiceReference(SalesService.class);
        if (salesServiceReference != null) {
            SalesService salesService = context.getService(salesServiceReference);
            if (salesService != null) {
                System.out.println("✅ SalesService found! Adding a test order...");

                // Add a new order before fetching
                Order testOrder = new Order("Alice", "Wheat", 50, 20.5, 25.0, "Farm A");
                salesService.addOrder(testOrder);

                // Fetch and print orders from the database
                System.out.println("\n📄 Fetching all orders from the database...");
                List<Order> orders = salesService.getOrders();
                if (orders.isEmpty()) {
                    System.out.println("❌ No orders found.");
                } else {
                    orders.forEach(System.out::println);
                }
            } else {
                System.out.println("❌ Failed to retrieve SalesService.");
            }
        } else {
            System.out.println("❌ SalesService is not available.");
        }
    }
}
