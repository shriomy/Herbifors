package com.example.osgi.consumer.warehouseInventory;

import com.example.osgi.producer.harvestTracker.Crop;
import com.example.osgi.producer.harvestTracker.HarvestTrackingService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class WarehouseInventory implements BundleActivator {

    private ServiceReference<HarvestTrackingService> harvestServiceReference;
    private BufferedReader reader;

    @Override
    public void start(BundleContext context) {
        System.out.println("📦 Warehouse Inventory Consumer started.");

        // Reinitialize BufferedReader to avoid closed stream issues
        reader = new BufferedReader(new InputStreamReader(System.in));

        harvestServiceReference = context.getServiceReference(HarvestTrackingService.class);
        if (harvestServiceReference != null) {
            HarvestTrackingService harvestService = context.getService(harvestServiceReference);
            if (harvestService != null) {
                displayCropData(harvestService);
                displayMenu(harvestService);
            } else {
                System.out.println("❌ Failed to retrieve HarvestTrackingService.");
            }
        } else {
            System.out.println("❌ HarvestTrackingService is not available.");
        }
    }

    private void displayMenu(HarvestTrackingService harvestService) {
        while (true) {
            System.out.println("\n📌 Warehouse Inventory Menu:");
            System.out.println("1. Add a new crop");
            System.out.println("2. View all crops");
            System.out.println("3. Update a crop");
            System.out.println("4. Delete a crop");
            System.out.println("5. Sort crops");
            System.out.println("6. Get crop recommendations");
            System.out.println("7. Export crop data");
            System.out.println("8. Exit");

            Integer choice = getIntInput("➡️ Choose an option: ");
            if (choice == null) {
                System.out.println("⚠️ Input error detected. Restarting menu...");
                continue;
            }
            switch (choice) {
                case 1 -> addCrop(harvestService);
                case 2 -> displayCropData(harvestService);
                case 3 -> updateCrop(harvestService);
                case 4 -> deleteCrop(harvestService);
                case 5 -> sortCrops(harvestService);
                case 6 -> recommendCrops(harvestService);
                case 7 -> exportCropData(harvestService);
                case 8 -> {
                    System.out.println("👋 Exiting Warehouse Inventory Consumer...");
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        System.out.println("❌ Error closing reader: " + e.getMessage());
                    }
                    return;
                }
                default -> System.out.println("❌ Invalid option. Please try again.");
            }
        }
    }

    private void displayCropData(HarvestTrackingService harvestService) {
        Map<Integer, Crop> crops = harvestService.getCropDetails();
        if (crops.isEmpty()) {
            System.out.println("⚠️ No crops available in inventory.");
            return;
        }

        System.out.println("\n🌾 Current Crop Inventory:");
        System.out.println("+------------+------+----------+--------------+");
        System.out.println("| Crop Name  | Qty  | Price    | Weather Type |");
        System.out.println("+------------+------+----------+--------------+");
        for (Crop crop : crops.values()) {
            System.out.printf("| %-10s | %-4d | %-8.2f | %-12s |\n",
                    crop.getName(), crop.getQuantity(), crop.getPrice(), crop.getWeatherType());
        }
        System.out.println("+------------+------+----------+--------------+");
    }

    private void addCrop(HarvestTrackingService harvestService) {
        int cropId = getIntInput("Enter crop ID: ");
        String name = getStringInput("Enter crop name: ");
        int quantity = getIntInput("Enter quantity: ");
        double price = getDoubleInput("Enter price: ");
        String weatherType = getStringInput("Enter weather type: ");

        harvestService.addCrop(cropId, name, quantity, price, weatherType);
        System.out.println("✅ Crop added successfully!");
    }

    private void updateCrop(HarvestTrackingService harvestService) {
        int cropId = getIntInput("Enter crop ID to update: ");
        int quantity = getIntInput("Enter new quantity: ");
        double price = getDoubleInput("Enter new price: ");
        String weatherType = getStringInput("Enter new weather type: ");

        harvestService.updateCrop(cropId, quantity, price, weatherType);
        System.out.println("✅ Crop updated successfully!");
    }

    private void deleteCrop(HarvestTrackingService harvestService) {
        int cropId = getIntInput("Enter crop ID to delete: ");
        harvestService.deleteCrop(cropId);
        System.out.println("✅ Crop deleted successfully!");
    }

    private void sortCrops(HarvestTrackingService harvestService) {
        System.out.println("\nSort crops by:");
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

    private void recommendCrops(HarvestTrackingService harvestService) {
        String weather = getStringInput("Enter current weather type: ");
        System.out.println("\nRecommended crops for " + weather + ":");
        harvestService.recommendCrops(weather).forEach(crop -> System.out.println("- " + crop));
    }

    private void exportCropData(HarvestTrackingService harvestService) {
        String filePath = getStringInput("Enter export file path (e.g., crops.csv): ");
        harvestService.exportCropDataToCSV(filePath);
        System.out.println("✅ Crop data exported successfully to " + filePath);
    }

    @Override
    public void stop(BundleContext context) {
        System.out.println("📦 Warehouse Inventory Consumer stopped.");
        if (harvestServiceReference != null) {
            context.ungetService(harvestServiceReference);
        }
        reader = null;  // Don't close System.in, just clear reference
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        try {
            String input = reader.readLine();
            return (input == null || input.isEmpty()) ? "N/A" : input.trim();
        } catch (IOException e) {
            System.out.println("⚠️ Error reading input: " + e.getMessage());
            return "N/A";
        }
    }

    private Integer getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = reader.readLine();
                if (input == null) {
                    System.out.println("⚠️ Input error detected. Restarting menu...");
                    return null;
                }
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException | IOException e) {
                System.out.println("❌ Please enter a valid number.");
            }
        }
    }

    private Double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = reader.readLine();
                if (input == null) {
                    System.out.println("⚠️ Input error detected. Restarting menu...");
                    return null;
                }
                return Double.parseDouble(input.trim());
            } catch (NumberFormatException | IOException e) {
                System.out.println("❌ Please enter a valid decimal number.");
            }
        }
    }
}
