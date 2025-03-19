package com.example.osgi.consumer.soilmoisture;

import com.example.osgi.producer.soilmoisture.SoilMoistureService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;


public class SoilMoistureConsumer implements BundleActivator, EventHandler {

    private BundleContext context;
    private ServiceReference<SoilMoistureService> soilMoistureServiceReference;
    private SoilMoistureService soilMoistureService;
    private boolean waterPumpOn = false;

    @Override
    public void start(BundleContext context) throws Exception {

        this.context = context;
        System.out.println("Soil Moisture Consumer started.");

        testConsumeSoilMoisture();  // test start consuming the service

        // Register event handler for temperature updates
        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put(EventConstants.EVENT_TOPIC, "com/example/soilMoisture/update");
        context.registerService(EventHandler.class.getName(), this, properties);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Sending request to stop water pump due to stopping the consumer...");
        sendWaterPumpControlRequest(false);
        System.out.println("Soil Moisture Consumer stopped.");
        if (soilMoistureServiceReference != null) {
            context.ungetService(soilMoistureServiceReference);
        }
    }

    @Override
    public void handleEvent(Event event) {
        // Check if the event is the soil moisture update event
        if ("com/example/soilMoisture/update".equals(event.getTopic())) {
            // Extract the soil moisture from the event
            float soilMoisture = (float) event.getProperty("soilMoisture");
            // Call the method to consume the soil moisture data
            consumeSoilMoisture(soilMoisture);

            if (soilMoisture > 15.0) {
                System.out.println("Soil Moisture level within threshold.");
                if(waterPumpOn){
                    System.out.println("Sending request to stop water pump...");
                    sendWaterPumpControlRequest(false);
                }
            } else {
                System.out.println("Soil Moisture level lower than threshold.");
                if(!waterPumpOn){
                    System.out.println("Sending request to turn on water pump......");
                    sendWaterPumpControlRequest(true);
                }
            }
        }
    }

    public void consumeSoilMoisture(float soilMoisture) {
        System.out.println("Event: Consumed Soil Moister : " + soilMoisture + "°%");
    }

    public void testConsumeSoilMoisture() {
        soilMoistureServiceReference = context.getServiceReference(SoilMoistureService.class);

        if (soilMoistureServiceReference != null) {
            soilMoistureService = context.getService(soilMoistureServiceReference);
            if (soilMoistureService != null) {
                float currentSoilMoisture = soilMoistureService.getSoilMoisture();
                System.out.println("Test: Consumed Soil Moisture Level: " + currentSoilMoisture + "%");

                // Release the service after use
                context.ungetService(soilMoistureServiceReference);

            } else {
                System.out.println("Failed to retrieve Soil Moisture service.");
            }
        }
    }


    //simulated method
//    public void sendWaterPumpControlRequest(boolean turnOn) {
//        // Simulated ESP32 URL (not used in fake mode)
//        String esp32Url = "http://192.168.8.130/" + (turnOn ? "start-pump" : "stop-pump");
//
//        // Simulating a successful request
//        System.out.println("Sending water pump control request to " + esp32Url);
//        System.out.println("Water pump control request sent successfully.");
//
//        // Update the fan state as if the request was successful
//        waterPumpOn = turnOn;
//    }



    //working method
    public void sendWaterPumpControlRequest(boolean turnOn) {
        //esp32 http endpoint (with ip address)
        String esp32Url = "http://192.168.8.130/" + (turnOn ? "start-pump" : "stop-pump");

        try {
            URL url = new URL(esp32Url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                System.out.println("Water Pump control request sent successfully.");
                waterPumpOn = turnOn;
            } else {
                System.out.println("Failed to send Water Pump control request. Response code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            System.out.println("Error sending Water Pump control request: " + e.getMessage());
        }
    }
}
