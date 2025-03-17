package com.example.osgi.producer.sales;

import java.io.Serializable;

public class Order implements Serializable {
    private int id;
    private String customer;
    private String item;
    private int quantity;
    private double manufacturedPrice;
    private double sellingPrice;
    private String location;

    public Order(String customer, String item, int quantity, double manufacturedPrice, double sellingPrice, String location) {
        this.customer = customer;
        this.item = item;
        this.quantity = quantity;
        this.manufacturedPrice = manufacturedPrice;
        this.sellingPrice = sellingPrice;
        this.location = location;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getManufacturedPrice() {
        return manufacturedPrice;
    }

    public void setManufacturedPrice(double manufacturedPrice) {
        this.manufacturedPrice = manufacturedPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Order{" +
                "customer='" + customer + '\'' +
                ", item='" + item + '\'' +
                ", quantity=" + quantity +
                ", manufacturedPrice=" + manufacturedPrice +
                ", sellingPrice=" + sellingPrice +
                ", location='" + location + '\'' +
                '}';
    }
}
