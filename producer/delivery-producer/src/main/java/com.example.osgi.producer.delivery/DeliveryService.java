
package com.example.osgi.producer.delivery;

import java.util.List;

public interface DeliveryService {
    void addDelivery(DeliveryOrder order);
    List<DeliveryOrder> getDeliveries();
    void updateDeliveryStatus(int deliveryId, String status);
    List<DeliveryOrder> getDeliveriesByCustomer(String customerName); // New method
    void deleteDelivery(int deliveryId); // New method

}
