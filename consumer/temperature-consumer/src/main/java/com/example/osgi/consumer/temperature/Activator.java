package com.example.osgi.consumer.temperature;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private TemperatureConsumerService temperatureConsumerService;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Temperature Consumer started.");
        temperatureConsumerService = new TemperatureConsumerService(context);
        temperatureConsumerService.consumeTemperature();  // Start consuming the service
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Temperature Consumer stopped.");
        if (temperatureConsumerService != null) {
            temperatureConsumerService.stop();
        }
    }
}
