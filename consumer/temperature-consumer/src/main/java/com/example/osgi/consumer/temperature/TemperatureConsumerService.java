package com.example.osgi.consumer.temperature;

import com.example.osgi.producer.temperature.TemperatureService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;


public class TemperatureConsumerService implements BundleActivator, EventHandler {

    private BundleContext context;
    private ServiceReference<TemperatureService> temperatureServiceReference;
    private TemperatureService temperatureService;

    @Override
    public void start(BundleContext context) throws Exception {

        this.context = context;
        System.out.println("Temperature Consumer started.");
        testConsumeTemperature();  // test start consuming the service

        // Register this class as an event handler for the temperature update events
        context.registerService(EventHandler.class.getName(), this, null);
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
            System.out.println("Consumed Temperature: " + temperature + "°C");

            // Call the method to consume the temperature data
            consumeTemperature(temperature);
        }
    }

    public void consumeTemperature(float temperature) {
        temperatureServiceReference = context.getServiceReference(TemperatureService.class);
        if (temperatureServiceReference != null) {
            temperatureService = context.getService(temperatureServiceReference);
            if (temperatureService != null) {
                float currentTemperature = temperatureService.getTemperature();
                System.out.println("Event: Consumed Temperature : " + currentTemperature + "°C");
            } else {
                System.out.println("Failed to retrieve temperature service.");
            }
        } else {
            System.out.println("Temperature service is not available yet.");
        }
    }

    public void testConsumeTemperature() {
        temperatureServiceReference = context.getServiceReference(TemperatureService.class);
        if (temperatureServiceReference != null) {
            temperatureService = context.getService(temperatureServiceReference);
            if (temperatureService != null) {
                float currentTemperature = temperatureService.getTemperature();
                System.out.println("Test: Consumed Temperature: " + currentTemperature + "°C");
            } else {
                System.out.println("Failed to retrieve temperature service.");
            }
        }
    }
}
