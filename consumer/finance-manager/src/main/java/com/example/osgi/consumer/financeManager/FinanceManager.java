package com.example.osgi.consumer.financeManager;

import com.example.osgi.producer.harvestTracker.HarvestTrackingService;
import com.example.osgi.producer.sales.SalesService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import java.util.Map;

public class FinanceManager implements BundleActivator {

    private ServiceReference<SalesService> salesServiceRef;
    private SalesService salesService;

    @Override
    public void start(BundleContext context) throws Exception{
            System.out.println(" Finance Manager Consumer Started");

        // Look up the SalesService
        salesServiceRef = context.getServiceReference(SalesService.class);
        if (salesServiceRef != null) {
            salesService = context.getService(salesServiceRef);
            System.out.println(" Connected to SalesService.");

            // Calculate and display financial details
            calculateFinancialSummary();
        } else {
            System.out.println(" SalesService not found.");
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        System.out.println("Finance Manager Consumer stopped.");
        if (salesServiceRef != null) {
            context.ungetService(salesServiceRef);
        }
    }

    private void calculateFinancialSummary() {
        if (salesService != null) {
            double totalManufacturingCost = salesService.getTotalManufacturingExpenses();
            double totalSellingCost = salesService.getRevenue();
            double roi = totalSellingCost - totalManufacturingCost;
            double totalSalesRevenue = totalSellingCost; // Total revenue is the total selling cost

            System.out.println("\n Financial Summary:");
            System.out.println("+--------------------------+------------+");
            System.out.println("| Metric                   | Amount ($) |");
            System.out.println("+--------------------------+------------+");
            System.out.printf("| Total Manufacturing Cost  | %.2f      |\n", totalManufacturingCost);
            System.out.printf("| Total Selling Cost        | %.2f      |\n", totalSellingCost);
            System.out.printf("| ROI (Profit/Loss)         | %.2f      |\n", roi);
            System.out.printf("| Total Sales Revenue       | %.2f      |\n", totalSalesRevenue);
            System.out.println("+--------------------------+------------+\n");

            if (roi > 0) {
                System.out.println(" Business is profitable!");
            } else if (roi < 0) {
                System.out.println(" Business is running at a loss.");
            } else {
                System.out.println(" Break-even point reached.");
            }
        } else {
            System.out.println(" Cannot calculate financial details. SalesService is unavailable.");
        }
    }
}