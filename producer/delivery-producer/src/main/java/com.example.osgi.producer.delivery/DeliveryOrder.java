package com.example.osgi.producer.delivery;

import java.io.Serializable;

public class DeliveryOrder implements Serializable {
    private String recipient;
    private String address;
    private String item;
    private int quantity;
    private String deliveryDate;
    private String status;

    public DeliveryOrder(String recipient, String address, String item, int quantity, String deliveryDate, String status) {
        this.recipient = recipient;
        this.address = address;
        this.item = item;
        this.quantity = quantity;
        this.deliveryDate = deliveryDate;
        this.status = status;
    }

    // Getters and Setters
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "DeliveryOrder{" +
                "recipient='" + recipient + '\'' +
                ", address='" + address + '\'' +
                ", item='" + item + '\'' +
                ", quantity=" + quantity +
                ", deliveryDate='" + deliveryDate + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}