package com.example.osgi.soilMoisture;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SoilMoistureServiceImpl implements BundleActivator, SoilMoistureService {

        private ServiceRegistration<SoilMoistureService> registration;
        private EventAdmin eventAdmin;
        private volatile boolean running = true;  // Flag to control the loop
        private final Random random = new Random();

        @Override
        public void start(BundleContext context) throws Exception {
            System.out.println("SoilMoisture Producer started.");

            // Get the EventAdmin service
            ServiceReference<EventAdmin> eventAdminRef = context.getServiceReference(EventAdmin.class);
            if (eventAdminRef != null) {
                eventAdmin = context.getService(eventAdminRef);
                System.out.println("EventAdmin service retrieved.");
                simulateSoilMoistureUpdates();
            } else {
                System.out.println("EventAdmin service not found.");
            }

            // Register the TemperatureService as an OSGi service (register the actual service instance)
            registration = context.registerService(SoilMoistureService.class, this, null);
            System.out.println("SoilMoisture Service registered.");
        }


        @Override
        public void stop(BundleContext context) throws Exception {
            System.out.println("SoilMoisture Producer stopped.");

            running = false; // stopping the temperature thread


            // Unregister the service
            if (registration != null) {
                registration.unregister();
            }
        }

        @Override
        public float getSoilMoisture() {
            return 20.0F + random.nextFloat() * (40.0F - 20.0F);
        }

        // simulate soil moisture updates and publish events
        private void simulateSoilMoistureUpdates() {
            // Simulate a new SoilMoisture level every 5 seconds
            new Thread(() -> {
                while (running) {
                    try {
                        Thread.sleep(5000); // Simulate delay between updates
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!running) {
                        break;
                    }

                    float currentSoilMoisture = getSoilMoisture();

                    // Create event with the new temperature
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("soilMoisture", currentSoilMoisture);
                    Event soilMoistureEvent = new Event("com/example/soilMoisture/update", properties);

                    // Publish the event using EventAdmin
                    eventAdmin.postEvent(soilMoistureEvent);
                }
            }).start();
        }

    }



