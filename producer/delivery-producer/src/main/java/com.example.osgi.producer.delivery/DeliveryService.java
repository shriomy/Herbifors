package com.example.osgi.producer.delivery;

public interface DeliveryService {
    String getDeliveryStatus(String trackingId);
    void sendNotification(String message);
}
