package com.example.osgi.producer.harvestTracker;
import java.util.List;
import java.util.Map;

public interface HarvestTrackingService {
    Map<Integer, String> getCropDetails();
    void addCrop(String name, int quantity, double price, String weatherType, int cropId);
    void updateCrop(int cropId, int quantity, double price, String weatherType);
    void deleteCrop(int cropId);
    Map<Integer, String> getSortedCrops(String sortBy, boolean ascending);
    List<String> recommendCrops(String currentWeather);
    void exportCropDataToCSV(String filePath);
}
