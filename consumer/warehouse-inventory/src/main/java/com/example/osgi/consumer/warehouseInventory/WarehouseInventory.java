package com.example.osgi.consumer.warehouseInventory;

import com.example.osgi.producer.harvestTracker.Crop;
import com.example.osgi.producer.harvestTracker.HarvestTrackingService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Map;

public class WarehouseInventory implements BundleActivator {

    private ServiceReference<HarvestTrackingService> harvestServiceReference;
    private final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void start(BundleContext context) {
        System.out.println("Warehouse Inventory Consumer started.");

        harvestServiceReference = context.getServiceReference(HarvestTrackingService.class);
        if (harvestServiceReference != null) {
            HarvestTrackingService harvestService = context.getService(harvestServiceReference);
            if (harvestService != null) {
                harvestService.displayCropData();
                new Thread(() -> displayMenu(harvestService)).start();
            } else {
                System.out.println("Failed to retrieve HarvestTrackingService.");
            }
        } else {
            System.out.println("HarvestTrackingService is not available.");
        }
    }

    private void displayMenu(HarvestTrackingService harvestService) {
        while (true) {
            System.out.println("\nWarehouse Inventory Menu:");
            System.out.println(" 1. Add a new crop");
            System.out.println(" 2. View all crops");
            System.out.println(" 3. Update a crop");
            System.out.println(" 4. Delete a crop");
            System.out.println(" 5. Sort crops");
            System.out.println(" 6. Get crop recommendations");
            System.out.println(" 7. Export crop data");
            System.out.println(" 8. Exit");

            int choice = getIntInput(" Choose an option: ");

            switch (choice) {
                case 1 -> addCropThroughConsumer(harvestService);
                case 2 -> harvestService.displayCropData();
                case 3 -> updateCrop(harvestService);
                case 4 -> deleteCrop(harvestService);
                case 5 -> sortCrops(harvestService);
                case 6 -> getCropRecommendations(harvestService);
                case 7 -> exportCropData(harvestService);
                case 8 -> {
                    System.out.println(" Exiting Warehouse Inventory Consumer...");
                    return;
                }
                default -> System.out.println(" Invalid option. Please try again.");
            }
        }
    }

    private void addCropThroughConsumer(HarvestTrackingService harvestService) {
        System.out.println("\n Enter crop details:");
        int cropId = getIntInput("Enter crop ID: ");
        String name = getStringInput("Enter crop name: ");
        int quantity = getIntInput("Enter quantity: ");
        double price = getDoubleInput("Enter price: ");
        String weatherType = getStringInput("Enter weather type: ");

        harvestService.addCrop(cropId, name, quantity, price, weatherType);
    }

    private void updateCrop(HarvestTrackingService harvestService) {
        System.out.println("\n Update a crop:");
        int cropId = getIntInput("Enter crop ID to update: ");

        Map<Integer, Crop> crops = harvestService.getCropDetails();
        if (!crops.containsKey(cropId)) {
            System.out.println(" Crop not found with ID: " + cropId);
            return;
        }

        int quantity = getIntInput("Enter new quantity: ");
        double price = getDoubleInput("Enter new price: ");
        String weatherType = getStringInput("Enter new weather type: ");

        harvestService.updateCrop(cropId, quantity, price, weatherType);
    }

    private void sortCrops(HarvestTrackingService harvestService) {
        System.out.println("\n Sort crops by:");
        System.out.println("1. Name");
        System.out.println("2. Price");
        System.out.println("3. Quantity");

        int choice = getIntInput("Choose sorting field: ");
        String sortBy = switch (choice) {
            case 1 -> "name";
            case 2 -> "price";
            case 3 -> "quantity";
            default -> "id";
        };

        boolean ascending = getStringInput("Sort ascending? (y/n): ").toLowerCase().startsWith("y");
        harvestService.getSortedCrops(sortBy, ascending).forEach(System.out::println);
    }

    private void getCropRecommendations(HarvestTrackingService harvestService) {
        String weather = getStringInput("\n Enter current weather type: ");
        System.out.println("\nRecommended crops for " + weather + ":");
        harvestService.recommendCrops(weather).forEach(crop -> System.out.println("- " + crop));
    }

    private void exportCropData(HarvestTrackingService harvestService) {
        String filePath = getStringInput("\n Enter export file path (e.g., crops.csv): ");
        harvestService.exportCropDataToCSV(filePath);
    }

    private void deleteCrop(HarvestTrackingService harvestService) {
        System.out.println("\n🗑️ Delete a crop:");
        int cropId = getIntInput("Enter crop ID to delete: ");
        harvestService.deleteCrop(cropId);
    }

    // ... existing helper methods (getStringInput, getIntInput, getDoubleInput) remain the same ...

    @Override
    public void stop(BundleContext context) {
        System.out.println(" Warehouse Inventory Consumer stopped.");
        if (harvestServiceReference != null) {
            context.ungetService(harvestServiceReference);
        }
        try {
            reader.close();
        } catch (IOException e) {
            System.out.println(" Error closing reader: " + e.getMessage());
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        try {
            return reader.readLine().trim();
        } catch (IOException e) {
            System.out.println(" Error reading input: " + e.getMessage());
            return "";
        }
    }

    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(reader.readLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(" Please enter a valid number.");
            } catch (IOException e) {
                System.out.println(" Error reading input: " + e.getMessage());
                return 0;
            }
        }
    }

    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(reader.readLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(" Please enter a valid decimal number.");
            } catch (IOException e) {
                System.out.println(" Error reading input: " + e.getMessage());
                return 0.0;
            }
        }
    }
}