package com.example.osgi.consumer.warehouseInventory;

import com.example.osgi.producer.harvestTracker.HarvestTrackingService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.List;
import java.util.Map;

public class WarehouseInventory implements BundleActivator {
    private HarvestTrackingService harvestService;

    @Override
    public void start(BundleContext context) {
        ServiceReference<HarvestTrackingService> ref = context.getServiceReference(HarvestTrackingService.class);
        if (ref != null) {
            harvestService = context.getService(ref);
            System.out.println("✅ WarehouseManager connected to HarvestTrackingService.");

            Map<Integer, String> sortedCrops = harvestService.getSortedCrops("price", false);
            System.out.println("Sorted Crops by Price: " + sortedCrops);

            List<String> recommendedCrops = harvestService.recommendCrops("Sunny");
            System.out.println("🌞 Recommended Crops: " + recommendedCrops);

            harvestService.exportCropDataToCSV("crops.csv");
        }
    }

    @Override
    public void stop(BundleContext context) {
        System.out.println("🛑 WarehouseManager Stopped.");
    }
}