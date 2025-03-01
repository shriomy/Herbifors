package com.example.osgi.producer.temperature;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
    private ServiceRegistration<?> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Temperature Producer started.");

        // Register the TemperatureService as an OSGi service
        TemperatureService temperatureService = new TemperatureServiceImpl();
        registration = context.registerService(TemperatureService.class.getName(), temperatureService, null);
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
}
