package com.example.osgi.consumer.temperature;

import com.example.osgi.producer.temperature.TemperatureService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import java.util.Dictionary;
import java.util.Hashtable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class TemperatureConsumerService implements BundleActivator, EventHandler {

    private BundleContext context;
    private ServiceReference<TemperatureService> temperatureServiceReference;
    private TemperatureService temperatureService;
    private boolean fanOn = false;

    @Override
    public void start(BundleContext context) throws Exception {

        this.context = context;
        System.out.println("Temperature Consumer started.");

        testConsumeTemperature();  // test start consuming the service

        // Register event handler for temperature updates
        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put(EventConstants.EVENT_TOPIC, "com/example/temperature/update");
        context.registerService(EventHandler.class.getName(), this, properties);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Temperature Consumer stopped.");
        if (temperatureServiceReference != null) {
            context.ungetService(temperatureServiceReference);
        }
    }

    @Override
    public void handleEvent(Event event) {
        // Check if the event is the temperature update event
        if ("com/example/temperature/update".equals(event.getTopic())) {
            // Extract the temperature from the event
            float temperature = (float) event.getProperty("temperature");
            // Call the method to consume the temperature data
            consumeTemperature(temperature);

            if (temperature > 30.0) {
                System.out.println("Temperature too high!");
                if(!fanOn){
                    System.out.println("Sending request to start fan...");
                    sendFanControlRequest(true);
                }
            } else {
                System.out.println("Temperature normal.");
                if(fanOn){
                    System.out.println("Sending request Stopping fan......");
                    sendFanControlRequest(false);
                }
            }
        }
    }

    public void consumeTemperature(float temperature) {
        System.out.println("Event: Consumed Temperature : " + temperature + "°C");
    }

    public void testConsumeTemperature() {
       temperatureServiceReference = context.getServiceReference(TemperatureService.class);

        if (temperatureServiceReference != null) {
            temperatureService = context.getService(temperatureServiceReference);
            if (temperatureService != null) {
                float currentTemperature = temperatureService.getTemperature();
                System.out.println("Test: Consumed Temperature: " + currentTemperature + "°C");

                // Release the service after use
                context.ungetService(temperatureServiceReference);

            } else {
                System.out.println("Failed to retrieve temperature service.");
            }
        }
    }

    public void sendFanControlRequest(boolean turnOn) {
        String esp32Url = "http://192.168.8.130/" + (turnOn ? "start-fan" : "stop-fan"); // Change IP

        try {
            URL url = new URL(esp32Url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                System.out.println("Fan control request sent successfully.");
                fanOn = turnOn;
            } else {
                System.out.println("Failed to send fan control request. Response code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            System.out.println("Error sending fan control request: " + e.getMessage());
        }
    }
}
