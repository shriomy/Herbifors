package com.example.osgi.consumer.temperature;

import com.example.osgi.producer.temperature.TemperatureService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class TemperatureConsumerService {

    private BundleContext context;
    private ServiceReference<TemperatureService> temperatureServiceReference;
    private TemperatureService temperatureService;

    public TemperatureConsumerService(BundleContext context) {
        this.context = context;
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
    public void stop() {
        if (temperatureServiceReference != null) {
            context.ungetService(temperatureServiceReference);
        }
    }
}
