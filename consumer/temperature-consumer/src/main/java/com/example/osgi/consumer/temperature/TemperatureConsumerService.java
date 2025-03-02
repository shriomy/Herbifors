package com.example.osgi.consumer.temperature;

import com.example.osgi.producer.temperature.TemperatureService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class TemperatureConsumerService implements BundleActivator {

    private BundleContext context;
    private ServiceReference<TemperatureService> temperatureServiceReference;
    private TemperatureService temperatureService;

    @Override
    public void start(BundleContext context) throws Exception {

        this.context = context;
        System.out.println("Temperature Consumer started.");
        consumeTemperature();  // Start consuming the service
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Temperature Consumer stopped.");
        if (temperatureServiceReference != null) {
            context.ungetService(temperatureServiceReference);
        }
    }

    public void consumeTemperature() {
        temperatureServiceReference = context.getServiceReference(TemperatureService.class);
        if (temperatureServiceReference != null) {
            temperatureService = context.getService(temperatureServiceReference);
            if (temperatureService != null) {
                float currentTemperature = temperatureService.getTemperature();
                System.out.println("Consumed Temperature: " + currentTemperature + "°C");
            } else {
                System.out.println("Failed to retrieve temperature service.");
            }
        }
    }
}
