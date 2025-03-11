
package com.example.osgi.producer.delivery;

import java.util.List;

public interface DeliveryService {
    void addDelivery(DeliveryOrder order);
    List<DeliveryOrder> getDeliveries();
}
