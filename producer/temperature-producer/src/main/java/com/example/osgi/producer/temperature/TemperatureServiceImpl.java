package com.example.osgi.producer.temperature;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class TemperatureServiceImpl implements TemperatureService, BundleActivator {

    private ServiceRegistration<TemperatureService> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Temperature Producer started.");

        TemperatureServiceImpl temperatureService = new TemperatureServiceImpl();


        // Register the TemperatureService as an OSGi service
        registration = context.registerService(TemperatureService.class, this, null);
        System.out.println("TemperatureService registered.");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Temperature Producer stopped.");

        // Unregister the service
        if (registration != null) {
            registration.unregister();
        }
    }

    @Override
    public float getTemperature() {
        // Simulating a temperature reading
        return 25.05F; // Example temperature
    }
}
