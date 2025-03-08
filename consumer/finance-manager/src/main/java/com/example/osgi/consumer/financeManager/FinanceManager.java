/*package com.example.osgi.consumer.financeManager;


import com.example.osgi.producer.harvestTracker.HarvestTrackingService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Map;

public class FinanceManager implements BundleActivator {
    private ServiceReference<HarvestTrackingService> harvestRef;

    @Override
    public void start(BundleContext context) {
        harvestRef = context.getServiceReference(HarvestTrackingService.class);
        if (harvestRef != null) {
            HarvestTrackingService harvestService = context.getService(harvestRef);
            System.out.println("💰 Finance Manager Started");

            // Fetch crop details
            Map<Integer, String> crops = harvestService.getCropDetails();
            System.out.println("🌾 Current Crops: " + crops);

            // Calculate ROI and revenue for each crop
            for (Map.Entry<Integer, String> entry : crops.entrySet()) {
                int cropId = entry.getKey();
                double roi = harvestService.calculateROI(cropId);
                double revenue = harvestService.calculateRevenue(cropId);
                System.out.println("💵 Crop ID " + cropId + " - ROI: $" + roi + ", Revenue: $" + revenue);
            }

            // Calculate total revenue
            double totalRevenue = harvestService.calculateTotalRevenue();
            System.out.println("💰 Total Revenue: $" + totalRevenue);
        } else {
            System.out.println("❌ Harvest Service Not Found");
        }
    }

    @Override
    public void stop(BundleContext context) {
        System.out.println("🛑 Finance Manager Stopped");
        if (harvestRef != null) {
            context.ungetService(harvestRef);
        }
    }
}

 */
