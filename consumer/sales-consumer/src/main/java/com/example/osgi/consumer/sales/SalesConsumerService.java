package com.example.osgi.consumer.sales;

import com.example.osgi.producer.sales.SalesService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class SalesConsumerService implements BundleActivator, EventHandler {

    private BundleContext context;
    private ServiceReference<SalesService> salesServiceReference;
    private SalesService salesService;

    @Override
    public void start(BundleContext context) throws Exception {
        this.context = context;
        System.out.println("Sales Consumer started.");
        testConsumeSales();  // test start consuming the service

        // Register this class as an event handler for the sales update events
        context.registerService(EventHandler.class.getName(), this, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Sales Consumer stopped.");
        if (salesServiceReference != null) {
            context.ungetService(salesServiceReference);
        }
    }

    @Override
    public void handleEvent(Event event) {
        // Check if the event is the sales update event
        if ("com/example/sales/update".equals(event.getTopic())) {
            // Extract the sales data from the event
            String sales = (String) event.getProperty("sales");
            System.out.println("Consumed Sales: " + sales);

            // Call the method to consume the sales data
            consumeSales(sales);
        }
    }

    public void consumeSales(String sales) {
        salesServiceReference = context.getServiceReference(SalesService.class);
        if (salesServiceReference != null) {
            salesService = context.getService(salesServiceReference);
            if (salesService != null) {
                String currentSales = salesService.getSales();
                System.out.println("Event: Consumed Sales: " + currentSales);
            } else {
                System.out.println("Failed to retrieve sales service.");
            }
        } else {
            System.out.println("Sales service is not available yet.");
        }
    }

    public void testConsumeSales() {
        salesServiceReference = context.getServiceReference(SalesService.class);
        if (salesServiceReference != null) {
            salesService = context.getService(salesServiceReference);
            if (salesService != null) {
                String currentSales = salesService.getOrders();
                System.out.println("Test: Consumed Sales: " + currentSales);
            } else {
                System.out.println("Failed to retrieve sales service.");
            }
        }
    }
}