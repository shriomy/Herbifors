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

        // Fetch and print orders from the database
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
                System.out.println("Fetching orders from database...");
                List<Order> orders = salesService.getOrders();
                orders.forEach(System.out::println);
            } else {
                System.out.println("Failed to retrieve SalesService.");
            }
        } else {
            System.out.println("SalesService is not available.");
        }
    }
}
