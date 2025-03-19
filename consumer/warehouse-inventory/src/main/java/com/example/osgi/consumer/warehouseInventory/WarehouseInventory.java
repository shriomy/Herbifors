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
import java.util.logging.Logger;

public class WarehouseInventory implements BundleActivator {

    private ServiceReference<HarvestTrackingService> harvestServiceReference;
    private BufferedReader reader;
    private static final Logger logger = Logger.getLogger(WarehouseInventory.class.getName());

    @Override
    public void start(BundleContext context) {
        System.out.println(" Warehouse Inventory Consumer started.");

        // Reinitialize BufferedReader to avoid closed stream issues
        reader = new BufferedReader(new InputStreamReader(System.in));

        harvestServiceReference = context.getServiceReference(HarvestTrackingService.class);
        if (harvestServiceReference != null) {
            HarvestTrackingService harvestService = context.getService(harvestServiceReference);
            if (harvestService != null) {
                displayCropData(harvestService);
                //displayMenu(harvestService);
            } else {
                System.out.println(" Failed to retrieve HarvestTrackingService.");
            }
            displayMenu(harvestService);
        } else {
            System.out.println(" HarvestTrackingService is not available.");
        }
    }

    private void displayMenu(HarvestTrackingService harvestService) {
        while (true) {
            System.out.println("\n Warehouse Inventory Menu:");
            System.out.println("1. Add a new crop");
            System.out.println("2. View all crops");
            System.out.println("3. Update a crop");
            System.out.println("4. Delete a crop");
            System.out.println("5. Sort crops");
            System.out.println("6. Get crop recommendations");
            System.out.println("7. Export crop data");
            System.out.println("8. Exit");

            Integer choice = getIntInput(" Choose an option: ");
            if (choice == null) {
                System.out.println(" Input error detected. Restarting menu...");
                continue;
            }
            try {
                switch (choice) {
                    case 1 -> addCrop(harvestService);
                    case 2 -> displayCropData(harvestService);
                    case 3 -> updateCrop(harvestService);
                    case 4 -> deleteCrop(harvestService);
                    case 5 -> sortCrops(harvestService);
                    case 6 -> recommendCrops(harvestService);
                    case 7 -> exportCropData(harvestService);
                    case 8 -> {
                        System.out.println(" Exiting Warehouse Inventory Consumer...");
                        return;
                    }
                    default -> System.out.println(" Invalid option. Please try again.");
                }
            } catch (Exception e) {
                logger.severe("Error during menu operation: " + e.getMessage());
                System.out.println("An error occurred. Please try again.");
            }
        }
    }

    private void displayCropData(HarvestTrackingService harvestService) {
        try {
            // Call the producer service to get the crop details
            Map<Integer, Crop> crops = harvestService.getCropDetails();

            if (crops.isEmpty()) {
                System.out.println("No crops available in inventory.");
            } else {
                System.out.println("\nCurrent Crop Inventory:");
                System.out.println("+----------+------------+------+----------+--------------+");
                System.out.println("| Crop ID  | Crop Name  | Qty  | Price    | Weather Type |");
                System.out.println("+----------+------------+------+----------+--------------+");

                // Print crop data
                for (Crop crop : crops.values()) {
                    System.out.printf("| %-8d | %-10s | %-4d | %-8.2f | %-12s |\n",
                            crop.getCropId(), crop.getName(), crop.getQuantity(), crop.getPrice(), crop.getWeatherType());
                }
                System.out.println("+----------+------------+------+----------+--------------+");
            }
        } catch (Exception e) {
            // Log the error and provide feedback to the user
            logger.severe("Error displaying crop data: " + e.getMessage());
            System.out.println("An error occurred while retrieving the crop data. Please try again.");
        }
    }

    private void addCrop(HarvestTrackingService harvestService) {
        int cropId = getIntInput("Enter crop ID: ");
        String name = getStringInput("Enter crop name: ");

        // Check if crop ID or name already exists
        if (isCropExists(harvestService, cropId, name)) {
            System.out.println("Error: Crop with the same ID or name already exists.");
            return;
        }

        int quantity = getIntInput("Enter quantity: ");
        double price = getDoubleInput("Enter price: ");
        String weatherType = getStringInput("Enter weather type: ");

        try {
            harvestService.addCrop(cropId, name, quantity, price, weatherType);
            System.out.println("\n Crop successfully added: " + name);
        } catch (Exception e) {
            logger.severe("\n Error adding crop: " + e.getMessage());
            System.out.println("\n An error occurred while adding the crop.");
        }
    }

    // Helper method to check if crop ID or name already exists
    private boolean isCropExists(HarvestTrackingService harvestService, int cropId, String name) {
        Map<Integer, Crop> crops = harvestService.getCropDetails(); // Get current crops
        for (Crop crop : crops.values()) {
            if (crop.getCropId() == cropId || (name != null && crop.getName().equalsIgnoreCase(name))) {
                return true; // Crop ID or name exists
            }
        }
        return false; // Crop doesn't exist
    }

    private void updateCrop(HarvestTrackingService harvestService) {
        try {
            int cropId = getIntInput("Enter crop ID to update: ");

            // Check if the crop ID exists
            if (!isCropExists(harvestService, cropId, null)) {
                // If the crop does not exist, display a message and return
                System.out.println("Error: Crop ID does not exist.");
                return; // Exit the method if the crop doesn't exist
            }

            int quantity = getIntInput("Enter new quantity: ");
            double price = getDoubleInput("Enter new price: ");
            String weatherType = getStringInput("Enter new weather type: ");

            // Call the updateCrop method from the producer
            harvestService.updateCrop(cropId, quantity, price, weatherType);

            // Success message after update
            System.out.println("Crop updated successfully.");
        } catch (NumberFormatException e) {
            // Handle input errors (non-numeric values for quantity or price)
            logger.warning("Invalid input provided for crop update: " + e.getMessage());
            System.out.println("Invalid input. Please ensure you enter correct numeric values.");
        } catch (Exception e) {
            // Catch any other unexpected exceptions
            logger.severe("Error updating crop: " + e.getMessage());
            System.out.println("An error occurred while updating the crop. Please try again.");
        }
    }

    private void deleteCrop(HarvestTrackingService harvestService) {
        try {
            int cropId = getIntInput("Enter crop ID to delete: ");

            // Check if the crop exists before deleting
            if (!isCropExists(harvestService, cropId, null)) {
                // If the crop does not exist, log the warning and notify the user
                logger.warning("Attempted to delete a crop with non-existent ID: " + cropId);
                System.out.println("Error: Crop ID does not exist.");
                return; // Exit the method if the crop doesn't exist
            }

            // Proceed to delete the crop if it exists
            harvestService.deleteCrop(cropId);
            System.out.println("Crop deleted successfully with ID: " + cropId);
        } catch (Exception e) {
            // Catch any unexpected exceptions during the deletion process
            logger.severe("Error deleting crop: " + e.getMessage());
            System.out.println("An error occurred while deleting the crop. Please try again.");
        }
    }

    private void sortCrops(HarvestTrackingService harvestService) {
        try {
            System.out.println("\nSort crops by:");
            System.out.println("1. Name");
            System.out.println("2. Price");
            System.out.println("3. Quantity");

            int choice = getIntInput("Choose sorting field: ");
            String sortBy = switch (choice) {
                case 1 -> "name";
                case 2 -> "price";
                case 3 -> "quantity";
                default -> "id"; // Default to sorting by ID if input is invalid
            };

            boolean ascending = getStringInput("Sort ascending? (y/n): ").toLowerCase().startsWith("y");

            // Fetch and display sorted crops
            harvestService.getSortedCrops(sortBy, ascending).forEach(System.out::println);
            logger.info("Crops sorted by " + sortBy + " in " + (ascending ? "ascending" : "descending") + " order.");
        } catch (Exception e) {
            // Log any error during the sorting process
            logger.severe("Error sorting crops: " + e.getMessage());
            System.out.println("An error occurred while sorting the crops. Please try again.");
        }
    }

    private void recommendCrops(HarvestTrackingService harvestService) {
        try {
            String weather = getStringInput("Enter current weather type: ");
            System.out.println("\nRecommended crops for " + weather + ":");

            // Fetch and display recommended crops
            harvestService.recommendCrops(weather).forEach(crop -> System.out.println("- " + crop));
            logger.info("Crops recommended for weather type: " + weather);
        } catch (Exception e) {
            // Log any error during the recommendation process
            logger.severe("Error recommending crops: " + e.getMessage());
            System.out.println("An error occurred while recommending crops. Please try again.");
        }
    }

    private void exportCropData(HarvestTrackingService harvestService) {
        try {
            String filePath = getStringInput("Enter export file path (e.g., crops.csv): ");

            // Attempt to export the crop data to a CSV file
            harvestService.exportCropDataToCSV(filePath);
            logger.info("Crop data exported successfully to: " + filePath);
            System.out.println("Crop data exported successfully.");
        } catch (RuntimeException e) {
            // Log any error during the export process and show user-friendly message
            logger.severe("Error exporting crop data: " + e.getMessage());
            System.out.println("An error occurred while exporting the crop data. Please check the file path and try again.");
        } catch (Exception e) {
            // Log any error during the export process
            logger.severe("Error exporting crop data: " + e.getMessage());
            System.out.println("An error occurred while exporting the crop data. Please try again.");
        }
    }


    @Override
    public void stop(BundleContext context) {
        System.out.println(" Warehouse Inventory Consumer stopped.");
        if (harvestServiceReference != null) {
            context.ungetService(harvestServiceReference);
        }
        reader = null;  // Don't close System.in, just clear reference
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        try {
            return reader.readLine();
        } catch (Exception e) {
            logger.warning("Invalid input: " + e.getMessage());
            return "";  // Default value or you can prompt again
        }
    }

    private Integer getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = reader.readLine().trim();  // Read input and trim any extra whitespace
                if (input.isEmpty()) {
                    System.out.println("Input cannot be empty. Please enter a valid number.");
                    continue;
                }
                return Integer.parseInt(input);  // Attempt to parse the input as an integer
            } catch (IOException e) {
                System.out.println("Error reading input. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private double getDoubleInput(String prompt) {
        System.out.print(prompt);
        try {
            return Double.parseDouble(reader.readLine());
        } catch (Exception e) {
            logger.warning("Invalid input: " + e.getMessage());
            return 0.0;  // Default value or you can prompt again
        }
    }
}
