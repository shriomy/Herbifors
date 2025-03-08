package com.example.osgi.producer.temperature;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TemperatureServiceImpl implements TemperatureService, BundleActivator {

    private ServiceRegistration<TemperatureService> registration;
    private EventAdmin eventAdmin;


    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Temperature Producer started.");

        // Create an instance of the TemperatureServiceImpl
        //TemperatureServiceImpl temperatureService = new TemperatureServiceImpl();

        // Get the EventAdmin service (check if the service is available first)
        ServiceReference<EventAdmin> eventAdminRef = context.getServiceReference(EventAdmin.class);
        if (eventAdminRef != null) {
            eventAdmin = context.getService(eventAdminRef);
            System.out.println("EventAdmin service retrieved.");
            simulateTemperatureUpdates();
        } else {
            System.out.println("EventAdmin service not found.");
        }

        // Register the TemperatureService as an OSGi service (register the actual service instance)
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

    private final Random random = new Random();


    @Override
    public float getTemperature() {
        // Simulating a temperature reading
        return 20.0F + random.nextFloat() * (40.0F - 20.0F);
    }

    // to simulate temperature updates and publish events
    private void simulateTemperatureUpdates() {
        // Simulate a new temperature every 5 seconds
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000); // Simulate delay between updates
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                float currentTemperature = getTemperature();

                // Create event with the new temperature
                Map<String, Object> properties = new HashMap<>();
                properties.put("temperature", currentTemperature);
                Event temperatureEvent = new Event("com/example/temperature/update", properties);

                // Publish the event using EventAdmin
                eventAdmin.postEvent(temperatureEvent);
                //System.out.println("Published temperature: " + currentTemperature + "°C");
            }
        }).start();
    }
}
