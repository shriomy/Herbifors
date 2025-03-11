package com.example.osgi.producer.delivery;

import java.io.Serializable;

public class DeliveryOrder implements Serializable {

    private int id;  // Auto-generated in the database
    private String customer;
    private String address;
    private String item;
    private String deliveryDate;
    private String status;

    public DeliveryOrder(String customer, String address, String item, String deliveryDate, String status) {
        this.customer = customer;
        this.address = address;
        this.item = item;
        this.deliveryDate = deliveryDate;
        this.status = status;
    }

    // Constructor WITH id (Used when fetching from the database)
    public DeliveryOrder(int id, String customer, String address, String item, String deliveryDate, String status) {
        this.id = id;
        this.customer = customer;
        this.address = address;
        this.item = item;
        this.deliveryDate = deliveryDate;
        this.status = status;
    }

    // Getters (No setter for `id` since it's auto-generated)
    public int getId() { return id; }

    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "DeliveryOrder{" +
                "id=" + id +
                ", customer='" + customer + '\'' +
                ", address='" + address + '\'' +
                ", item='" + item + '\'' +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
