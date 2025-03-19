// Crop.java
package com.example.osgi.producer.harvestTracker;

public class Crop {
    private int cropId;
    private String name;
    private int quantity;
    private double price;
    private String weatherType;

    public Crop(int cropId, String name, int quantity, double price, String weatherType) {
        this.cropId = cropId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.weatherType = weatherType;
    }

    // Getters
    public int getCropId() { return cropId; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getWeatherType() { return weatherType; }

    // Setters
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setWeatherType(String weatherType) { this.weatherType = weatherType; }

    @Override
    public String toString() {
        return "Crop{" +
                "cropId=" + cropId +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", weatherType='" + weatherType + '\'' +
                '}';
    }
}