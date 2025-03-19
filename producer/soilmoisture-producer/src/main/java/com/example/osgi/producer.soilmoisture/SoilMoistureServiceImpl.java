package com.example.osgi.producer.soilmoisture;

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
            soilMoistureUpdateThread();
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

    //  soil moisture updates and publish events
    private void soilMoistureUpdateThread() {
        // Request  SoilMoisture level every 2 seconds
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(2000); // delay between updates
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!running) {
                    break;
                }

                float currentSoilMoisture = fetchSensorData();
                if(currentSoilMoisture != -999f) {
                    // Create event with the new temperature
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("soilMoisture", currentSoilMoisture);
                    Event soilMoistureEvent = new Event("com/example/soilMoisture/update", properties);

                    // Publish the event using EventAdmin
                    eventAdmin.postEvent(soilMoistureEvent);
                }
            }
        }).start();
    }

    public float fetchSensorData() {
        String esp32Url = "http://192.168.8.130/sensor-moisture";

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
