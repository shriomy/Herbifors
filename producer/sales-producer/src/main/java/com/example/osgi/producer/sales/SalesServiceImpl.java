package com.example.osgi.producer.sales;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class SalesServiceImpl implements SalesService, BundleActivator {

    private ServiceRegistration<SalesService> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Sales Producer started.");

        // Register the SalesService as an OSGi service
        registration = context.registerService(SalesService.class, this, null);
        System.out.println("SalesService registered.");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Sales Producer stopped.");

        // Unregister the service
        if (registration != null) {
            registration.unregister();
        }
    }

    @Override
    public String getOrders() {
        // Simulating a sales data retrieval
        return "Sales data";
    }
}