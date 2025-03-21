package com.example.osgi.producer.temperature;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TemperatureServiceImpl implements TemperatureService, BundleActivator {

    private ServiceRegistration<TemperatureService> registration;
    private EventAdmin eventAdmin;
    private volatile boolean running = true;  // Flag to control the loop
    private final Random random = new Random();

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Temperature Producer started.");

        // Get the EventAdmin service
        ServiceReference<EventAdmin> eventAdminRef = context.getServiceReference(EventAdmin.class);
        if (eventAdminRef != null) {
            eventAdmin = context.getService(eventAdminRef);
            System.out.println("EventAdmin service retrieved.");
            temperatureUpdateThread();
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

        running = false; // stopping the temperature thread


        // Unregister the service
        if (registration != null) {
            registration.unregister();
        }
    }

    @Override
    public float getTemperature() {
        return 20.0F + random.nextFloat() * (40.0F - 20.0F);
    }

    // fetch temperature updates and publish events
    private void temperatureUpdateThread() {
        // request  temperature every 2 seconds
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(5000); //delay between updates
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!running) {
                    break;
                }

                float currentTemperature = fetchSensorData();

                if(currentTemperature != -999f){
                    // Create event with the new temperature
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("temperature", currentTemperature);
                    Event temperatureEvent = new Event("com/example/temperature/update", properties);

                    // Publish the event using EventAdmin
                    eventAdmin.postEvent(temperatureEvent);
                }
            }
        }).start();
    }

    public float fetchSensorData() {
        String esp32Url = "http://192.168.8.130/sensor-temperature";

        try {
            URL url = new URL(esp32Url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String response = in.readLine();
                    if (response != null) {
                        return Float.parseFloat(response.trim());
                    }
                }
            } else {
                System.out.println("Failed to fetch data. Response code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error fetching sensor data: " + e.getMessage());
        }
        return -999f; // Return -999 as an indication of failure
    }

}
