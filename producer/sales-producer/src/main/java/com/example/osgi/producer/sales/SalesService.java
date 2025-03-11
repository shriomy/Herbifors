package com.example.osgi.producer.sales;

import java.util.List;

public interface SalesService {
    void addOrder(Order order);
    List<Order> getOrders();
    void updateOrder(int orderId, Order order);
    void deleteOrder(int orderId);
    double getTotalManufacturingCost();
    double getTotalSellingCost();
}
