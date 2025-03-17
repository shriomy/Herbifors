package com.example.osgi.producer.harvestTracker;

import java.util.List;
import java.util.Map;

public interface HarvestTrackingService {
    Map<Integer, Crop> getCropDetails();
    void addCrop(int cropId, String name, int quantity, double price, String weatherType);
    void updateCrop(int cropId, int quantity, double price, String weatherType);
    void deleteCrop(int cropId);
    List<Crop> getSortedCrops(String sortBy, boolean ascending);
    List<String> recommendCrops(String currentWeather);
    void exportCropDataToCSV(String filePath);
    //void displayCropData();
    void closeConnection();
}