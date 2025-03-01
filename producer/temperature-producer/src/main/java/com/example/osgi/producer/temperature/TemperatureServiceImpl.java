package com.example.osgi.producer.temperature;

public class TemperatureServiceImpl implements TemperatureService {
    @Override
    public float getTemperature() {
        // Simulating a temperature reading
        return 25.05F; // Example temperature
    }
}
